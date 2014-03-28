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
// new ui states which were shown after a specified timeout (e.g. 10s) out when robot has stopped
var ROBOT_UI_STATE_STOPPED_MANUAL = 20021;
var ROBOT_UI_STATE_STOPPED_ALL = 20022;
var ROBOT_UI_STATE_STOPPED_SPOT = 20023;
var ROBOT_UI_STATE_STOPPED_WAITED_MANUAL = 20031;
var ROBOT_UI_STATE_STOPPED_WAITED_ALL = 20032;
var ROBOT_UI_STATE_STOPPED_WAITED_SPOT = 20033;
var ROBOT_UI_STATE_PAUSED_MANUAL = 20041;
var ROBOT_UI_STATE_PAUSED_ALL = 20042;
var ROBOT_UI_STATE_PAUSED_SPOT = 20043;
var ROBOT_UI_STATE_CLEANING_MANUAL = 20051;
var ROBOT_UI_STATE_CLEANING_ALL = 20052;
var ROBOT_UI_STATE_CLEANING_SPOT = 20053;
var ROBOT_UI_STATE_CLEANING_TAP_MANUAL = 20061;
var ROBOT_UI_STATE_ROBOT_OFFLINE = 20062;




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

visualState[ROBOT_UI_STATE_STOPPED_MANUAL] = "stopped_manual";
visualState[ROBOT_UI_STATE_STOPPED_ALL] = "stopped_all";
visualState[ROBOT_UI_STATE_STOPPED_SPOT] = "stopped_spot";
visualState[ROBOT_UI_STATE_STOPPED_WAITED_MANUAL] = "stopped_waited_manual";
visualState[ROBOT_UI_STATE_STOPPED_WAITED_ALL] = "stopped_waited_all";
visualState[ROBOT_UI_STATE_STOPPED_WAITED_SPOT] = "stopped_waited_spot";
visualState[ROBOT_UI_STATE_PAUSED_MANUAL] = "paused_manual";
visualState[ROBOT_UI_STATE_PAUSED_ALL] = "paused_all";
visualState[ROBOT_UI_STATE_PAUSED_SPOT] = "paused_spot";
visualState[ROBOT_UI_STATE_CLEANING_MANUAL] = "cleaning_manual";
visualState[ROBOT_UI_STATE_CLEANING_ALL] = "cleaning_all";
visualState[ROBOT_UI_STATE_CLEANING_SPOT] = "cleaning_spot";

visualState[ROBOT_UI_STATE_CLEANING_TAP_MANUAL] = "cleaning_tap_manual";
visualState[ROBOT_UI_STATE_ROBOT_OFFLINE] = "robot_offline";
