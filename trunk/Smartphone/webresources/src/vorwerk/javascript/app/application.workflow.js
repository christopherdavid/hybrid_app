Application.prototype.loadWorkflow = function() {
    return {
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
            }],
            clearHistory : true
        },
        "createAccount" : {
            navrules : [{
                targetScreenId : "loginUser",
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
                targetScreenId : "cleaning",
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
                targetScreenId : "cleaning",
                conditions : [{
                    "key" : "homeScreen",
                    "value" : true
                }]
            }],
            clearHistory : true
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
        "cleaning" : {
            navrules : [{
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
            }],
            clearHistory : true
        },
        "settings" : {
            navrules : [{
                targetScreenId : "robotSelection",
                conditions : [{
                    "key" : "changeRobot",
                    "value" : true
                }]
            }, {
                targetScreenId : "robotManagement",
                conditions : [{
                    "key" : "robotManagement",
                    "value" : true
                }]
            },{
                targetScreenId : "userSettings",
                conditions : [{
                    "key" : "userSettings",
                    "value" : true
                }]
            },{
                targetScreenId : "generalSettings",
                conditions : [{
                    "key" : "generalSettings",
                    "value" : true
                }]
            },{
                targetScreenId : "about",
                conditions : [{
                    "key" : "about",
                    "value" : true
                }]
            },{
                targetScreenId : "basicScheduler",
                conditions : [{
                    "key" : "schedule",
                    "value" : true
                }]
            },{
                targetScreenId : "cleaning",
                conditions : [{
                    "key" : "cleaning",
                    "value" : true
                }]
            }],
            clearHistory : true
        },
        "robotManagement" : {
            navrules : [{
                targetScreenId : "robotSelection",
                conditions : [{
                    "key" : "changeRobot",
                    "value" : true
                }]
            },{
                targetScreenId : "cleaning",
                backConditions : [{
                    "key" : "home",
                    "value" : true
                }]
            }]
        },
        "userSettings" : {
            navrules : [{
                targetScreenId : "robotSelection",
                conditions : [{
                    "key" : "changeRobot",
                    "value" : true
                }]
            },{
                targetScreenId : "cleaning",
                backConditions : [{
                    "key" : "home",
                    "value" : true
                }]
            },{
                targetScreenId : "start",
                backConditions : [{
                    "key" : "logout",
                    "value" : true
                }]
            }]
        },
        "generalSettings" : {
            navrules : [{
                targetScreenId : "robotSelection",
                conditions : [{
                    "key" : "changeRobot",
                    "value" : true
                }]
            },{
                targetScreenId : "cleaning",
                backConditions : [{
                    "key" : "home",
                    "value" : true
                }]
            }]
        },
        "basicScheduler" : {
            navrules : [{
                targetScreenId : "cleaning",
                conditions : [{
                    "key" : "cleaning",
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
            },{
                targetScreenId : "settings",
                conditions : [{
                    "key" : "settings",
                    "value" : true
                }]
            },{
                targetScreenId : "robotSelection",
                conditions : [{
                    "key" : "changeRobot",
                    "value" : true
                }]
            }],
            clearHistory : true
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
            },{
                targetScreenId : "robotSelection",
                conditions : [{
                    "key" : "changeRobot",
                    "value" : true
                }]
            }]
        },
        "about" : {
            navrules : [{
                targetScreenId : "robotSelection",
                conditions : [{
                    "key" : "changeRobot",
                    "value" : true
                }]
            },{
                targetScreenId : "settings",
                conditions : [{
                    "key" : "back",
                    "value" : true
                }]
            }]
        }

    };
};
