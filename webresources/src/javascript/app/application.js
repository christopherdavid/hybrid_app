/**
 * Application is the main class.
 *
 * @class Represents the main application
 */
function Application() {
    var that = this, workflow, dataTemplate, pageLoadedDeferred, config;
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
        pageTransition : "slide"
    };
    // user object
    this.user = {
        name : "",
    };
    this.loadingArea

    this.robots = [], this.scheduler;
    this.flowNotification

    /**
     * Shows a loading spinner indicating background activity.
     */
    this.showLoadingArea = function(show) {
        if (show) {

            //cue the page loader
            $.mobile.loading('show');
        } else {
            $.mobile.loading('hide');
        }
    }
    /**
     * loads a view according to it's filename
     *
     * @param {string}
     *            fileName The filename which should be loaded
     */
    this.loadView = function(fileName) {
        console.log('loadView ' + fileName);
        $.mobile.changePage(fileName, {
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
    this.loadViewModel = function(modelId, screenId, bundle, fncCallback) {
        console.log('loadViewModel ' + modelId);
        // check if view model for this screen is stored in history
        // otherwise load it
        if (that.history.compareLastEntry(screenId)) {
            console.log('use view model from workflow history');
            that.viewModel = that.history.pop();
            fncCallback();
            if ( typeof that.viewModel.reload != "undefined") {
                that.viewModel.reload();
            }
            // maybe the language has changed since that, so reassign it
            that.viewModel.language = that.language;
        } else {
            resourceHandler.loadJS((modelId + '.js'), function() {
                console.log('create new view model');
                that.viewModel = new resourceHandler.ressources[modelId
                + '.js'].func[modelId](that);
                console.log('new view model ' + that.viewModel.id)
                fncCallback();
                // add screenId to view model (needed for history)
                that.viewModel.screenId = screenId;
                that.viewModel.language = that.language;
                that.viewModel.bundle = bundle;
            });
        }
    }
    /**
     * Removes the view model
     */
    this.unloadViewModel = function() {
        if ( typeof that.viewModel.deinit != "undefined") {
            that.viewModel.deinit();
        }
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
        dataTemplate = that.loadDataTemplate();
        workflow = that.loadWorkflow();

        that.history = new WorkflowHistory(that);
        that.flowNavigator = new WorkflowNavigator(that, workflow, dataTemplate);
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
            console.log('language couldn\'t be detected. Load fallback en-GB');
            that.changeLanguage('en-GB', function() {
                loadFirstPage()
            });
        });
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
                fallbackLng : false
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
Application.prototype.loadDataTemplate = function() {
    // console.log('called empty loadDataTemplate');
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
