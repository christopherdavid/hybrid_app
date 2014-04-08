/**
 * WorkflowHistory stores the view model in history.
 * @class Represents a WorkflowHistory
 * @param {object} parent Reference to the parent object.  
 */
function WorkflowHistory(parent) {
    console.log('create WorkflowHistory instance');
    var that = this, entries = [];
    
    /**
     * Compares the screenId with the screenId of the last history entry 
     * @param {string} screenlId The id of the current screen
     * @return {boolean} true if screenId matches otherwise false
     */
    this.compareLastEntry = function(screenlId) {
        return (entries.length > 0  
            && entries[entries.length -1].screenId == screenlId);
    };
    
    /**
     * checks if an entry in history matches the screenId and returns its index
     * @param {string} screenlId The id of the current screen
     * 
     */
    this.getIndexById = function(screenlId) {
        for(var i = entries.length - 1; i >= 0; i--) {
            if(entries[i].screenId == screenlId) {
                return i;
            }
        }
        return -1;
    };
    
    this.getEntryByIndex = function(index) {
        var temp = null;
        // remove all entries after (including) the current one
        if(entries.length > 0) {
            temp = entries[index];
            that.clearTillIndex(index);
        }
        return temp;
    };
    
    this.clearTillIndex = function(index) {
        for(var i = entries.length - 1; i >= index; i--) {
            if(typeof entries[i].destroy != "undefined") {
                entries[i].destroy();
            }
            delete entries[i];
        }
        entries = entries.slice(0, index);
    };
    
    /**
     * Clear the history 
     */
    this.clear = function() {
        for(var i = entries.length - 1; i >= 0; i--) {
            if(typeof entries[i].destroy != "undefined") {
                entries[i].destroy();
            }
            delete entries[i];
        }
        entries.length = 0;
    };
    
    /**
     * Returns the last entry from history 
     * @return {object} Last entry in history or null if there were no entries
     */
    this.getLastEntry = function() {
        return entries.length > 0  ? entries[entries.length -1] : null;
    };
    
    this.getLastIndex = function() {
        return entries.length > 0  ? (entries.length -1) : -1;
    };
    
    /**
     * Removes last entry from history and returns the object 
     * @return {object} Stored view model or null if there were no entries
     */
    this.pop = function() {
        return entries.pop() || null;
    };
    
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
    };
}
