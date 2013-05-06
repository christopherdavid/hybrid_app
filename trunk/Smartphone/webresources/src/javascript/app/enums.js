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

var ICON = ["cat-bath", "cat-baby", "cat-children","cat-cat", "cat-garage", "cat-dog"];
// icon position in image starting from top with 0
var ICONPOS = {"cat-bath":0,"cat-baby":1,"cat-children":2, "cat-cat":3, "cat-garage":4,"cat-dog":5};

var COLORTABLE = ["#000000", "#f1e60d", "#f39129", "#e72832", "#6ac6d9", "#65b32e", "#b3b4b3", "#63318a", "#0470b8"];

var STATE = {
    INACTIVE : "inactive",
    ACTIVE : "active",
    PAUSED : "paused",
    STOPPED : "stopped",
    BLOCKED : "blocked", // Robot is blocked, as the robot is used by another user
    UNAVAILABLE : "disabled" // Robot is out of reach, or couldn't be found (no network connection)
}

/**
 * Enumeration defining the caller of the robot selection screen 
 */
var robotScreenCaller = {
    REGISTER : "register",
    LOGIN : "login",
    CHANGE : "change",
    MANAGE : "manage"
}

/**
 * Enumeration defining the type of notifications. 
 */
var notificationType = {
    SPINNER : "0", // simple loading spinner in the center of the screen
    OPERATION : "1", // Notification area with text display and spinner, will be displayed until dismissed. 
    HINT: "2", // short displayed hint text (will be visible for ~ 2 sec. and disappear automatically)
    NONE:"3" // no notification will be displayed 
}
