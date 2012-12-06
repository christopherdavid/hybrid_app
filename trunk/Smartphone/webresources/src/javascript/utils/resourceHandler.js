/**
 * load external javascript file and include it in header
 * @param src
 * @param callback
 */

var resourceHandler = {
    ressources : {},
    oDomHeader : null,
    func : {},
    //maxEntries:5,
    posFile : [],

    getDomHeader : function() {
        if (!resourceHandler.oDomHeader) {
            resourceHandler.oDomHeader = document.getElementsByTagName('head')[0];
        }
        return resourceHandler.oDomHeader;
    },
    loadJS : function(src, callback) {
        console.log('called loadJS(' + src + ')');
        if (!resourceHandler.ressources[src]) {
            var s = document.createElement('script');
            // store reference for deletion
            resourceHandler.ressources[src] = {
                dom : s,
                func : {}
            };
            resourceHandler.getDomHeader().appendChild(s);
            //document.getElementsByTagName('head')[0].appendChild(s);
            s.onload = function() {
                //console.log('\tonload done');
                //callback if existent.
                if ( typeof callback == 'function') {
                    callback();
                }
                // Wipe callback, to prevent multiple calls.
                callback = null;
            }
            s.onreadystatechange = function() {
                //console.log('\treadyState: ' + s.readyState)
                if (s.readyState == 4 || s.readyState == 'complete') {
                    if ( typeof callback == 'function') {
                        callback();
                    }
                    // Wipe callback, to prevent multiple calls.
                    callback = null;
                }
            }
            s.src = 'javascript/viewmodel/' + src;
        } else {
            console.log('file already loaded. using cached one');
            //callback if existent.
            if ( typeof callback == 'function') {
                callback();
            }
            // Wipe callback, to prevent multiple calls.
            callback = null;
        }
    },
    registerFunction : function(src, functionName, functionObject) {
        //console.log('registerFunction');
        if (resourceHandler.ressources[src]) {
            resourceHandler.ressources[src].func[functionName] = functionObject;
            //resourceHandler.func[functionName] = resourceHandler.ressources[src].fnc[functionName];

            /* temporary removed unload call
             if(resourceHandler.posFile.length > resourceHandler.maxEntries) {
             // remove first oldest entry
             resourceHandler.unloadJS(resourceHandler.posFile[0]);
             resourceHandler.posFile.shift();
             }
             */
            resourceHandler.posFile.push('src')
        }

    },
    unloadJS : function(src) {
        if (resourceHandler.ressources[src]) {
            console.log('called unloadJS(' + src + ')');
            resourceHandler.getDomHeader().removeChild(resourceHandler.ressources[src].dom);
            resourceHandler.posFile.shift();

            // remove reference
            //resourceHandler.ressources[src].func = null;
            resourceHandler.ressources[src] = null;
            delete resourceHandler.ressources[src];
        } else {
            console.log('called unloadJS(' + src + ') file not found!');
        }
    }
}
