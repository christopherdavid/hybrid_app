/**
 * structs contains structure definitions for:
 * - robot: the selected robot and all associated robots
 *  
 */

var statusInformation = {
    messageIcon:"",
    messageText:"",
    startButton:"",
    ui:"",
    robot:""
}

function getRobotStruct() {
    var defaultState = ROBOT_STATE_UNKNOWN;
    return {
        // has to be filled with one robot of UserPluginManager.getAssociatedRobots or RobotPluginManager.getRobotDetail
        robotId:"",
        robotName:"",
        // has to be filled initially with RobotPluginManager.getRobotCleaningState
        robotCurrentState:defaultState,
        robotNewVirtualState:defaultState,
        // has to be filled initially with RobotPluginManager.getSpotDefinition
        spotCleaningAreaLength:"",
        spotCleaningAreaHeight:"",
        // no initial value available 
        cleaningCategory:CLEANING_CATEGORY_ALL,
        cleaningMode:"",
        cleaningModifier:"1",
        stateString:$.i18n.t("robotStateCodes." + defaultState),
        connectionState:""
    }
};


