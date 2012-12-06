//TODO: move into utilities
/**
 * isPointInPoly checks if a polygon contains an specified point
 * @param {Array} array with point objects, each point must have the properties 
 *                x and y
 * @param {Object} point object which must have the properties x and y
 * @return {Boolean} return true if polygon contains the point 
 */
function isPointInPoly(poly, pt){
    var c = false;
    for(var i = -1, l = poly.length, j = l - 1; ++i < l; j = i) {
        ((poly[i].y <= pt.y && pt.y < poly[j].y) || (poly[j].y <= pt.y && pt.y < poly[i].y))
        && (pt.x < (poly[j].x - poly[i].x) * (pt.y - poly[i].y) / (poly[j].y - poly[i].y) + poly[i].x)
        && (c = !c)
    }
    return c;
}

/**
 * MapCanvas is the class for the map view which contains also a canvas layer
 * @class Represents the MapCanvas control
 * @param {object} viewmodel Reference to the current view model.  
 */
function MapCanvas(viewmodel) {
    // Hold canvas information
    var that = this;
    var viewScroll;
    var drawArea = false;
    var geoData;
    
    var canvas, $canvas, context, width, height, posX=0, posY=0;
    var canvasView, $canvasView, contextView, posViewX, posViewY;
    var degree = 0;
    var zoom = 0;
    var maxZoom = 5;
    this.curSnapshot;
    var snapImageData;
    var visualPadding = 10;
    var viewX = visualPadding, viewY = visualPadding, viewXOrg,viewYOrg;
    var $viewport;
    var contentHeight = 0;
    
    var roomMaxX = 0;
    var roomMaxY = 0;
    var verticalScrolling = false;
    var horizontalScrolling = false;
    var scrollHeight = 0;
    var scrollWidth = 0;
    var smooth = false;
    var selectedRooms = [];
    var $gridArea;
    var $gridLayer;
    var $selectionArea;
    var selectionArea;
    
    var gridX,gridY,gridW,gridH;
    var preventSlide = false;
    var gridImg = new Image();
    var gridPattern;
    
    
    this.init = function() {
        $viewport = $('#viewport');
        canvasView = document.getElementById('canvasView');
        contextView = canvasView.getContext('2d');
        $canvasView = $('#canvasView');
        
        $gridLayer = $("#grid-layer");
        $gridArea = $('#grid');
        $selectionArea = $('#selection-area');
        selectionArea = document.getElementById('selection-area');
        gridImg.src = "img/grid.png";
        
        //that.loadRoom();
        geoData = getGeogophy("floor1");
        roomMaxX = geoData.boundingBox[2];
        roomMaxY = geoData.boundingBox[3];
        
        $selectionArea.resizable({
            containment:"#grid",
            grid:[5,5],
            //helper: "ui-resizable-helper", //buggy with v1.9.2
            handles: "sw,ne,se,nw",
            minHeight: 50,
            minWidth: 50,
            start: function( event, ui ) {
                // disable iScroll, to resize the selection area
                viewScroll.disable();
            },
            stop: function( event, ui ) {
                // resize done: enable iScroll
                viewScroll.enable();
            }
        }).draggable({ 
            containment:"#grid",
            grid:[5,5],
            snap:"#grid",
            snapTolerance:5,
            snapMode: "inner",
            start: function( event, ui ) {
                // disable iScroll, to drag the selection area
                viewScroll.disable();
            },
            stop: function( event, ui ) {
                // dragging done: enable iScroll
                viewScroll.enable();
            }
        });
        
        $(document).on("updateLayout.mapCanvas", function(event) {
            updatePosition();
        });
        
        $(document).one("pageshow.mapCanvas", function(e) {
            initScrollView();
            setPattern();
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
        // remove all event handler for mapCanvas 
        $(document).off(".mapCanvas");
    }
    function initScrollView() {
        viewScroll = new iScroll('viewport', {
            lockDirection:false,
            zoom: true,
            zoomMax:2,
            hideScrollbar:true,
            fadeScrollbar:true,
            
            onZoomEnd: function () {
                console.log("ZOOM END"
                        + "\nx " + viewScroll.x
                        + "\ny " + viewScroll.y
                        + "\nscale " + viewScroll.scale
                        );
                 
            },
            onTouchEnd: function(e) {
                var point = e.changedTouches ? e.changedTouches[0] : e;
                if(!viewScroll.moved) {
                    var pt = {x:((point.pageX - viewScroll.x - visualPadding) / viewScroll.scale) - posX, y: ((point.pageY - viewScroll.y - visualPadding) / viewScroll.scale) - posY};
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
                    if(!drawArea) {
                        if(checkRooms(pt)) {
                            //drawRooms();
                            if(selectedRooms.length == 0) {
                                viewmodel.fsm.disable();
                            } else if(selectedRooms.length == 1) {
                                viewmodel.fsm.enable();
                            } else if(selectedRooms.length > 1) {
                                //TODO: check if rooms were neighbors
                                viewmodel.fsm.merge();
                            }
                            
                        }
                    }
                }
            }
            });
        
        $(":jqmData(role='content')").css({"position":"relative", "padding":"0"});
    }
    function setPattern() {
        gridPattern = contextView.createPattern(gridImg, "repeat");
    }
    function updatePosition() {
        posY =  $viewport.offset().top;
        contentHeight = ($(window).height() - posY);
        $(":jqmData(role='content')").height(contentHeight);
        /*
        console.log("w " + $(window).height()
                  + " h " + $(window).find(':jqmData(role="header")').height()
                  + " o " + $viewport.offset().top
                  + " posY " + posY
              );
        */
        height = $viewport.height();
        width = $viewport.width();
        if(width < roomMaxX) {
            horizontalScrolling = true;
        }
        if(height < roomMaxY) {
            verticalScrolling = true;
        }
        
        scrollHeight = verticalScrolling ? height - roomMaxY - visualPadding: 0;
        scrollWidth = horizontalScrolling ? width - roomMaxX - visualPadding: 0;
        
        $canvasView.attr({
            width : (horizontalScrolling ? roomMaxX : width),
            height : (verticalScrolling ? roomMaxY : height)
        });
        console.log("updatePosition(); height "+ height + " width "+ width + " posY " + posY );
        that.clear();
        drawRooms();
        viewScroll.refresh();
    }
    
    //wipes the canvas context
    this.clear = function() {
        // just visible view
        //contextView.clearRect(0, 0, width, height);
        // complete canvas
        contextView.clearRect(0, 0,(horizontalScrolling ? roomMaxX : width),(verticalScrolling ? roomMaxY : height));
    }
    this.zoom = function() {
        viewScroll.zoom(viewScroll.pointX, viewScroll.pointY, viewScroll.scale == 1 ? viewScroll.options.doubleTapZoom : 1);
    }
    this.rotate = function() {
            degree = degree + 90;
            console.log("rotate " + degree +"°");
            $canvasView.css({"transform":"rotate(" + degree + "deg)",
                    "transition-duration":"250ms",
                    });
    };
    
    
    /**
     * selection area 
     */
    this.enableAreaSelection = function(flag) {
        drawArea = flag;
        if(flag) {
            // show area div
            showSelectionArea();
        } else {
            // hide div
            hideSelectionArea();
        }
    }
    
    function showSelectionArea() {
        gridX = geoData.rooms[selectedRooms[0]].boundingBox[0];
        gridY = geoData.rooms[selectedRooms[0]].boundingBox[1];
        gridW = (geoData.rooms[selectedRooms[0]].boundingBox[2] - geoData.rooms[selectedRooms[0]].boundingBox[0]);
        gridH = (geoData.rooms[selectedRooms[0]].boundingBox[3] - geoData.rooms[selectedRooms[0]].boundingBox[1]);
        
        $gridArea.css({
            "left":gridX + visualPadding + "px",
            "top":gridY + visualPadding + "px",
            "width":gridW + "px",
            "height":gridH + "px"
        })
        
        /*
        console.log("left:"+(gridX + visualPadding) + "px"
            +"\ntop:"+ (gridY + visualPadding) + "px"
            +"\nwidth:"+ gridW + "px"
            +"\nheight:" + gridH + "px");
        */
        $selectionArea.css({
            "left":"0",
            "top":"0",
            "width":"80px",
            "height":"80px"
        })
        
        $gridArea.toggle();
        drawRooms();
    }
    
    function hideSelectionArea() {
        gridX = gridY = gridW = gridH = 0;
        //$gridLayer.toggle();
        $gridArea.toggle();
        drawRooms();
    }
    
    this.confirmSelectionArea = function() {
        var selX = parseFloat($selectionArea.css("left"));
        var selY = parseFloat($selectionArea.css("top"));
        var selW = parseFloat($selectionArea.css("width"));
        var selH = parseFloat($selectionArea.css("height"));
        console.log("confirmSelectionArea:"
                    +"\nx:" + selX
                    +"\ny:" + selY
                    +"\nw:" + selW
                    +"\nh:" + selH
                    )
        // to get the correct coordinates add bounding box x,y values
        createNewRoom((selX + gridX), (selY + gridY), (selW + gridX + selX), (selH + gridY + selY));
        unselectRoom(selectedRooms[0]);
        // clear selected room array
        selectedRooms.length = 0;
        // last room in array is the new one, select it
        selectRoom(geoData.rooms.length - 1);
        that.enableAreaSelection(false);
    }
    
    function createNewRoom(x,y,x2,y2) {
        // get data from parent (current selected room)
        var parentRoom = geoData.rooms[selectedRooms[0]];
        console.log("createNewRoom:\nx " + x + " y " + y + " x2 " + x2 + " y2 " + y2);
        geoData.rooms.push(
            {
                id:parentRoom.id + "_2",
                name:parentRoom.name + "_2",
                icon:parentRoom.icon,
                color:parentRoom.color,
                //selColor:"#FF0000",
                boundingBox:[x,y,x2,y2],
                coord:[
                    {x:x,y:y},
                    {x:x2,y:y},
                    {x:x2,y:y2},
                    {x:x,y:y2}
                ]
            }
        )
    }
   
    
    /**
     *  room drawing/selection 
     */
    
    function selectRoom(roomIndex) {
        geoData.rooms[roomIndex].sel = true;
        selectedRooms.push(roomIndex);
        drawRoom(roomIndex);
    }
    
    function unselectRoom(roomIndex) {
        geoData.rooms[roomIndex].sel = false;
        selectedRooms = jQuery.grep(selectedRooms, function(n, j){
          return (n != roomIndex);
        });
        drawRoom(roomIndex);
    }
    
    function drawRooms() {
        that.clear();
        for(var i = 0, maxR = geoData.rooms.length; i < maxR; i++) {
            drawRoom(i);
        }
    }
    function drawRoom(roomIndex) {
        // set canvas context style for state 0
        contextView.strokeStyle = "#FFFFFF";
        contextView.fillStyle = "#FFF";
        contextView.lineJoin = "round";
        contextView.lineWidth = 2;
        
        // create polygon
        contextView.beginPath();
        drawPolygon(geoData.rooms[roomIndex].coord);
        contextView.closePath();
        // first draw a white background for each room
        contextView.fill();
        // save state 0 and create a new state
        contextView.save();
        
        if(drawArea) {
            contextView.lineWidth = 2;
            if(geoData.rooms[roomIndex].sel) {
                // set grid pattern as background style 
                contextView.fillStyle = gridPattern;
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
            contextView.fillStyle = geoData.rooms[roomIndex].color;
            if(typeof geoData.rooms[roomIndex].sel == "undefined" || geoData.rooms[roomIndex].sel === false ) {
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
    
    function drawPolygon(arrCoord) {
        if(arrCoord.length > 0) {
            // first entry is always the new start position
            contextView.moveTo(arrCoord[0].x, arrCoord[0].y);
            for(var i = 1, maxC = arrCoord.length; i < maxC; i++) {
                contextView.lineTo(arrCoord[i].x, arrCoord[i].y);
            }
        }
    }
    
    function checkRooms(pt) {
        for(var i = 0, maxR = geoData.rooms.length; i < maxR; i++) {
            if(isPointInPoly(geoData.rooms[i].coord, pt) ) {
                if(typeof geoData.rooms[i].sel == "undefined" || !geoData.rooms[i].sel) {
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
