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
    this.flowNavigator;
    this.notification;
    this.language = '';
    this.history;
    this.communicationWrapper;
    // configuration for the application
    this.config = {
        firstScreen : "start",
        pageTransition : "none",
        pageReverseDirection: false,
        version:"0.6.4.8",
        pluginVersion:"0.6.1.0",
        fallbackLanguage:"en-GB",
        viewPath:"",
        device:"",
        emailRegEx: /^[A-Z0-9._%+-]+@[A-Z0-9.-]+\.[A-Z]{2,4}$/i
    };
    this.scheduler;
    this.orientation = {
        landscape: true,
        portrait: true
    };
    
    
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
        initDeviceConfig();
        workflow = that.loadWorkflow();

        that.history = new WorkflowHistory(that);
        that.flowNavigator = new WorkflowNavigator(that, workflow);
        that.communicationWrapper = new WorkflowCommunication(that);
        that.notification = new WorkflowNotification(that);
        that.notification.init();
        
        that.communicationWrapper.setDataValue("robotList", ko.observableArray([]));
        that.communicationWrapper.setDataValue("selectedRobot", ko.observable({}));
        robotUiStateHandler.current = ko.observable("");
        robotUiStateHandler.current(ko.mapping.fromJS(statusInformation), null, robotUiStateHandler.current)
        
        //that.communicationWrapper.mapDataValue("selectedRobot", getRobotStruct());
        
        
        // that.changeLanguage('de-de', function() { loadFirstPage(); });
        // get last language selection from local storage
        window.plugins.globalization.getLocaleName(function(locale) {
            console.log('plugin.getLocaleName: ' + locale.value);
            locale.value = locale.value.replace('_', '-');
            that.changeLanguage(locale.value, function() {
                loadFirstPage();
            });
        }, function() {
            console.log('language couldn\'t be detected. Load fallback');
            that.changeLanguage(that.config.fallbackLanguage, function() {
                loadFirstPage();
            });
        });
    }
    
    /**
     * Load the first page when the application starts
     */
    function loadFirstPage() {
        // check if user was already logged in before
        var username = that.communicationWrapper.getFromLocalStorage('username');
        if(username != null) {
            var tDeffer = that.communicationWrapper.exec(UserPluginManager.isUserLoggedIn, [username], { type: notificationType.NONE, message: ""});
            tDeffer.done(function(result){
                if(result == true) {
                    var tDeffer2 = that.communicationWrapper.exec(UserPluginManager.getUserDetail, [username], {});
                    tDeffer2.done(function(result){
                        that.communicationWrapper.setDataValue("user", result);
                        // register for push notifications (from server)
                        that.notification.registerForRobotMessages();
                        
                        // register for notifications from robot if app is running
                        that.notification.registerForRobotNotifications();
                        that.flowNavigator.loadScreen("robotSelection", robotScreenCaller.LOGIN);
                    });
                    
                    // in case of an error navigate to first screen
                    tDeffer2.fail(function(error, notificationOptions, errorHandled){
                        errorHandled.resolve();
                        that.flowNavigator.loadScreen(that.config.firstScreen, null);
                    });
                    
                } else {
                    // not logged in anymore or session expired, load first screen
                    that.flowNavigator.loadScreen(that.config.firstScreen, null);
                }
            });
            
            // in case of an error navigate to first screen
            tDeffer.fail(function(error, notificationOptions, errorHandled) {
                errorHandled.resolve();
                that.communicationWrapper.saveToLocalStorage('username', null); 
                that.flowNavigator.loadScreen(that.config.firstScreen, null);
            });
        } else {
            // load first page from workflow
            that.flowNavigator.loadScreen(that.config.firstScreen, null);
        }
    }
    
    /**
     * loads a view according to it's filename
     *
     * @param {string}
     *            fileName The filename which should be loaded
     */
    this.loadView = function(screenId) {
        console.log('loadView ' + screenId);
        $.mobile.changePage((that.config.viewPath + screenId + ".html"), {
            transition : that.config.pageTransition,
            reverse : that.config.pageReverseDirection
        });
        // disable reverse animation
        that.config.pageReverseDirection = false;
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
    
    this.loadViewModelFromHistory = function(tempViewModel, bundle, fncCallback) {
        console.log('loadViewModelFromHistory ');
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
        // clear callbacks notification class
        that.notification.clearStatusListener();
        
        that.viewModel = null;
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
                fallbackLng : that.config.fallbackLanguage
            }).done(function() {
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
    
    console.log('Loading CSS');
    resourceHandler.loadCSS(deviceSize.getResolution() + "-res.css");
    
    if (!window.app) {
        window.app = new Application();
    }

    // splash screen is visible load next page
    setTimeout(function() {
        app.init()
    }, 2000);
});
