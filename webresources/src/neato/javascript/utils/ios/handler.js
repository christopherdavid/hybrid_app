/**
 * create eventhandler which fires when both (cordova and jquery mobile) frameworks were loaded.
 */

var deviceReadyDeferred = $.Deferred();
var jqmReadyDeferred = $.Deferred();
document.addEventListener('deviceReady', deviceReadyDeferred.resolve, false);

// trigger event when both frameworks were ready
$.when(deviceReadyDeferred, jqmReadyDeferred).then(function() {
    $(document).trigger('frameworksReady');
});

/**
 * To override default jQuery Mobile settings, bind to mobileinit
 */

$(document).one('mobileinit', function() {
    jqmReadyDeferred.resolve();
    // Used for cross domain support
    $.extend($.mobile, {
        allowCrossDomainPages : true
    });
    $.extend($.mobile.support, {
        cors : true
    });
}); 