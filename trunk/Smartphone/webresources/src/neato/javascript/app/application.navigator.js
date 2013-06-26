/**
 * WorkflowNavigator stores the view model in history.
 * @class Represents a WorkflowNavigator
 * @param {object} parent Reference to the parent object.
 * @param {object} parent Reference to the parent workflow object.
 * @param {object} parent Reference to the parent dataTemplate object.
 */
function WorkflowNavigator(parent, workflow) {
    console.log('create WorkflowNavigator instance')
    var that = this;
    var currentScreen = {
        'id' : '',
        'storeInHistory' : true
    };

    /**
     * Triggered from a view model for workflow navigation
     */
    this.next = function(bundle) {
        console.log('WorkflowNavigator.next()');
        var tempScreenId = getNextValidScreenId();
        if (tempScreenId != null) {
            
            // check if loading indicators and hide them if necessary
            hideLoadingIndicators();
        
            that.loadScreen(tempScreenId, typeof bundle != "undefined" ? bundle : null);
        }
    }
    /**
     * Checks if the workflow condition rules were correct
     * @returns {string} The screenId for the valid condition rule
     */
    var getNextValidScreenId = function(conditionPath) {
        conditionPath = typeof conditionPath != "undefined" ? conditionPath : "conditions"; 
        console.log('WorkflowNavigator.getNextValidScreenId() for screenId:' + currentScreen.id + " conditionPath:" + conditionPath);
        var screenId = null;
        if (workflow[currentScreen.id]) {
            // loop through all navigation rules and check each condition
            for (var r = 0, maxR = workflow[currentScreen.id].navrules.length; r < maxR; r++) {
                var tempRule = workflow[currentScreen.id].navrules[r];
                
                // loop through all conditions in each rule and check if all were valid
                var validConditions = 0;
                
                // Only analyse the rule, if the conditionPath exists
                if (tempRule[conditionPath]){                
                    var maxConditions = tempRule[conditionPath].length;
                    for (var c = 0; c < maxConditions; c++) {
                        var tempCondition = tempRule[conditionPath][c];
                        console.log('check tempCondition key ' + tempCondition.key + ' value ' + tempCondition.value);
                        // TODO: needs to be discussed
                        // conditions with function calls first
                        if (tempCondition.type && tempCondition.type == conditonType.FUNCTION) {
                            tempCondition.func();
                        }
                        // compare conditions with view model
                        else if (parent.viewModel[conditionPath] && parent.viewModel[conditionPath][tempCondition.key] == tempCondition.value) {
                            validConditions++;
                        } else {
                            // skip check for this rule if one condition fails
                            break;
                        }
                    }
                    if (validConditions == maxConditions) {
                        screenId = tempRule.targetScreenId;
                        break;
                    }
                }
            }
        }
        return screenId;
    }
    /**
     * Triggered whenever back button (Android) is pressed or from a view model
     * for workflow navigation
     */
    this.previous = function() {
        console.log('WorkflowNavigator.previous()');
        
        // check if loading indicators and hide them is necessary
        hideLoadingIndicators();
        
        // check if backConditions were set
        if(parent.viewModel.backConditions) {
            var tempScreenId = getNextValidScreenId("backConditions");
            if (tempScreenId != null) {
                // check if history contains an entry with the screenId
                var historyIndex = parent.history.getIndexById(tempScreenId);
                if(historyIndex > -1) {
                    // disable history for current screen
                    currentScreen.storeInHistory = false;
                    // navigate
                    that.loadScreenFromHistory(historyIndex, typeof bundle != "undefined" ? bundle : null);
                } else {
                    that.loadScreen(tempScreenId, typeof bundle != "undefined" ? bundle : null);
                }
            } else {
                defaultBack();
            }
        } else {
            defaultBack();
        }
    }
    function defaultBack() {
        if (parent.history.getLastEntry() != null) {
            // disable history for current screen
            currentScreen.storeInHistory = false;
            // get last screen history index
            var historyIndex = parent.history.getLastIndex();
            // navigate
            that.loadScreenFromHistory(historyIndex, typeof bundle != "undefined" ? bundle : null);
        } else {
            that.exit();
        }
    }
    
    /**
     *  Hides all loading indicators and hints if the screen is left. 
     */
    function hideLoadingIndicators() {
        parent.notification.reset();
    }
    
    /**
     * Confirmation check if the application should really be closed.
     * Closes the application if the user agreed 
     */
    this.exit = function() {
        if (confirm($.i18n.t('confirm.close_app'))) {
            navigator.app.exitApp();
        }
    }
    this.loadScreenFromHistory = function(historyIndex, bundle) {
        console.log('WorkflowNavigator.loadScreenFromHistory(' + historyIndex + ')');
         // always true because it comes from history
        storeInHistory = true;
        
        that.unloadScreen();
        
        // get view model from history
        var tempViewModel = parent.history.getEntryByIndex(historyIndex);
        
        if(typeof workflow[tempViewModel.screenId].clearHistory != "undefined" && workflow[tempViewModel.screenId].clearHistory === true) {
            // clear history
            parent.history.clear();
        }
        
        parent.loadViewModelFromHistory(tempViewModel, bundle, function() {
            parent.loadView(tempViewModel.screenId);
        });
        currentScreen.id = tempViewModel.screenId;
        currentScreen.storeInHistory = storeInHistory;
    }
    
    
    /**
     * loads a screen according to it's screenId
     * @param {string} screenId The id of the current screen which should be
     *                 loaded
     * bundle
     * @param {boolean} [storeInHistory] defines if a screen should be stored
     *                  in workflow history, default is true
     */
    this.loadScreen = function(screenId, bundle, storeInHistory) {
        console.log('WorkflowNavigator.loadScreen(' + screenId + ',' + storeInHistory + ')');
        storeInHistory = typeof storeInHistory != 'undefined' ? storeInHistory : true;
        // check if target page exists
        if (workflow[screenId]) {
            that.unloadScreen();
            if(typeof workflow[screenId].clearHistory != "undefined" && workflow[screenId].clearHistory === true) {
                // clear history
                parent.history.clear();
            } else if(typeof workflow[screenId].clearHistoryAfter != "undefined" && workflow[screenId].clearHistoryAfter === true) {
                // check if history contains an entry with the screenId
                var historyIndex = parent.history.getIndexById(screenId);
                if(historyIndex > -1) {
                    // remove all history entries
                    parent.history.clearTillIndex(historyIndex);
                }
            }
            
            
            console.log('screenId found in workflow');
            parent.loadViewModel(screenId, bundle, function() {
                parent.loadView(screenId);
            });
            currentScreen.id = screenId;
            currentScreen.storeInHistory = storeInHistory;
        } else {
            console.log('screenId NOT found in dataTemplate or workflow');
        }
    }
    /**
     * called before the DOM of a page gets removed
     * handles if a page gets stored in workflow history
     */
    this.unloadScreen = function(curScreen) {
        console.log('WorkflowNavigator.unloadScreen(' + currentScreen.id + ')');
        // check if screen must be stored in history
        if (currentScreen.id != "" && currentScreen.storeInHistory) {
            console.log('store screen with id ' + currentScreen.id);
            // store view model in history and delete it
            parent.history.store(parent.viewModel, currentScreen.id);
            parent.unloadViewModel();
        }
    }
}
