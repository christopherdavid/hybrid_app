Application.prototype.loadWorkflow = function() {
    return {
        "home" : {
            navrules : [{
                targetScreenId : "start",
                conditions : [{
                    "key" : "logout",
                    "value" : true
                }]
            }, {
                targetScreenId : "robotSelection",
                conditions : [{
                    "key" : "changeRobot",
                    "value" : true
                }]
            }, {
                targetScreenId : "basicScheduler",
                conditions : [{
                    "key" : "schedule",
                    "value" : true
                }]
            }, {
                targetScreenId : "settings",
                conditions : [{
                    "key" : "settings",
                    "value" : true
                }]
            }, {
                targetScreenId : "remote",
                conditions : [{
                    "key" : "remote",
                    "value" : true
                }]
            }],
            clearHistory : true
        },
        "start" : {
            navrules : [{
                targetScreenId : "createAccount",
                conditions : [{
                    "key" : "register",
                    "value" : true
                }]
            }, {
                targetScreenId : "loginUser",
                conditions : [{
                    "key" : "login",
                    "value" : true
                }]
            }, {
                targetScreenId : "basicScheduler",
                conditions : [{
                    "key" : "schedule",
                    "value" : true
                }]
            }],
            clearHistory : true
        },
        "createAccount" : {
            navrules : [{
                targetScreenId : "robotSelection",
                conditions : [{
                    "key" : "valid",
                    "value" : true
                }]
            }]
        },
        "robotSelection" : {
            navrules : [{
                targetScreenId : "start",
                conditions : [{
                    "key" : "back",
                    "value" : true
                }]
            }, {
                targetScreenId : "robotActivationId",
                conditions : [{
                    "key" : "addRobot",
                    "value" : true
                }]
            }, {
                targetScreenId : "home",
                conditions : [{
                    "key" : "robotSelected",
                    "value" : true
                }]
            }, {
                targetScreenId : "start",
                backConditions : [{
                    "key" : "logout",
                    "value" : true
                }]
            }]
        },
        "robotActivationId" : {
            navrules : [{
                targetScreenId : "robotActivationName",
                conditions : [{
                    "key" : "robotIdValid",
                    "value" : true
                }]
            }]
        },
        "robotActivationName" : {
            navrules : [{
                targetScreenId : "robotActivationDone",
                conditions : [{
                    "key" : "robotNameValid",
                    "value" : true
                }]
            }]
        },
        "robotActivationDone" : {
            navrules : [{
                targetScreenId : "home",
                conditions : [{
                    "key" : "homeScreen",
                    "value" : true
                }]
            }]
        },
        "loginUser" : {
            navrules : [{
                targetScreenId : "robotSelection",
                conditions : [{
                    "key" : "valid",
                    "value" : true
                }]
            }]
        },
        "settings" : {
            navrules : [{
                targetScreenId : "robotSelection",
                conditions : [{
                    "key" : "robots",
                    "value" : true
                }]
            }, {
                targetScreenId : "test",
                conditions : [{
                    "key" : "test",
                    "value" : true
                }]
            }, {
                targetScreenId : "basicScheduler",
                conditions : [{
                    "key" : "basicSchedule",
                    "value" : true
                }]
            }]
        },
        "basicScheduler" : {
            navrules : [{
                targetScreenId : "home",
                backConditions : [{
                    "key" : "home",
                    "value" : true
                }]
            }, {
                targetScreenId : "basicSchedulerDate",
                conditions : [{
                    "key" : "addEvent",
                    "value" : true
                }]
            }, {
                targetScreenId : "basicSchedulerDate",
                conditions : [{
                    "key" : "editEvent",
                    "value" : true
                }]
            }],
            clearHistoryAfter : true
        },
        "basicSchedulerDate" : {
            navrules : [{
                targetScreenId : "basicScheduler",
                conditions : [{
                    "key" : "cancel",
                    "value" : true
                }]
            }, {
                targetScreenId : "basicScheduler",
                conditions : [{
                    "key" : "next",
                    "value" : true
                }]
            }]
        },
        "remote" : {
            navrules : [{
                targetScreenId : "home",
                backConditions : [{
                    "key" : "home",
                    "value" : true
                }]
            }]
        },

        "test" : {
            navrules : [{
                targetScreenId : "home",
                conditions : [{
                    "key" : "add",
                    "value" : true
                }]
            }]
        }

    };
};
