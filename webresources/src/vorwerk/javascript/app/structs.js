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
};

function getRobotStruct() {
    var defaultState = ROBOT_STATE_UNKNOWN;
    return {
        // has to be filled with one robot of UserPluginManager.getAssociatedRobots or RobotPluginManager.getRobotDetail
        robotId:"",
        robotName:"",
        displayName:"",
        // has to be filled initially with RobotPluginManager.getRobotCurrentState
        robotCurrentState:defaultState,
        robotNewVirtualState:defaultState,
        // has to be filled initially with RobotPluginManager.getSpotDefinition
        spotCleaningAreaLength:"",
        spotCleaningAreaHeight:"",
        // has to be filled initially with RobotPluginManager.getRobotCurrentStateDetails
        cleaningCategory:CLEANING_CATEGORY_ALL,
        cleaningMode:CLEANING_MODE_ECO,
        cleaningModifier:"1",
        stateString:$.i18n.t("robotStateCodes." + visualState[defaultState]),
        connectionState:"",
        robotOnline:null,
        visualOnline:null,
        robotIsDocked:0,
        clockIsSet:0,
        dockHasBeenSeen:0,
        isCharging:0,
        crntErrorCode:0
    };
};


