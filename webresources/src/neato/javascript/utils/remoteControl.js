function RemoteControl($root) {

    that = this;

    var $canvas;

    const DEBUG = false;

    var pointer = {};

    nextFrame = (function() {
        return window.requestAnimationFrame 
            || window.webkitRequestAnimationFrame 
            || window.mozRequestAnimationFrame 
            || window.oRequestAnimationFrame 
            || window.msRequestAnimationFrame 
            || function(callback) {
                window.setTimeout(callback, 1);
            };
        })();

    /**
     * Builds the control and all of its functionality
     */
    this.init = function() {

        buildDomTree();

        // receive resize and orientation change events
        $(window).on("resize.remote", function() {

            $canvas.attr('height', '600px');
            $canvas.attr('width', $root.width());
        });

        // listener after all divs got a size
        $(document).one("pageshow.remote", function(e) {

            $canvas.attr('height', $root.height());
            $canvas.attr('width', $root.width());
            //console.log('$root w/h: ' + $root.width() + '/' + $root.height());
        });

        $canvas.jCanvas();

        nextFrame(draw);

    }
    function draw() {

        nextFrame(draw);

        $canvas.clearCanvas();

        var centerX = $canvas.width() / 2;
        var centerY = $canvas.height() / 2;

        var rad = 30;

        var radius = 300;

        var coord = {};
        coord.x = pointer.x - centerX;
        coord.y = (pointer.y - centerY);

        coord.percentX = coord.x / centerX;
        coord.percentY = coord.y / centerY;

        coord.degree = Math.atan2(coord.x, -coord.y);

        //console.log(coord);

        var curveWidth = 20;

        // bezier
        var curve = pointer.x;
        if (pointer.y - rad > centerY) {

            if (coord.x > 0) {
                curve = (pointer.x + coord.y * coord.percentX);
            } else {
                var percent = Math.min(1, (centerX / pointer.x) - 1);
                curve = (pointer.x - (pointer.y - centerY) * percent);
            }
            // console.log(percent + '\ncenterY: ' + centerX + '\npointerX: ' +
            // pointer.x + '\npointerX2: '+(pointer.x-centerX));
        }

        var maxArcTop = curveWidth;
        var maxArcWidth = curveWidth;
        var arcTop = curveWidth * Math.abs(coord.percentX);
        var arcSide = curveWidth * Math.abs(coord.percentY);

        var heightDifLeft = 0;
        var heightDifRight = 0;
        if (coord.x > 0) {
            var heightDifLeft = arcTop;
            var heightDifRight = arcTop;
        } else {
            var heightDifLeft = arcTop;
            var heightDifRight = arcTop;
        }

        var start = {};
        var end = {};
        var peak = {};

        var cx1 = {};
        var cx2 = {};
        var cx3 = {};
        var cx4 = {};

        // left
        start.x = centerX - curveWidth * Math.sin(coord.degree / 2 + Math.PI / 2);
        start.y = centerY + curveWidth * Math.cos(coord.degree / 2 + Math.PI / 2);

        // right
        end.x = centerX + curveWidth * Math.sin(coord.degree / 2 + Math.PI / 2);
        end.y = centerY - curveWidth * Math.cos(coord.degree / 2 + Math.PI / 2);

        // console.log(end.x+"/"+end.y + " " + start.x +"/"+ start.y);

        peak.x = pointer.x;
        peak.y = pointer.y;

        if (coord.x > 0) {
            if (coord.y < 0) {
                // I. Quadrant
                cx1.x = centerX - curveWidth;
                cx1.y = centerY - arcTop * 10;

                cx2.x = curve;
                cx2.y = centerY;

                cx3.x = curve;
                cx3.y = centerY;

                cx4.x = centerX + curveWidth;
                cx4.y = centerY - arcTop * 7;
            } else {
                // II. Quadrant
                cx1.x = centerX - curveWidth;
                cx1.y = centerY - arcTop * 10;

                cx2.x = curve + curveWidth;
                cx2.y = centerY;

                cx3.x = curve - heightDifRight - curveWidth;
                cx3.y = centerY;

                cx4.x = centerX + curveWidth;
                cx4.y = centerY - arcTop * 7;

            }
        } else {
            if (coord.y > 0) {
                // III. Quadrant
                cx1.x = centerX - curveWidth;
                cx1.y = centerY - arcTop * 7;

                cx2.x = curve + curveWidth;
                cx2.y = centerY;

                cx3.x = curve - heightDifLeft - curveWidth;
                cx3.y = centerY;

                cx4.x = centerX + curveWidth;
                cx4.y = centerY - arcTop * 10;
            } else {
                // IV. Quadrant
                cx1.x = centerX - curveWidth;
                cx1.y = centerY - arcTop * 7;

                cx2.x = curve - heightDifLeft;
                cx2.y = centerY;

                cx3.x = curve;
                cx3.y = centerY;

                cx4.x = centerX + curveWidth;
                cx4.y = centerY - arcTop * 10;

            }
        }

        // bezier
        $canvas.drawBezier({
            strokeStyle : DEBUG ? "#000" : "ddd",
            strokeWidth : 1,

            fillStyle : '#ddd',

            x1 : start.x,
            y1 : start.y, // Start point
            cx1 : cx1.x,
            cy1 : cx1.y, // Control point
            cx2 : cx2.x,
            cy2 : cx2.y, // Control point
            x2 : peak.x,
            y2 : peak.y, // Spitze

            cx3 : cx3.x,
            cy3 : cx3.y, // Control point
            cx4 : cx4.x,
            cy4 : cx4.y, // Control
            x3 : end.x,
            y3 : end.y, // End point

            // close shape
            cx5 : end.x,
            cy5 : end.y, // Control point
            cx6 : end.x,
            cy6 : end.y, // Control
            x4 : end.x,
            y4 : end.y
            // back to Start

        });

        // center
        $canvas.drawArc({
            fillStyle : "white",
            x : centerX,
            y : centerY,
            radius : rad
        });

        // pointer
        $canvas.drawArc({
            fillStyle : "black",
            x : pointer.x,
            y : pointer.y,
            radius : rad
        });

        if (DEBUG) {

            $canvas.drawLine({
                strokeStyle : "#000",
                strokeWidth : 4,
                x1 : start.x,
                y1 : start.y,
                x2 : end.x,
                y2 : end.y
            });

            // peak
            $canvas.drawArc({
                fillStyle : "#ff8000",
                x : peak.x,
                y : peak.y,
                radius : 2
            });

            // start
            $canvas.drawArc({
                fillStyle : "#ff8000",
                x : start.x,
                y : start.y,
                radius : 2
            });

            // cx1
            $canvas.drawArc({
                fillStyle : "red",
                x : cx1.x,
                y : cx1.y,
                radius : 5
            });

            // cx2
            $canvas.drawArc({
                fillStyle : "yellow",
                x : cx2.x,
                y : cx2.y,
                radius : 5
            });

            // cx3
            $canvas.drawArc({
                fillStyle : "blue",
                x : cx3.x,
                y : cx3.y,
                radius : 5
            });

            // cx4
            $canvas.drawArc({
                fillStyle : "green",
                x : cx4.x,
                y : cx4.y,
                radius : 5
            });
        }

    }

    /**
     * remove event handler and destroy objects
     */
    this.destroy = function() {
        $(document).off(".remote");
    }
    function buildDomTree() {
        $root.addClass('remoteControl');

        $canvas = $('<canvas/>', {
            style : 'z-index:-1;'
        });

        $canvas.mousemove(function(e) {
            pointer.x = e.offsetX;
            pointer.y = e.offsetY;
        });

        $canvas.bind('vmousemove.remote', function(e) {
            pointer.x = e.offsetX;
            pointer.y = e.offsetY;
        });

        //works for android 4.0
        $canvas.bind('touchmove.remote', function(e) {
            e.preventDefault();
            pointer.x = e.offsetX;
            pointer.y = e.offsetY;
        });

        $canvas.bind('mousemove.remote', function(e) {
            pointer.x = e.offsetX;
            pointer.y = e.offsetY;
        });

        $root.append($canvas);
    }


    that.init();
}
