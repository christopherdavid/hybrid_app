
/**
 * Opens a link in an external browser. 
 * @param {String} url the url which has to be opened 
 */
var openExternalLink = function (url) {
    navigator.app.loadUrl(url, { openExternal:true });
};