$.ui.plugin.add("resizable", "gridWithEvent", {
    resize : function(event, ui) {
        var that = $(this).data("resizable");
        // add snapFactor property to object
        if ( typeof that.oldSnapFactor == "undefined") {
            that.oldSnapFactor = {
                x : 0,
                y : 0
            };
        }
        var s = that.oldSnapFactor, o = that.options, cs = that.size, os = that.originalSize, op = that.originalPosition, a = that.axis, ratio = o._aspectRatio || event.shiftKey;
        o.grid = typeof o.gridWithEvent == "number" ? [o.gridWithEvent, o.gridWithEvent] : o.gridWithEvent;
        var snapFactorX = Math.round((cs.width - os.width) / (o.grid[0] || 1)), snapFactorY = Math.round((cs.height - os.height) / (o.grid[1] || 1));
        var ox = snapFactorX * (o.grid[0] || 1), oy = snapFactorY * (o.grid[1] || 1);

        // update size
        if (/^(se|s|e)$/.test(a)) {
            that.size.width = os.width + ox;
            that.size.height = os.height + oy;
        } else if (/^(ne)$/.test(a)) {
            that.size.width = os.width + ox;
            that.size.height = os.height + oy;
            that.position.top = op.top - oy;

        } else if (/^(sw)$/.test(a)) {
            that.size.width = os.width + ox;
            that.size.height = os.height + oy;
            that.position.left = op.left - ox;
        } else {
            that.size.width = os.width + ox;
            that.size.height = os.height + oy;
            that.position.top = op.top - oy;
            that.position.left = op.left - ox;
        }
        
        // check if snapped factor has changed
        if (snapFactorX != s.x || snapFactorY != s.y) {
            that.oldSnapFactor.x = snapFactorX;
            that.oldSnapFactor.y = snapFactorY;
            // snapped to new position trigger event
            that._trigger('gridSnapEvent', event, [ui, {
                x : (op.left - ox),
                y : (op.top - oy),
                w : (os.width + ox),
                h : (os.height + oy)
            }]);
        }
    }
}); 