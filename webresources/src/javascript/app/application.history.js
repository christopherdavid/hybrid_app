/**
 * WorkflowHistory stores the view model in history.
 * @class Represents a WorkflowHistory
 * @param {object} parent Reference to the parent object.  
 */
function WorkflowHistory(parent) {
    console.log('create WorkflowHistory instance')
    var that = this, entries = [];
    
    /**
     * Compares the screenId with the screenId of the last history entry 
     * @param {string} viewModelId The id of the current view model
     * @return {boolean} true if screenId matches otherwise false
     */
    this.compareLastEntry = function(screenlId) {
        return (entries.length > 0  
            && entries[entries.length -1].screenId == screenlId);
    }
    
    /**
     * Returns the last entry from history 
     * @return {object} Last entry in history or null if there were no entries
     */
    this.getLastEntry = function() {
        return entries.length > 0  ? entries[entries.length -1] : null;
    }
    
    /**
     * Removes last entry from history and returns the object 
     * @return {object} Stored view model or null if there were no entries
     */
    this.pop = function() {
        return entries.pop() || null;
    }
    
    /**
     * Stores the view model in history
     * only stores the view model if it isn't already stored in history
     * @param {object} viewModel The current view model of the application
     * @param {string} screenId The screenId of the current screen
     */
    this.store = function(viewModel, screenId) {
        // check if entry is already stored in history
        if(entries.length == 0 || (entries.length > 0 
            && entries[entries.length - 1].screenId != screenId)) {
            entries.push(viewModel);
        }
    }
}
