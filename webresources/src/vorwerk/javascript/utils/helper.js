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
 * Creates a unique id for identification purposes. 
 * @return {String} returns an uniqueId 
 */
var guid = function () {
    function s4() {
        return Math.floor((1 + Math.random()) * 0x10000)
             .toString(16)
             .substring(1);
    }
    return s4() + s4() + '-' + s4() + '-' + s4() + '-' + s4() + '-' + s4() + s4() + s4();
};

var deviceSize =  (function() {
    var size, res;
    return {
        getSize: function() {
            size = {
                width: $('[data-role="page"]').first().width(),
                height: $('[data-role="page"]').first().height()
            }
            console.log("deviceSize " + JSON.stringify(size));
            this.getSize = function() {
                return size;
            }
            return size;
        },
        getResolution: function() {
            res = Math.max(this.getSize().width, this.getSize().height) > 1000 ? "high" : "low";
            console.log("resolution " + res);
            this.getResolution = function() {
                return res;
            }
            return res;
        }
    }
}());
