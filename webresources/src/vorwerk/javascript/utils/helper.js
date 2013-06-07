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

function localizeTime(time) {
    var timeFormat = $.i18n.t("pattern.time");
    var amPmMarker = "";
    var hour = time.split(":")[0];
    var min = time.split(":")[1]
    if (timeFormat == "hhiiA") {
        amPmMarker = hour <= 12 ? ' am' : ' pm';
        
        if(hour > 12) {
            hour = hour - 12;
        }
    }
    // add leading zero
    hour = hour < 10 ? "0" + hour : hour;
    return hour + ':' + min + amPmMarker;
}
// helper class for robot state
var robotStateMachine = {
    lastState:"",
    current:"inactive",
    callback: null,

    is:function() {
        return this.current;
    },
    stateBefore:function() {
        return this.lastState;
    },
    clean:function() {
        console.log("clean")
        this.changestate(this.current, "active");
    },
    disable:function() {
        console.log("disable")
        this.changestate(this.current, "disabled")
    },
    deactivate:function() {
        console.log("deactivate")
        this.changestate(this.current, "inactive")
    },
    pause:function() {
        console.log("pause")
        this.changestate(this.current, "paused")
    },
    changestate: function(from, to) {
        this.lastState = from;
        this.current = to;
        if(this.callback != null && typeof this.callback == "function") {
            this.callback(from, to);
        }
    }
};
