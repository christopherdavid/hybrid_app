//TODO: move into utilities
/**
 * isPointInPoly checks if a polygon contains an specified point
 * @param {Array} array with point objects, each point must have the properties
 *                x and y
 * @param {Object} point object which must have the properties x and y
 * @return {Boolean} return true if polygon contains the point
 */
function isPointInPoly(poly, pt) {
    var c = false;
    for (var i = -1, l = poly.length, j = l - 1; ++i < l; j = i) {
        ((poly[i].y <= pt.y && pt.y < poly[j].y) || (poly[j].y <= pt.y && pt.y < poly[i].y)) && (pt.x < (poly[j].x - poly[i].x) * (pt.y - poly[i].y) / (poly[j].y - poly[i].y) + poly[i].x) && ( c = !c)
    }
    return c;
}

/**
 * MapCanvas is the class for the map view which contains also a canvas layer
 * @class Represents the MapCanvas control
 * @param {object} viewmodel Reference to the current view model.
 */
function MapCanvas(viewmodel) {
    var that = this;
    var viewScroll;
    // container for geography data
    var geoData;

    var canvas, $canvas, context, width, height, posX = 0, posY = 0;
    var canvasView, $canvasView, contextView, posViewX, posViewY;
    var degree = 0;
    var visualPadding = 10;
    var viewX = visualPadding, viewY = visualPadding, viewXOrg, viewYOrg;
    var $viewport;
    var contentHeight = 0;
    var roomMaxX = 0;
    var roomMaxY = 0;
    var verticalScrolling = false;
    var horizontalScrolling = false;
    // collection of rooms which were selected from user
    var selectedRooms = [];
    var $gridArea;
    var $selectionArea;

    var gridX, gridY, gridW, gridH;
    var gridImg = new Image();
    var gridPattern;
    var nogoImg = new Image();
    var nogoPattern;
    // initial default size of selection area
    var selectionArea = {
        defWidth : 80,
        defHeight : 80
    }
    // tooltip: show room summary
    //var $infoPopup;
    //var popupTimer;
    // sets the time in ms after the popup gets closed automatically
    //var popupLiveTime = 2000;

    var state;
    this.STATES = {
        SPOT : 0,
        AREA : 1,
        CUT : 2
    }
    this.LAYERFLAG = {
        ROOM:1,
        BASE:4,
        NOGO:8,
        ALL:16
    }

    this.init = function() {
        $viewport = $('#viewport');
        canvasView = document.getElementById('canvasView');
        contextView = canvasView.getContext('2d');
        $canvasView = $('#canvasView');

        $gridArea = $('#grid');
        $selectionArea = $('#selection-area');
        gridImg.src = "img/grid.png";
        nogoImg.src = "img/nogo.png";
        //$infoPopup = $("#infoPopup");

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
        geoData = null;
        // remove all event handler for mapCanvas
        $(document).off(".mapCanvas");
    }
    
    this.setGeoData = function(geography) {
        // clear selected room array
        selectedRooms.length = 0;
        geoData = geography;
        roomMaxX = geoData.boundingBox[2];
        roomMaxY = geoData.boundingBox[3];
        // TODO: temporary fix: wrong server values for color and icon
        for (var i = 0, maxR = geoData.rooms.length; i < maxR; i++) {
            geoData.rooms[i].color = 1;
            geoData.rooms[i].icon = 0;
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
                     + "\nwrapperOffsetLeft" + viewScroll.wrapperOffsetLeft
                     + "\nwrapperOffsetTop" + viewScroll.wrapperOffsetTop
                     + "\nposY" + posY
                     + "\nvisualPadding " + visualPadding
                     + "\ncheck: pt.x " + pt.x + " pt.y " + pt.y)
                     */
                    if (state == that.STATES.AREA) {
                        if (checkRooms(pt)) {
                            if (selectedRooms.length == 0) {
                                viewmodel.fsm.disable();
                            } else if (selectedRooms.length == 1) {
                                viewmodel.fsm.enable();
                            } else if (selectedRooms.length > 1) {
                                //TODO: check if rooms were neighbors
                                if (!viewmodel.fsm.is("multiple")) {
                                    viewmodel.fsm.merge();
                                }
                            }

                        }
                    }
                }
            }
        });

        $("#mapView").css({
            "position" : "relative",
            "padding" : "0"
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
        posY = $viewport.offset().top;
        contentHeight = ($(window).height() - posY);
        $("#mapView").height(contentHeight);
        console.log("w " + $(window).height() + " h " + $(window).find(':jqmData(role="header")').height() + " o " + $viewport.offset().top + " posY " + posY);
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
            redrawLayer(that.LAYERFLAG.ALL);
            viewScroll.refresh();
        }
    }


    this.setState = function(newState) {
        if (state != newState) {
            that.leaveState();
            switch(newState) {
                case that.STATES.SPOT:
                    $selectionArea.addClass("selectionarea-spot");
                    break;
                case that.STATES.AREA:
                    break;
                case that.STATES.CUT:
                    $selectionArea.addClass("selectionarea-cut");
                    showSelectionArea();
                    break;
            }
            state = newState;
            redrawLayer(that.LAYERFLAG.ALL);
        }
    }
    this.leaveState = function() {
        switch(state) {
            case that.STATES.SPOT:
                $selectionArea.removeClass("selectionarea-spot");
                break;
            case that.STATES.AREA:
                break;
            case that.STATES.CUT:
                $selectionArea.removeClass("selectionarea-cut");
                hideSelectionArea();
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

    
    function showSelectionArea() {
        gridX = geoData.rooms[selectedRooms[0]].boundingBox[0];
        gridY = geoData.rooms[selectedRooms[0]].boundingBox[1];
        gridW = (geoData.rooms[selectedRooms[0]].boundingBox[2] - geoData.rooms[selectedRooms[0]].boundingBox[0]);
        gridH = (geoData.rooms[selectedRooms[0]].boundingBox[3] - geoData.rooms[selectedRooms[0]].boundingBox[1]);

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
        $selectionArea.css({
            "left" : (gridW / 2 - selectionArea.defWidth / 2) + "px",
            "top" : (gridH / 2 - selectionArea.defHeight / 2) + "px",
            "width" : selectionArea.defWidth + "px",
            "height" : selectionArea.defHeight + "px"
        })

        $gridArea.toggle();
    }

    function hideSelectionArea() {
        gridX = gridY = gridW = gridH = 0;
        $gridArea.toggle();
    }


    this.confirmSelectionArea = function() {
        var selX = parseFloat($selectionArea.css("left"));
        var selY = parseFloat($selectionArea.css("top"));
        var selW = parseFloat($selectionArea.css("width"));
        var selH = parseFloat($selectionArea.css("height"));
        console.log("confirmSelectionArea:" + "\nx:" + selX + "\ny:" + selY + "\nw:" + selW + "\nh:" + selH)
        // to get the correct coordinates add bounding box x,y values
        createNewRoom((selX + gridX), (selY + gridY), (selW + gridX + selX), (selH + gridY + selY));
        unselectRoom(selectedRooms[0]);
        // clear selected room array
        selectedRooms.length = 0;
        // last room in array is the new one, select it
        selectRoom(geoData.rooms.length - 1);
        //that.enableAreaSelection(false);
    }
    function createNewRoom(x, y, x2, y2) {
        // get data from parent (current selected room)
        var parentRoom = geoData.rooms[selectedRooms[0]];
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
        })
    }

    /*
     function showRoomInfo(roomIndex) {
     window.clearTimeout(popupTimer);
     $infoPopup.css("backgroundColor",COLORTABLE[geoData.rooms[roomIndex].color]);
     $infoPopup.children("h3").text(geoData.rooms[roomIndex].name);
     $infoPopup.popup("open", {  x: (geoData.rooms[roomIndex].boundingBox[0] + visualPadding + (geoData.rooms[roomIndex].boundingBox[2] - geoData.rooms[roomIndex].boundingBox[0])/2),
     y: (geoData.rooms[roomIndex].boundingBox[1] + visualPadding + posY + (geoData.rooms[roomIndex].boundingBox[3] - geoData.rooms[roomIndex].boundingBox[1])/2)})
     popupTimer = window.setTimeout(function(){ $infoPopup.popup("close"); }, popupLiveTime)
     }
     */
    this.getSelectedArea = function() {
        // return reference to geoData
        return geoData.rooms[selectedRooms[0]];
    }

    this.updateRoom = function(oRoom) {
        //console.log("updateRoom name: " + oRoom.name + "\ncolor: " + oRoom.color + "\ncolorIndex" + jQuery.inArray(oRoom.color, COLORTABLE));
        // update local structure
        geoData.rooms[selectedRooms[0]].name = oRoom.name();
        geoData.rooms[selectedRooms[0]].icon = jQuery.inArray(oRoom.icon(), ICON);
        geoData.rooms[selectedRooms[0]].color = jQuery.inArray(oRoom.color(), COLORTABLE);
        // call api with room id to send room data to server
        // or send whole geoData

        // redraw room
        drawRoom(selectedRooms[0]);
        redrawLayer(that.LAYERFLAG.BASE|that.LAYERFLAG.NOGO);
    }
    this.setNoGo = function(oRoom) {
        console.log("set new no go area")
    }
    /**
     *  room drawing/selection
     */

    function selectRoom(roomIndex) {
        geoData.rooms[roomIndex].sel = true;
        selectedRooms.push(roomIndex);
        drawRoom(roomIndex);
        redrawLayer(that.LAYERFLAG.BASE|that.LAYERFLAG.NOGO);
        //showRoomInfo(roomIndex);
    }

    function unselectRoom(roomIndex) {
        geoData.rooms[roomIndex].sel = false;
        selectedRooms = jQuery.grep(selectedRooms, function(n, j) {
            return (n != roomIndex);
        });
        drawRoom(roomIndex);
        redrawLayer(that.LAYERFLAG.BASE|that.LAYERFLAG.NOGO);
    }

    function drawRooms() {
        if (contextView) {
            that.clear();
            for (var i = 0, maxR = geoData.rooms.length; i < maxR; i++) {
                drawRoom(i);
            }
        }
    }

    function drawRoom(roomIndex) {
        //console.log("drawRoom " + roomIndex)
        // set canvas context style for state 0
        contextView.strokeStyle = "#FFFFFF";
        contextView.fillStyle = "#FFF";
        contextView.lineJoin = "rect";
        contextView.lineWidth = 2.0;

        // create polygon
        contextView.beginPath();
        drawPolygon(geoData.rooms[roomIndex].coord);
        contextView.closePath();
        // first draw a white background for each room
        contextView.fill();
        // save state 0 and create a new state
        contextView.save();

        if (state == that.STATES.CUT) {
            contextView.lineWidth = 2.0;
            if (geoData.rooms[roomIndex].sel) {
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
            contextView.fillStyle = COLORTABLE[geoData.rooms[roomIndex].color];
            if ( typeof geoData.rooms[roomIndex].sel == "undefined" || geoData.rooms[roomIndex].sel === false) {
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
        if(layer & that.LAYERFLAG.ROOM) {
            //console.log("redrawLayer ROOM")
            drawRooms();
        }
        
        if(layer & that.LAYERFLAG.BASE) {
            //console.log("redrawLayer BASE")
            drawBases();
        }
        
        if(layer & that.LAYERFLAG.NOGO) {
            //console.log("redrawLayer NOGO")
            drawNogos();
        }
        
        if(layer & that.LAYERFLAG.ALL) {
            //console.log("redrawLayer ALL")
            drawRooms();
            drawBases();
            drawNogos();
        }
    }
    
    function drawBases() {
        //console.log("drawBases")
        if (contextView && geoData.base) {
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
        if (state == that.STATES.AREA || state == that.STATES.SPOT) {
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
        if (contextView && geoData.nogo) {
            // save state 0 and create a new state
            contextView.save();
            for (var i = 0, maxN = geoData.nogo.length; i < maxN; i++) {
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
        if (state == that.STATES.AREA || state == that.STATES.SPOT) {
            // create polygon
            contextView.beginPath();
            drawRect(geoData.nogo[nogoIndex]);
            contextView.closePath();
             // fill room with pattern
            contextView.fill();
            // draw room border
            contextView.stroke();
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
                if ( typeof geoData.rooms[i].sel == "undefined" || !geoData.rooms[i].sel) {
                    selectRoom(i);
                } else {
                    unselectRoom(i);
                }
                return true;
            }
        }
        return false;
    }

}
