var conditonType = {
    FUNCTION : "0",
};

var textTarget = {
    INNERHTML : "0",
    FIRSTCHILD : "1",
    VALUE : "2",
    PLACEHOLDER : "3",
    jqLinkButton : "4",
    SLIDER_LABEL_A : "5",
    SLIDER_LABEL_B : "6"
};

/**
 * Enumeration defining the caller of the robot selection screen 
 */
var robotScreenCaller = {
    REGISTER : "register",
    LOGIN : "login",
    CHANGE : "change",
    MANAGE : "manage",
    DELETE : "delete"
}

/**
 * Enumeration defining the type of notifications. 
 */
var notificationType = {
    SPINNER : "0", // simple loading spinner in the center of the screen
    OPERATION : "1", // Notification area with text display and spinner, will be displayed until dismissed. 
    HINT: "2", // short displayed hint text (will be visible for ~ 2 sec. and disappear automatically)
    NONE:"3", // no notification will be displayed 
    GETREADY:"4", // Notification area with text display and get ready animation, will be displayed until dismissed.
    WAKEUP:"5" // Notification area with text display and wake up animation, will be displayed until dismissed.
}

var dialogType = {
    INFO : "1",
    WARNING : "2",
    ERROR : "3"
}

var DEVICETYPE = {
    "iOS":0,
    "Android":1
};

var ROBOT_UI_STATE_ERROR    = 20001;
var ROBOT_UI_STATE_WAIT     = 20002;
var ROBOT_UI_STATE_WAKEUP   = 20003;
var ROBOT_UI_STATE_GETREADY = 20004;
var ROBOT_UI_STATE_AWAKE    = 20005;
var ROBOT_UI_STATE_DISABLED = 20006;
var ROBOT_UI_STATE_CONNECTING = 20007;

var visualState = {};
// robot states from API
visualState[ROBOT_STATE_UNKNOWN] = "unknown";
visualState[ROBOT_STATE_CLEANING] = "cleaning";
visualState[ROBOT_STATE_IDLE] = "idle";
visualState[ROBOT_STATE_CHARGING] = "charging";
visualState[ROBOT_STATE_STOPPED] = "stopped";
visualState[ROBOT_STATE_STUCK] = "stucked";
visualState[ROBOT_STATE_PAUSED] = "paused";
visualState[ROBOT_STATE_RESUMED] = "resumed";
visualState[ROBOT_STATE_ON_BASE] = "inbase";
visualState[ROBOT_STATE_MANUAL_CLEANING] = "manual";
visualState[ROBOT_STATE_MANUAL_PLAY_MODE] = "play";
// UI states
visualState[ROBOT_UI_STATE_ERROR] = "error";
visualState[ROBOT_UI_STATE_WAIT] = "waiting";
visualState[ROBOT_UI_STATE_WAKEUP] = "wakeup";
visualState[ROBOT_UI_STATE_GETREADY] = "getready";
visualState[ROBOT_UI_STATE_AWAKE] = "awake";
visualState[ROBOT_UI_STATE_DISABLED] = "disabled"; 
visualState[ROBOT_UI_STATE_CONNECTING] = "connecting";
