/**
 * Application is the main class.
 *
 * @class Represents the main application
 */
function Application() {
    var that = this, workflow, pageLoadedDeferred, config;
    this.viewModel = {
        id : 'empty'
    };
    this.flowNavigator
    this.language = '';
    this.history;
    this.communicationWrapper;
    // configuration for the application
    this.config = {
        firstScreen : "s1-1",
        pageTransition : "none"
    };
    // user object
    this.user = {
        name : "",
    };

    this.scheduler;
    this.flowNotification;
    
    // loading
    this.$loadingSpinner;
    this.$notificationArea;

    /**
     * Shows a loading spinner indicating background activity.
     * There are two types of spinner. One is shown on the center of the screen and has no text.
     * The other one is a notification bar on top of the screen (below the header)
     * @param {boolean} show Flag to determine if the area should be shown or not
     * @param {enumeration} type The notificationType enumeration that determines which kind of indicator should be shown.
     * @param {string} message The message to be displayed when using OPERATION or HINT notification types.
     * @param {boolean} force Flag that forces the action (i.e. hide the notification)
     */
    this.showLoadingArea = function(show, type, message, force) {
        if (show) {
            
            // show the simple spinner by default
            if (!type){
                that.$loadingSpinner.show();
            }
            
            //cue the page loader
            switch(type){
                case notificationType.SPINNER:
                    that.$loadingSpinner.show();
                    break;
                case notificationType.OPERATION:
                case notificationType.HINT:
                    that.$notificationArea.notificationbar("show", message, type);
                    break;
            }
        } else {
            
            if (force){
                // hide all notification displays
                that.$loadingSpinner.hide();
                that.$notificationArea.notificationbar("hide", type, force);
            }
            else{
                // hide the simple spinner by default
                if (!type){
                    that.$loadingSpinner.hide();
                }
                
                switch(type) {
                    case notificationType.SPINNER:
                        that.$loadingSpinner.hide();
                        break;
                    case notificationType.OPERATION:
                    case notificationType.HINT:
                        that.$notificationArea.notificationbar("hide", type);
                        break;                                                
                }
            }
        }
    }
    
    /**
     * Shows an error message on the screen (and blocks all other interaction).
     * Whenever there's an error (i.e. "Wifi lost", "User name not valid") there will be an error Popup 
     * in the middle of the screen with an "OK" button to be dismissed by the user. The other content on the screen is blocked.
     */
    this.showError = function (error){//errorTitle, errorText, callback) {
    	
    	//TODO fix that! there is no result...
        alert("An Error occurred while contacting the server:\n" + error.errorMessage);
        console.log("error: " + result.errorCode + " msg: " + result.errorMessage);        
    }
    
    /**
     * loads a view according to it's filename
     *
     * @param {string}
     *            fileName The filename which should be loaded
     */
    this.loadView = function(screenId) {
        console.log('loadView ' + screenId);
        $.mobile.changePage((screenId + ".html"), {
            transition : that.config.pageTransition
        });
    }
    /**
     * Loads the view model creates a new view model object or uses a history
     * stored one.
     *
     * @param {string}
     *            modelId The id of the view model which should be loaded
     * @param {string}
     *            screenId The id of the current screen which should be mapped
     *            to the view model (needed for history).
     * @param {function}
     *            fncCallback Callback method which should be called after the
     *            view model has been loaded.
     */
    this.loadViewModel = function(screenId, bundle, fncCallback) {
        console.log('loadViewModel ' + screenId + '_ViewModel');
        resourceHandler.loadJS((screenId + '_ViewModel.js'), function() {
            console.log('create new view model ' + screenId + '_ViewModel');
            that.viewModel = new resourceHandler.ressources[screenId + '_ViewModel.js'].func(that);
            fncCallback();
            // add screenId to view model (needed for history)
            that.viewModel.screenId = screenId;
            that.viewModel.language = that.language;
            that.viewModel.bundle = bundle;
        });
    }
    this.loadViewModelFromHistory = function(tempViewModel, bundle,fncCallback) {
        console.log('loadViewModelFromHistory ' + JSON.stringify(tempViewModel));
        that.viewModel = tempViewModel;
        fncCallback();
        if ( typeof that.viewModel.reload != "undefined") {
            that.viewModel.reload();
        }
        // maybe the language has changed since that, so reassign it
        that.viewModel.language = that.language;
    }
    /**
     * Removes the view model
     */
    this.unloadViewModel = function() {
        if ( typeof that.viewModel.deinit != "undefined") {
            that.viewModel.deinit();
        }
        // clear callbacks in communication wrapper 
        that.communicationWrapper.callbacks = {};
        that.viewModel = null;
    }
    /**
     * initialize Application configuration
     * <ul>
     * <li>load configuration from file or db</li>
     * <li>load values from local storage</li>
     * <li>user name, last selected language</li>
     * <li>load workflow data</li>
     * </ul>
     */
    this.init = function() {
        workflow = that.loadWorkflow();

        that.history = new WorkflowHistory(that);
        that.flowNavigator = new WorkflowNavigator(that, workflow);
        that.communicationWrapper = new WorkflowCommunication(that);
        // that.flowNotification = new WorkflowNotification(that);
        // that.changeLanguage('de-de', function() { loadFirstPage(); });
        // get last language selection from local storage
        window.plugins.globalization.getLocaleName(function(locale) {
            console.log('plugin.getLocaleName: ' + locale.value);
            locale.value = locale.value.replace('_', '-');
            that.changeLanguage(locale.value, function() {
                loadFirstPage()
            });
        }, function() {
            console.log('language couldn\'t be detected. Load fallback en-US');
            that.changeLanguage('en-US', function() {
                loadFirstPage()
            });
        });
        
        // Initialize the loading spinner and notification area
        initializeUserFeedbackControls();
    }
    
    function initializeUserFeedbackControls(){
        that.$loadingSpinner = $('#loadingArea');
        that.$notificationArea = $('#notificationArea');
    }
    
    /**
     * Load the first page when the application starts
     */
    function loadFirstPage() {
        // load first page from workflow
        that.flowNavigator.loadScreen(that.config.firstScreen, null);
    }


    this.getUserName = function() {
        return that.user.name;
    }

    this.setUserName = function(name) {
        that.user.name = name;
    }
    /**
     * Handles the back button key event
     *
     * @param {event}
     *            e The event parameter
     */
    this.onBackKeyDown = function(e) {
        // console.log('back button event')
        that.flowNavigator.previous();
    }
    /**
     * changes the language and triggers binding
     *
     * @param {String}
     *            sLang the language code for the translation file e.g. de-DE
     */
    this.changeLanguage = function(sLang, fncCallback) {
        // handle initial empty language and set options for the translation
        // tool
        if (that.language == '') {
            // If load option is set to 'current' i18next will load the current
            // set language
            // (this could be a specific (en-US) or unspecific (en) resource
            // file).
            $.i18n.init({
                lng : sLang,
                fallbackLng : 'en-GB'
            }, function() {
                that.language = ko.observable($.i18n.lng());
                console.log('init lang ' + $.i18n.lng() + ' app: ' + that.language());
                if ( typeof fncCallback === 'function') {
                    fncCallback();
                }
            });
        } else {
            $.i18n.setLng(sLang, function() {
                console.log('changed language to ' + $.i18n.lng());
                that.language($.i18n.lng());
                console.log('set lang ' + $.i18n.lng() + ' app: ' + that.language());
                if ( typeof fncCallback === 'function') {
                    fncCallback();
                }
            });
        }
    }
    /**
     * bind to back button pressed event handler
     */
    document.addEventListener('backbutton', that.onBackKeyDown, false);

    /**
     * bind to pagehinde event handler remove data binding to the current page
     * before it gets removed
     */
    $(document).on('pagehide', '[data-role=page]', function(event) {
        // remove binding. ignore splash screen
        if (event.target.id != 'splash') {
            console.log('pagehide remove binding from DOM: ' + event.target.id);
            ko.cleanNode(event.target);
        } else if (event.target.id == 'splash') {
            jQuery(event.target).remove();
        }
    });

    /**
     * bind to pagebeforeshow event handler add data binding to the current page
     * before it gets shown
     */
    $(document).on('pagebeforeshow', '[data-role=page]', function(event) {
        // apply binding. ignore splash screen
        if (event.target.id != 'splash') {
            console.log('pagebeforeshow add binding to DOM:' + event.target.id)
            if ( typeof that.viewModel.init != "undefined") {
                that.viewModel.init();
            }
            ko.applyBindings(that.viewModel, event.target);
        }
    });
};

Application.prototype.loadWorkflow = function() {
    // console.log('called empty loadWorkflow');
    return {}
};

/**
 * bind to frameworksReady event handler (cordova and jquery mobile were ready)
 * initialize Application after 2s timeout
 */
$(document).one('frameworksReady', function() {
    console.log('Application frameworksReady!');
    if (!window.app) {
        window.app = new Application();
    }

    // splash screen is visible load next page
    setTimeout(function() {
        app.init()
    }, 2000);
});
