/**
 * create eventhandler which fires when both (cordova and jquery mobile) frameworks were loaded.
 */

// for desktop usage
var deviceReadyDeferred = $.Deferred();
// on device usage
//var deviceReadyDeferred = $.Deferred();
var jqmReadyDeferred = $.Deferred();
$(document).ready(deviceReadyDeferred.resolve);

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
