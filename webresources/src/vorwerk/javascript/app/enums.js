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
};

var pageState = {
    REGISTER: "register",
    EDIT: "edit", // single view edit
    CHANGE: "change" // flow with changes
};

var dataImage = {
    MENU: "menu",
    ADD: "add",
    CANCEL: "cancel",
    OK: "ok",
    BACK: "back",
    NEXT: "next"
};

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
};

var dialogType = {
    INFO : "1",
    WARNING : "2",
    ERROR : "3"
};

var DEVICETYPE = {
    "iOS":0,
    "Android":1
};

var keyString = {};
keyString[CLEANING_MODE_ECO] = "eco";
keyString[CLEANING_MODE_NORMAL] = "normal";


var ROBOT_UI_STATE_WAIT     = 20001;
var ROBOT_UI_STATE_CONNECTING = 20002;
var ROBOT_UI_STATE_ROBOT_OFFLINE = 20003;
var ROBOT_UI_STATE_ON_BASE = 20004;
var ROBOT_UI_STATE_RETURN_TO_BASE = 20005;
var ROBOT_UI_STATE_RETURN_TO_START = 20006;
var ROBOT_UI_STATE_CHARGING = 20007;
var ROBOT_UI_STATE_ERROR    = 20008;
var ROBOT_UI_STATE_STUCKED = 20009;
var ROBOT_UI_STATE_RETURNING2BASE = 20010;
var ROBOT_UI_STATE_RETURNING2START = 20011;
var ROBOT_UI_STATE_DOCK_PAUSED = 20012;

var ROBOT_UI_ERRORALERT_CLEAR = 22000;
var ROBOT_UI_ERRORCODE_START = 21000;

// new ui states depending on category
var ROBOT_UI_STATE_IDLE_MANUAL      = "2" + CLEANING_CATEGORY_MANUAL + "00" + ROBOT_STATE_IDLE;
var ROBOT_UI_STATE_CLEANING_MANUAL  = "2" + CLEANING_CATEGORY_MANUAL + "00" + ROBOT_STATE_CLEANING;
var ROBOT_UI_STATE_PAUSED_MANUAL    = "2" + CLEANING_CATEGORY_MANUAL + "00" + ROBOT_STATE_PAUSED;

var ROBOT_UI_STATE_IDLE_ALL         = "2" + CLEANING_CATEGORY_ALL + "00" + ROBOT_STATE_IDLE;
var ROBOT_UI_STATE_CLEANING_ALL     = "2" + CLEANING_CATEGORY_ALL + "00" + ROBOT_STATE_CLEANING;
var ROBOT_UI_STATE_PAUSED_ALL       = "2" + CLEANING_CATEGORY_ALL + "00" + ROBOT_STATE_PAUSED;

var ROBOT_UI_STATE_IDLE_SPOT        = "2" + CLEANING_CATEGORY_SPOT + "00" + ROBOT_STATE_IDLE;
var ROBOT_UI_STATE_CLEANING_SPOT    = "2" + CLEANING_CATEGORY_SPOT + "00" + ROBOT_STATE_CLEANING;
var ROBOT_UI_STATE_PAUSED_SPOT      = "2" + CLEANING_CATEGORY_SPOT + "00" + ROBOT_STATE_PAUSED;

var visualState = {};
visualState[ROBOT_STATE_UNKNOWN] = "unknown";
visualState[ROBOT_STATE_IDLE] = "idle";
visualState[ROBOT_USER_MENU_STATE] = "menu";
visualState[ROBOT_STATE_CLEANING] = "cleaning";
visualState[ROBOT_STATE_SUSPENDED_CLEANING] = "suspend";
visualState[ROBOT_STATE_PAUSED] = "paused";
visualState[ROBOT_STATE_MANUAL_CLEANING] = "manual";
visualState[ROBOT_STATE_DOCK_PAUSED] = "dockpaused";

// UI states
visualState[ROBOT_UI_STATE_WAIT] = "waiting";
visualState[ROBOT_UI_STATE_CONNECTING] = "connecting";
visualState[ROBOT_UI_STATE_ROBOT_OFFLINE] = "robot_offline";
visualState[ROBOT_UI_STATE_ON_BASE] = "inbase";
visualState[ROBOT_UI_STATE_RETURN_TO_BASE] = "drive_to_base";
visualState[ROBOT_UI_STATE_RETURN_TO_START] = "drive_to_start";
visualState[ROBOT_UI_STATE_CHARGING] = "charging";
visualState[ROBOT_UI_STATE_ERROR] = "error";
visualState[ROBOT_UI_STATE_STUCKED] = "stucked";

visualState[ROBOT_UI_STATE_IDLE_MANUAL] = "idle_manual";
visualState[ROBOT_UI_STATE_IDLE_ALL] = "idle_all";
visualState[ROBOT_UI_STATE_IDLE_SPOT] = "idle_spot";

visualState[ROBOT_UI_STATE_PAUSED_MANUAL] = "paused_manual";
visualState[ROBOT_UI_STATE_PAUSED_ALL] = "paused_all";
visualState[ROBOT_UI_STATE_PAUSED_SPOT] = "paused_spot";
visualState[ROBOT_UI_STATE_DOCK_PAUSED] = "paused_dock";

visualState[ROBOT_UI_STATE_CLEANING_MANUAL] = "cleaning_manual";
visualState[ROBOT_UI_STATE_CLEANING_ALL] = "cleaning_all";
visualState[ROBOT_UI_STATE_CLEANING_SPOT] = "cleaning_spot";

visualState[ROBOT_UI_STATE_RETURNING2BASE] = "returning2base";
visualState[ROBOT_UI_STATE_RETURNING2START] = "returning2start";
