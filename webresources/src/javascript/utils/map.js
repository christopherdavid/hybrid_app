/**
 * MapCanvas is the class for the map view which contains also a canvas layer
 * @class Represents the MapCanvas control
 * @param {object} viewmodel Reference to the current view model.
 */
function MapCanvas(viewmodel) {
    var that = this;
    var viewScroll;
    // reference to geography data in view model
    var geoData;

    var canvas, $canvas, context, width, height, posX = 0, posY = 0;
    var canvasView, $canvasView, contextView, posViewX, posViewY;
    var degree = 0;
    var visualPadding = 10;
    var viewX = visualPadding, viewY = visualPadding, viewXOrg, viewYOrg;
    var $viewport;
    var roomMaxX = 0;
    var roomMaxY = 0;
    var verticalScrolling = false;
    var horizontalScrolling = false;
    // collection of rooms which were selected from user
    var selectedRooms = [];
    var selectedArea;
    var $gridArea;
    var $selectionArea;

    var gridX, gridY, gridW, gridH;
    var gridImg = new Image();
    var gridPattern;
    var nogoImg = new Image();
    var nogoPattern;
    var categoryIcon = new Image();
    var iconSize = {
        width : 80,
        height : 80,
        targetWidth : 40,
        targetHeight: 40
    }
    // initial default size of selection area
    var selectionArea = {
        defWidth : 80,
        defHeight : 80
    }
    var state;
    var lastState;
    this.STATES = {
        GRID : 0,
        SPOT : 1,
        AREA : 2,
        CUT : 3,
        MOVEAREA: 4, // roboter is moving, room selection disabled
        MOVESPOT: 5  // roboter is moving, spot resizing disabled
    }
    this.LAYERFLAG = {
        ROOM:1,
        ICON:4,
        BASE:8,
        NOGO:16,
        SPOT:32,
        ALL:64
    }

    this.init = function(canvasDomId) {
        $viewport = $('#viewport');
        canvasView = document.getElementById(canvasDomId);
        contextView = canvasView.getContext('2d');
        $canvasView = $('#' + canvasDomId);

        $gridArea = $('#grid');
        $selectionArea = $('#selection-area');
        gridImg.src = "img/grid.png";
        nogoImg.src = "img/nogo.png";
        categoryIcon.src = "img/icons/category_40x40.png";

        $selectionArea.resizable({
            containment : "#grid",
            //grid : [5, 5],
            handles : "sw,ne,se,nw",
            minHeight : 50,
            minWidth : 50,
            start : function(event, ui) {
                // disable iScroll, to resize the selection area
                viewScroll.disable();
                $selectionArea.toggleClass("resize");
            },
            stop : function(event, ui) {
                // resize done: enable iScroll
                viewScroll.enable();
                $selectionArea.toggleClass("resize");
            }
        }).draggable({
            //containment : "#grid",
            grid : [5, 5],
            snap : "#grid",
            snapTolerance : 5,
            snapMode : "inner",
            start : function(event, ui) {
                // disable iScroll, to drag the selection area
                viewScroll.disable();
            },
             drag: function(evt,ui) {
                // zoom fix
                ui.position.top = Math.round(ui.position.top / viewScroll.scale);
                ui.position.left = Math.round(ui.position.left / viewScroll.scale);
                /*
                console.log("drag: ui.position.left " + ui.position.left + " ui.position.top " + ui.position.top
                            +"\n width " + $(this).width() + " height " + $(this).height() + " scale " + viewScroll.scale
                            +"\ngridW " + gridW  + " gridH " + gridH )
                */
                // bounding box check
                if (ui.position.left < 0) { 
                    ui.position.left = 0;
                }
                if (ui.position.left + $(this).width() > gridW) {
                    ui.position.left = gridW - $(this).width();
                }
                if (ui.position.top < 0) {
                    ui.position.top = 0;
                }
                if (ui.position.top + $(this).height() > gridH) {
                    ui.position.top = gridH - $(this).height();
                }
            },
            stop : function(event, ui) {
                // dragging done: enable iScroll
                viewScroll.enable();
            }
        });
        
        $selectionArea.on('resize', function (e) {
            e.stopPropagation(); 
        });

        $(document).on("updateLayout.mapCanvas", function(event) {
            updatePosition();
        });

        $(window).on("resize.mapCanvas", function() {
        	updatePosition();
        });

        $(document).on("orientationchange.mapCanvas", function(event) {
            updatePosition();
        });

        $(document).one("pageshow.mapCanvas", function(e) {
            initScrollView();
            updatePosition();
        });
    }
    /**
     * remove event handler and destroy objects
     */
    this.deinit = function() {
        // destroy iScroll object
        viewScroll.destroy();
        viewScroll = null;
        //geoData = null;
        // remove all event handler for mapCanvas
        $(document).off(".mapCanvas");
    }
    
    this.setGeoData = function(geography) {
        // clear selected room array
        selectedRooms.length = 0;
        // set reference
        geoData = geography;
        roomMaxX = geoData.boundingBox[2];
        roomMaxY = geoData.boundingBox[3];
        // TODO: temporary fix: wrong server values for color and icon
        for (var i = 0, maxR = geoData.rooms.length; i < maxR; i++) {
            if(isNaN(geoData.rooms[i].color)) {
                geoData.rooms[i].color = 1;
            }
            if(isNaN(geoData.rooms[i].icon)) {
                geoData.rooms[i].icon = 0;
            }
        }
        updatePosition();
    };
    
    this.setGridImage = function(img) {
        console.log("setGridImage " + img);
        $canvasView.css("background-image","url("+ img +")");
    }
    function initScrollView() {
        viewScroll = new iScroll('viewport', {
            lockDirection : false,
            zoom : true,
            zoomMax : 2,
            hideScrollbar : true,
            fadeScrollbar : true,

            onZoomEnd : function() {
                //set scale for resizable
                $selectionArea.resizable("setScale",viewScroll.scale);
            },
            onTouchEnd : function(e) {
                var point = e.changedTouches ? e.changedTouches[0] : e;
                if (!viewScroll.moved) {
                    var pt = {
                        x : ((point.pageX - viewScroll.x - visualPadding) / viewScroll.scale) - posX,
                        y : ((point.pageY - viewScroll.y - visualPadding) / viewScroll.scale) - posY
                    };
                    /*
                    console.log("touchend"
                     + "\nx " + viewScroll.x
                     + "\ny " + viewScroll.y
                     + "\ne.pageX " + point.pageX
                     + "\ne.pageY " + point.pageY
                     + "\nwrapperOffsetLeft " + viewScroll.wrapperOffsetLeft
                     + "\nwrapperOffsetTop " + viewScroll.wrapperOffsetTop
                     + "\nposY" + posY
                     + "\nvisualPadding " + visualPadding
                     + "\ncheck: pt.x " + pt.x + " pt.y " + pt.y)
                    */
                    if (state == that.STATES.AREA) {
                        if (checkRooms(pt)) {
                            // trigger event listener
                            //$canvasView.trigger('areaSelected', [selectedRooms]);
                            $(that).trigger('areaSelected', [selectedRooms]);
                        }
                    }
                }
            }
        });
    }

    function getGridPattern() {
        gridPattern = contextView.createPattern(gridImg, "repeat");
        getGridPattern = function() {
            return gridPattern;
        }
        return gridPattern;
    }
    function getNogoPattern() {
        nogoPattern = contextView.createPattern(nogoImg, "repeat");
        getNogoPattern = function() {
            return nogoPattern;
        }
        return nogoPattern;
    }
    

    function updatePosition() {
        if(viewScroll) {
            posY = ($('#mapOptions').outerHeight(true) + $('#mapOptions').position().top + 1||0);
            /*
            console.log("window.height " + $(window).height()
                    + "\nwindow.width " + $(window).width()
                    + "\nviewport.offset().top " + $viewport.offset().top
                    + "\nheader height " + $('#pageHeader').outerHeight()
                    + "\nfooter height " + ($('#pageFooter').outerHeight() || 0)
                    + "\nmapOptions height " + $('#mapOptions').outerHeight()
                    + "\nmapOptions height+margin " + $('#mapOptions').outerHeight(true)
                    + "\nposY " + posY);
            */
            // add 1px additional space to top
            $viewport.css({"top": posY + "px",
                           "bottom": ($('#pageFooter').outerHeight()||0) + "px"});
                    
                    
            height = $viewport.height();
            width = $viewport.width();
            if (width < roomMaxX) {
                horizontalScrolling = true;
            }
            if (height < roomMaxY) {
                verticalScrolling = true;
            }
    
            $canvasView.attr({
                width : ( horizontalScrolling ? roomMaxX : width),
                height : ( verticalScrolling ? roomMaxY : height)
            });
            console.log("updatePosition(); height " + height + " width " + width + " posY " + posY);
    
            if (geoData) {
                // need to redraw all layer because canvas has been resized which cleans everything on it
                redrawLayer(that.LAYERFLAG.ALL);
                viewScroll.refresh();
            }
        }
    }


    this.setState = function(newState) {
        if (state != newState) {
            that.leaveState(newState);
            switch(newState) {
                case that.STATES.GRID:
                    break;
                case that.STATES.SPOT:
                    $selectionArea.addClass("selectionarea-spot");
                    showSelectionArea(geoData.boundingBox);
                    break;
                case that.STATES.AREA:
                    break;
                case that.STATES.CUT:
                    $selectionArea.addClass("selectionarea-cut");
                    showSelectionArea(selectedRooms[0].boundingBox);
                    break;
                case that.STATES.MOVEAREA:
                break;
                case that.STATES.MOVESPOT:
                    selectedArea = that.getSelectionArea();
                break;
            }
            state = newState;
            redrawLayer(that.LAYERFLAG.ALL);
        }
    }
    this.leaveState = function(newState) {
        switch(state) {
            case that.STATES.GRID:
                break;
            case that.STATES.SPOT:
                $selectionArea.removeClass("selectionarea-spot");
                hideSelectionArea();
                break;
            case that.STATES.AREA:
                break;
            case that.STATES.CUT:
                $selectionArea.removeClass("selectionarea-cut");
                hideSelectionArea();
                break;
            case that.STATES.MOVEAREA:
                break;
            case that.STATES.MOVESPOT:
                break;
        }
    }
    //wipes the canvas context
    this.clear = function() {
        // just visible view
        //contextView.clearRect(0, 0, width, height);
        // complete canvas
        contextView.clearRect(0, 0, ( horizontalScrolling ? roomMaxX : width), ( verticalScrolling ? roomMaxY : height));
    }
    this.zoom = function() {
        viewScroll.zoom(viewScroll.pointX, viewScroll.pointY, viewScroll.scale == 1 ? viewScroll.options.doubleTapZoom : 1);
        $selectionArea.resizable("setScale",viewScroll.scale);
    }
    this.rotate = function() {
        degree = degree + 90;
        console.log("rotate " + degree + "°");
        $canvasView.css({
            "transform" : "rotate(" + degree + "deg)",
            "transition-duration" : "250ms",
        });
    };

    
    function showSelectionArea(boundingBox) {
        gridX = boundingBox[0];
        gridY = boundingBox[1];
        gridW = (boundingBox[2] - boundingBox[0]);
        gridH = (boundingBox[3] - boundingBox[1]);
        
        $gridArea.css({
            "left" : gridX + visualPadding + "px",
            "top" : gridY + visualPadding + "px",
            "width" : gridW + "px",
            "height" : gridH + "px"
        })

        /*
         console.log("left:"+(gridX + visualPadding) + "px"
         +"\ntop:"+ (gridY + visualPadding) + "px"
         +"\nwidth:"+ gridW + "px"
         +"\nheight:" + gridH + "px");
         */
        
        if(selectedArea) {
            $selectionArea.css({
                "left" : (selectedArea[0]-gridX) + "px",
                "top" : (selectedArea[1]-gridY) + "px",
                "width" : (selectedArea[2]-selectedArea[0]-gridX) + "px",
                "height" : (selectedArea[3]-selectedArea[1]-gridY) + "px"
            });
            selectedArea = null;
        } else {
            // centered area
            $selectionArea.css({
                "left" : (gridW / 2 - selectionArea.defWidth / 2) + "px",
                "top" : (gridH / 2 - selectionArea.defHeight / 2) + "px",
                "width" : selectionArea.defWidth + "px",
                "height" : selectionArea.defHeight + "px"
            });
        }

        $gridArea.toggle();
    }

    function hideSelectionArea() {
        gridX = gridY = gridW = gridH = 0;
        $gridArea.toggle();
    }
    // returns bounding box for selection area
    this.getSelectionArea = function() {
        // using parseFloat to get rid off 'px' unit
        var selX = parseFloat($selectionArea.css("left"));
        var selY = parseFloat($selectionArea.css("top"));
        var selW = parseFloat($selectionArea.css("width"));
        var selH = parseFloat($selectionArea.css("height"));
        console.log("getSelectionArea:" + "\nx:" + selX + "\ny:" + selY + "\nw:" + selW + "\nh:" + selH)
        // to get the correct coordinates add bounding box x,y values
        return [(selX + gridX), (selY + gridY), (selW + gridX + selX), (selH + gridY + selY)]
    }
    this.confirmSelectionArea = function() {
        var newRoom = createNewRoom(that.getSelectionArea());
        unselectRoom(selectedRooms[0]);
        // select new room
        selectRoom(newRoom);
    }
    function createNewRoom(x, y, x2, y2) {
        // get data from parent (current selected room)
        var parentRoom = selectedRooms[0];
        console.log("createNewRoom:\nx " + x + " y " + y + " x2 " + x2 + " y2 " + y2);
        geoData.rooms.push({
            id : parentRoom.id + "_2",
            name : parentRoom.name + "_2",
            icon : parentRoom.icon,
            color : parentRoom.color,
            boundingBox : [x, y, x2, y2],
            coord : [{
                x : x,
                y : y
            }, {
                x : x2,
                y : y
            }, {
                x : x2,
                y : y2
            }, {
                x : x,
                y : y2
            }]
        });
        return geoData.rooms[geoData.rooms.length - 1];
    }
    
    this.updateRooms = function(newRooms) {
        console.log("updateRooms " + JSON.stringify(newRooms));
        // set new selected rooms 
        selectedRooms = newRooms;
        // redraw all layers
        redrawLayer(that.LAYERFLAG.ALL);
    }

    this.updateRoom = function(oRoom) {
        //console.log("updateRoom name: " + oRoom.name + "\ncolor: " + oRoom.color + "\ncolorIndex" + jQuery.inArray(oRoom.color, COLORTABLE));
        // update local structure
        selectedRooms[0].name = oRoom.name();
        selectedRooms[0].icon = jQuery.inArray(oRoom.icon(), ICON);
        selectedRooms[0].color = jQuery.inArray(oRoom.color(), COLORTABLE);
        // call api with room id to send room data to server
        // or send whole geoData

        // redraw room
        drawRoom(selectedRooms[0]);
        drawIcon(selectedRooms[0]);
        redrawLayer(that.LAYERFLAG.BASE|that.LAYERFLAG.NOGO);
    }
    this.setNoGo = function(oRoom) {
        console.log("set new no go area")
    }
    /**
     *  room drawing/selection
     */
    function selectRoom(room) {
        selectedRooms.push(room);
        drawRoom(room);
        drawIcon(room);
        redrawLayer(that.LAYERFLAG.BASE|that.LAYERFLAG.NOGO);
    }

    function unselectRoom(room) {
        selectedRooms = jQuery.grep(selectedRooms, function(n, j) {
            return (n != room);
        });
        drawRoom(room);
        drawIcon(room);
        redrawLayer(that.LAYERFLAG.BASE|that.LAYERFLAG.NOGO);
    }

    function drawRooms() {
        if (contextView && (state == that.STATES.CUT || state == that.STATES.AREA || state == that.STATES.MOVEAREA)) {
            that.clear();
            for (var i = 0, maxR = geoData.rooms.length; i < maxR; i++) {
                drawRoom(geoData.rooms[i]);
            }
        }
    }

    function drawRoom(room) {
        //console.log("drawRoom " + JSON.stringify(room));
        // set canvas context style for state 0
        contextView.strokeStyle = "#FFFFFF";
        contextView.fillStyle = "#FFF";
        contextView.lineJoin = "rect";
        contextView.lineWidth = 2.0;

        // create polygon
        contextView.beginPath();
        drawPolygon(room.coord);
        contextView.closePath();
        // first draw a white background for each room
        contextView.fill();
        // save state 0 and create a new state
        contextView.save();

        if (state == that.STATES.CUT) {
            contextView.lineWidth = 2.0;
            if (jQuery.inArray(room, selectedRooms) > -1) {
                // set grid pattern as background style
                contextView.fillStyle = getGridPattern();
                contextView.strokeStyle = "#1800ff";
            } else {
                // set grey
                contextView.fillStyle = "#000";
                contextView.globalAlpha = 0.8;
                contextView.strokeStyle = "#DDD";
            }
            // fill room with background grid pattern or transparent layer
            contextView.fill();
            // draw room border
            contextView.stroke();
            // switch back to state 0
            contextView.restore();
        } else {
            contextView.fillStyle = COLORTABLE[room.color];
            if ( jQuery.inArray(room, selectedRooms) == -1) {
                // not selected rooms get transparency of 40%
                contextView.globalAlpha = 0.4;
            }
            // fill room color
            contextView.fill();
            // switch back to state 0
            contextView.restore();
            // draw room border
            contextView.stroke();
        }
    }
    
    function redrawLayer(layer) {
        if (contextView) {
            if(layer & that.LAYERFLAG.ROOM) {
                //console.log("redrawLayer ROOM")
                drawRooms();
            }
            if(layer & that.LAYERFLAG.ICON) {
                //console.log("redrawLayer ICON")
                drawIcons();
            }
            
            if(layer & that.LAYERFLAG.BASE) {
                //console.log("redrawLayer BASE")
                drawBases();
            }
            
            if(layer & that.LAYERFLAG.NOGO) {
                //console.log("redrawLayer NOGO")
                drawNogos();
            }
            
            if(layer & that.LAYERFLAG.SPOT) {
                //console.log("redrawLayer SPOT")
                drawSpot(selectedArea);
            }
            
            if(layer & that.LAYERFLAG.ALL) {
                //console.log("redrawLayer ALL")
                that.clear();
                drawRooms();
                drawIcons();
                drawBases();
                drawNogos();
                drawSpot(selectedArea);
            }
        }
    }
    
    function drawIcons() {
        if (contextView) {
            for (var i = 0, maxR = geoData.rooms.length; i < maxR; i++) {
                drawIcon(geoData.rooms[i]);
            }
        }
    }
    
    function drawIcon(room) {
        try {
            //console.log("drawIcon for room " + JSON.stringify(room))
            var spriteY = ICONPOS[ICON[room.icon]]*iconSize.height||0;
            // draw icon centered in bounding box
            imgX = parseInt(room.boundingBox[0] + (room.boundingBox[2] - room.boundingBox[0])/2 - iconSize.width/2);
            imgY = parseInt(room.boundingBox[1] + (room.boundingBox[3] - room.boundingBox[1])/2 - iconSize.height/2);
            //console.log("spriteY " + spriteY + "\nimgX " + imgX + "\nimgY " + imgY)
            contextView.drawImage(categoryIcon, 0, spriteY, iconSize.width, iconSize.height, imgX, imgY, iconSize.targetWidth, iconSize.targetHeight)
        } catch(e) {
            // icon wasn't loaded yet, retry in one second
            window.setTimeout(function() {
                console.log("timeout drawIcon for room " + JSON.stringify(room))
                drawIcon(room);
            },1000)
        };
    }
    
    function drawBases() {
        //console.log("drawBases")
        if (geoData.base) {
            // save state 0 and create a new state
            contextView.save();
            for (var i = 0, maxB = geoData.base.length; i < maxB; i++) {
                drawBase(i);
            }
            // switch back to state 0
            contextView.restore();
        }
    }
    function drawBase(baseIndex) {
        contextView.globalAlpha = 1.0;
        contextView.strokeStyle = "#000000";
        contextView.fillStyle = "#EA6F0F";
        contextView.lineJoin = "miter";
        contextView.lineWidth = 2.0;
        if (state == that.STATES.AREA || state == that.STATES.MOVEAREA || state == that.STATES.SPOT || state == that.STATES.MOVESPOT) {
            // create polygon
            contextView.beginPath();
            drawRect(geoData.base[baseIndex]);
            contextView.closePath();
             // fill room with background
            contextView.fill();
            // draw room border
            contextView.stroke();
        }
    }
    
    function drawNogos() {
        //console.log("drawNogos")
        if (geoData.noGo) {
            // save state 0 and create a new state
            contextView.save();
            for (var i = 0, maxN = geoData.noGo.length; i < maxN; i++) {
                drawNogo(i);
            }
            // switch back to state 0
            contextView.restore();
        }
    }
    
    function drawNogo(nogoIndex) {
        contextView.globalAlpha = 1.0;
        contextView.fillStyle = getNogoPattern();
        contextView.strokeStyle = "#000000";
        contextView.lineJoin = "miter";
        contextView.lineWidth = 2.0;
        if (state == that.STATES.AREA || that.STATES.MOVEAREA || state == that.STATES.SPOT || state == that.STATES.MOVESPOT) {
            // create polygon
            contextView.beginPath();
            drawRect(geoData.noGo[nogoIndex]);
            contextView.closePath();
             // fill room with pattern
            contextView.fill();
            // draw room border
            contextView.stroke();
        }
    }
    
    function drawSpot(bounding) {
        if(bounding) {
            contextView.save();
            contextView.globalAlpha = 0.33;
            contextView.fillStyle = "#95c11c";
            
            // create polygon
            contextView.beginPath();
            drawRect(bounding);
            contextView.closePath();
             // fill room with pattern
            contextView.fill();
            contextView.restore();
        }
    }
    
    function drawRect(arrBox) {
        // first entry is always the new start position
        contextView.moveTo(arrBox[0], arrBox[1]);
        contextView.lineTo(arrBox[2], arrBox[1]);
        contextView.lineTo(arrBox[2], arrBox[3]);
        contextView.lineTo(arrBox[0], arrBox[3]);
    }

    function drawPolygon(arrCoord) {
        if (arrCoord.length > 0) {
            // first entry is always the new start position
            contextView.moveTo(arrCoord[0].x, arrCoord[0].y);
            for (var i = 1, maxC = arrCoord.length; i < maxC; i++) {
                contextView.lineTo(arrCoord[i].x, arrCoord[i].y);
            }
        }
    }

    function checkRooms(pt) {
        for (var i = 0, maxR = geoData.rooms.length; i < maxR; i++) {
            if (isPointInPoly(geoData.rooms[i].coord, pt)) {
                console.log("checkRooms found roomId " + geoData.rooms[i].id)
                if (jQuery.inArray(geoData.rooms[i], selectedRooms) > -1) {
                    unselectRoom(geoData.rooms[i]);
                } else {
                    selectRoom(geoData.rooms[i]);
                }
                return true;
            }
        }
        return false;
    }

}
