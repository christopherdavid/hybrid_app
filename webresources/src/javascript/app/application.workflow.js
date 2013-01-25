Application.prototype.loadWorkflow = function() {
    return {
    	"s0":{
            navrules : [{
                targetScreenId : "s1-1",
                conditions : [{
                    "key" : "logout",
                    "value" : true
                }]
            },
            {
                targetScreenId : "s1-2-2",
                conditions : [{
                    "key" : "changeRobot",
                    "value" : true
                }]
            },
            {
                targetScreenId : "s4-1-1",
                conditions : [{
                    "key" : "schedule",
                    "value" : true
                }]
            },
            {
                targetScreenId : "s3-1-1",
                conditions : [{
                    "key" : "settings",
                    "value" : true
                }]
            },
            {
                targetScreenId : "s5-1-1",
                conditions : [{
                    "key" : "cleaning",
                    "value" : true
                }]
            }],
            clearHistory:true
    	},
        "s1-1" : {
            navrules : [{
                targetScreenId : "s1-2-1",
                conditions : [{
                    "key" : "register",
                    "value" : true
                }]
            },
            {
                targetScreenId : "s1-3-1",
                conditions : [{
                    "key" : "login",
                    "value" : true
                }]
            },
            {
                targetScreenId : "s3-2-1",
                conditions : [{
                    "key" : "map",
                    "value" : true
                }]
            },
            {
                targetScreenId : "s4-1-1",
                conditions : [{
                    "key" : "schedule",
                    "value" : true
                }]
            }],
            clearHistory:true
        },
        "s1-2-1" : {
            navrules : [{
                targetScreenId : "s1-2-2",
                conditions : [{
                    "key" : "valid",
                    "value" : true
                }]
            }]
        },
        "s1-2-2" : {
            navrules : [{
                targetScreenId : "s1-1",
                conditions : [{
                    "key" : "back",
                    "value" : true
                }]
            }, {
                targetScreenId : "s1-2-3",
                conditions : [{
                    "key" : "addRobot",
                    "value" : true
                }]
            }, {
                targetScreenId : "s0",
                conditions : [{
                    "key" : "robotSelected",
                    "value" : true
                }]
        	}, {
                targetScreenId : "s1-1",
                backConditions : [{
                    "key" : "logout",
                    "value" : true
                }]
            }]
        },
        "s1-2-3" : {
            navrules : [{
                targetScreenId : "s1-2-4",
                conditions : [{
                    "key" : "robotIdValid",
                    "value" : true
                }]
			}]
        },
        "s1-2-4" : {
            navrules : [{
                targetScreenId : "s1-2-6",
                conditions : [{
                    "key" : "robotNameValid",
                    "value" : true
                }]
			}]
        },
        "s1-2-6" : {
            navrules : [{
                targetScreenId : "s0",
                conditions : [{
                    "key" : "homeScreen",
                    "value" : true
                }]
			}]
        },
         "s1-3-1" : {
            navrules : [{
                targetScreenId : "s1-2-2",
                conditions : [{
                    "key" : "valid",
                    "value" : true
                }]
            }]
        },
        "s3-1-1" : {
            navrules : [{
                targetScreenId : "s3-2-1",
                conditions : [{
                    "key" : "rooms",
                    "value" : true
                }]
            },
            {
                targetScreenId : "s1-2-2",
                conditions : [{
                    "key" : "robots",
                    "value" : true
                }]
            }
            
            ,{
                targetScreenId : "test",
                conditions : [{
                    "key" : "test",
                    "value" : true
                }]
            }
            ]
        },        
        "s3-2-1" : {
            navrules : [{
                targetScreenId : "s1-1",
                conditions : [{
                    "key" : "back",
                    "value" : true
                }]
            }]
        },
        "s4-1-1" : {
            navrules : [{
                targetScreenId : "s0",
                backConditions : [{
                    "key" : "home",
                    "value" : true
                }]
            },{
                targetScreenId : "s4-1-2",
                conditions : [{
                    "key" : "addEvent",
                    "value" : true
                }]
            }]
        },
        "s4-1-2" : {
            navrules : [{
                targetScreenId : "s4-1-1",
                conditions : [{
                    "key" : "cancel",
                    "value" : true
                }]
            },
            {
                targetScreenId : "s4-1-3",
                conditions : [{
                    "key" : "next",
                    "value" : true
                }]
            }]
        },
        "s4-1-3" : {
            navrules : [{
                targetScreenId : "s4-1-2",
                conditions : [{
                    "key" : "back",
                    "value" : true
                }]
            },
            {
                targetScreenId : "s4-1-1",
                conditions : [{
                    "key" : "next",
                    "value" : true
                }]
            }]
        },
        "s5-1-1" : {
            navrules : [{
                targetScreenId : "s1-1",
                conditions : [{
                    "key" : "back",
                    "value" : true
                }]
            }]
        },
        
        "test" : {
            navrules : [{
                targetScreenId : "s1-1",
                conditions : [{
                    "key" : "add",
                    "value" : true
                }]
            }]
        }
        
    };
};
