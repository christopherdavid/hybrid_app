var conditonType = {
    FUNCTION : "0",
};

var textTarget = {
    INNERHTML : "0",
    FIRSTCHILD : "1",
    VALUE : "2",
    PLACEHOLDER : "3",
    jqLinkButton : "4"
};

var ICON = ["cat-bath", "cat-baby", "cat-children","cat-cat", "cat-garage", "cat-dog"];

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
