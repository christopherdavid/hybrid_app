/**
 * Singleton for resource loading
 * load external JavaScript and Sylesheet files by including the links in the 
 * header. JavaScript files get cached and could be reused without loading them 
 * again. For some filetypes there is also a callback which gets called when 
 * loading is done.
 */
var resourceHandler = {
    ressources : {},
    oDomHeader : null,
    func : {},
    //maxEntries:5,
    posFile : [],
    viewModelPath:"javascript/viewmodel/",

    getDomHeader : function() {
        if (!resourceHandler.oDomHeader) {
            resourceHandler.oDomHeader = document.getElementsByTagName('head')[0];
        }
        return resourceHandler.oDomHeader;
    },
    /**
     * Loads an external Stylesheet file
     * @param {string} src the relative file path to 'stylesheets/css/'
     */
    loadCSS: function(src) {
        var s = document.createElement("link");
        s.setAttribute("rel", "stylesheet");
        s.setAttribute("type", "text/css");
        s.setAttribute("href", 'stylesheets/css/' + src)
        resourceHandler.getDomHeader().appendChild(s);
    },
    /**
     * Loads an external JavaScript and executes the callback when finished 
     * @param {string} src the relative file path to 'javascript/viewmodel/'
     * @param {function} callback The callback called, when loading is done
     */
    loadJS : function(src, callback) {
        console.log('called loadJS(' + src + ')');
        if (!resourceHandler.ressources[src]) {
        
            var s=document.createElement('script');
            s.setAttribute("type","text/javascript");
            s.setAttribute("src", this.viewModelPath + src);
            
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
    registerFunction : function(src, functionObject) {
        //console.log('registerFunction');
        if (resourceHandler.ressources[src]) {            
            resourceHandler.ressources[src].func = functionObject;

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
