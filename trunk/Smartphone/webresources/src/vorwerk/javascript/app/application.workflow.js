Application.prototype.loadWorkflow = function() {
    return {
        "start" : {
            navrules : [{
                targetScreenId : "selectCountry",
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
        "selectCountry" : {
            navrules : [{
                targetScreenId : "legalInformation",
                conditions : [{
                    "key" : "valid",
                    "value" : true
                }]
            }]
        },
        "legalInformation" : {
            navrules : [{
                targetScreenId : "marketingInformation",
                conditions : [{
                    "key" : "valid",
                    "value" : true
                }]
            }, {
                targetScreenId : "privacy",
                conditions : [{
                    "key" : "privacy",
                    "value" : true
                }]
            }, {
                targetScreenId : "terms",
                conditions : [{
                    "key" : "terms",
                    "value" : true
                }]
            }]
        },
        "marketingInformation" : {
            navrules : [{
                targetScreenId : "createAccount",
                conditions : [{
                    "key" : "valid",
                    "value" : true
                }]
            }, 
            {
                targetScreenId : "userSettings",
                conditions : [{
                    "key" : "userSettings",
                    "value" : true
                }]
            }]
        },
        "createAccount" : {
            navrules : [{
                targetScreenId : "start",
                conditions : [{
                    "key" : "start",
                    "value" : true
                }]
            },
            {
                targetScreenId : "robotSelection",
                conditions : [{
                    "key" : "robotSelection",
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
            }, {
                targetScreenId : "selectCountry",
                conditions : [{
                    "key" : "selectCountry",
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
                targetScreenId : "cleaning",
                conditions : [{
                    "key" : "robotNameValid",
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
            }, {
                targetScreenId : "userSettings",
                conditions : [{
                    "key" : "userSettings",
                    "value" : true
                }]
            }, {
                targetScreenId : "generalSettings",
                conditions : [{
                    "key" : "generalSettings",
                    "value" : true
                }]
            }, {
                targetScreenId : "about",
                conditions : [{
                    "key" : "about",
                    "value" : true
                }]
            }, {
                targetScreenId : "basicScheduler",
                conditions : [{
                    "key" : "schedule",
                    "value" : true
                }]
            }, {
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
            }, {
                targetScreenId : "cleaning",
                backConditions : [{
                    "key" : "home",
                    "value" : true
                }]
            }]
        },
        "userSettings" : {
            navrules : [{
                targetScreenId : "selectCountry",
                conditions : [{
                    "key" : "changeCountry",
                    "value" : true
                }]
            }, {
                targetScreenId : "marketingInformation",
                conditions : [{
                    "key" : "changeSubscription",
                    "value" : true
                }]
            }, {
                targetScreenId : "settings",
                conditions : [{
                    "key" : "settings",
                    "value" : true
                }]
            }, {
                targetScreenId : "cleaning",
                backConditions : [{
                    "key" : "home",
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
        "generalSettings" : {
            navrules : [{
                targetScreenId : "robotSelection",
                conditions : [{
                    "key" : "changeRobot",
                    "value" : true
                }]
            }, {
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
            }, {
                targetScreenId : "settings",
                conditions : [{
                    "key" : "settings",
                    "value" : true
                }]
            }, {
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
            }, {
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
            }, {
                targetScreenId : "settings",
                conditions : [{
                    "key" : "back",
                    "value" : true
                }]
            }]
        },
        "privacy" : {
            navrules : []
        },
        "terms" : {
            navrules : []
        },
        "test" : {
            navrules : []
        }
    };
};
