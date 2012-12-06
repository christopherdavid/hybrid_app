/**
 * WorkflowNavigator stores the view model in history.
 * @class Represents a WorkflowNavigator
 * @param {object} parent Reference to the parent object.
 * @param {object} parent Reference to the parent workflow object.
 * @param {object} parent Reference to the parent dataTemplate object.
 */
function WorkflowNavigator(parent, workflow, dataTemplate) {
    console.log('create WorkflowNavigator instance')
    var that = this;
    var currentScreen = {
        'id' : '',
        'storeInHistory' : true
    };

    /**
     * Triggered from a view model for workflow navigation
     */
    this.next = function() {
        console.log('WorkflowNavigator.next()');
        var tempScreenId = getNextValidScreenId();
        if (tempScreenId != null) {
            that.loadScreen(tempScreenId);
        }
    }
    /**
     * Checks if the workflow condition rules were correct
     * @returns {string} The screenId for the valid condition rule
     */
    var getNextValidScreenId = function() {
        console.log('WorkflowNavigator.getNextValidScreenId() for screenId:' + currentScreen.id);
        var screenId = null;
        if (workflow[currentScreen.id]) {
            // loop through all navigation rules and check each condition
            for (var r = 0, maxR = workflow[currentScreen.id].navrules.length; r < maxR; r++) {
                var tempRule = workflow[currentScreen.id].navrules[r];
                // loop through all conditions in each rule and check if all were valid
                var validConditions = 0;
                var maxConditions = tempRule.conditions.length;
                for (var c = 0; c < maxConditions; c++) {
                    var tempCondition = tempRule.conditions[c];
                    console.log('check tempCondition key ' + tempCondition.key + ' value ' + tempCondition.value);
                    // TODO: needs to be discussed
                    // conditions with function calls first
                    if (tempCondition.type && tempCondition.type == conditonType.FUNCTION) {
                        tempCondition.func();
                    }
                    // compare conditions with view model
                    else if (parent.viewModel.conditions && parent.viewModel.conditions[tempCondition.key] == tempCondition.value) {
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
        return screenId;
    }
    /**
     * Triggered whenever back button (Android) is pressed or from a view model
     * for workflow navigation
     */
    this.previous = function() {
        console.log('WorkflowNavigator.previous()');
        if (parent.history.getLastEntry() != null) {
            // disable history for current screen
            currentScreen.storeInHistory = false;
            // get last screen values (store strings to prevent object reference)
            var tempScreenId = parent.history.getLastEntry().screenId;
            // always true because it comes from history
            var tempScreenHistory = true;
            // navigate
            that.loadScreen(tempScreenId, tempScreenHistory);
        } else {
            that.exit();
        }
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
    /**
     * loads a screen according to it's screenId
     * @param {string} screenId The id of the current screen which should be
     *                 loaded
     * @param {boolean} [storeInHistory] defines if a screen should be stored
     *                  in workflow history, default is true
     */
    this.loadScreen = function(screenId, storeInHistory) {
        console.log('WorkflowNavigator.loadScreen(' + screenId + ',' + storeInHistory + ')');
        storeInHistory = typeof storeInHistory != 'undefined' ? storeInHistory : true;
        if (dataTemplate[screenId] && workflow[screenId]) {
            that.unloadScreen();
            console.log('screenId found in dataTemplate and workflow');
            parent.loadViewModel(dataTemplate[screenId].model, screenId, function() {
                parent.loadView(dataTemplate[screenId].view);
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
