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
var guid = function (separator) {
    function s4() {
        return Math.floor((1 + Math.random()) * 0x10000)
             .toString(16)
             .substring(1);
    }
    return s4() + s4() + '-' + s4() + '-' + s4() + '-' + s4() + '-' + s4() + s4() + s4();
};


function scheduleWrapper(data) {
    var uidData = [];
    //console.log("scheduleWrapper\n" + JSON.stringify(data))
    for(var i = 0; i < data["schedules"].length; i++) {
        var scheduleEntry = data["schedules"][i];
        for(var j = 0; j < scheduleEntry.day.length; j++) {
            var entry = {};
            entry.eventType = scheduleEntry.eventType == 1 ? "cleaning" : "quiet";
            entry.startTime = scheduleEntry.startTime;
            if(scheduleEntry.eventType == 0) {
                entry.endTime = scheduleEntry.endTime;
            }
            entry.state = "server";
            entry.rooms = [];
            entry.id =  guid();
            // uid data monday is 0, neato data monday is 1
            entry.day = scheduleEntry.day[j] - 1 >= 0 ? scheduleEntry.day[j] - 1 : 6;
            uidData.push(entry);
        }
    }
    
    return uidData;
}
