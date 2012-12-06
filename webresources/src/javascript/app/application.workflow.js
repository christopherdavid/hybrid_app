Application.prototype.loadWorkflow = function() {
    return {
    	"s0":{
            navrules : [{
                targetScreenId : "s1-1",
                conditions : [{
                    "key" : "logout",
                    "value" : true
                },]
            },
            {
                targetScreenId : "s3-2-1",
                conditions : [{
                    "key" : "changeRobot",
                    "value" : true
                },
                ]
            }]
    	},
        "s1-1" : {
            navrules : [{
                targetScreenId : "s1-2-1",
                conditions : [{
                    "key" : "register",
                    "value" : true
                },
                ]
            },
            {
                targetScreenId : "s1-3-1",
                conditions : [{
                    "key" : "login",
                    "value" : true
                },
                ]
            },
            {
                targetScreenId : "s3-2-1",
                conditions : [{
                    "key" : "map",
                    "value" : true
                },
                ]
            }]
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
        "s3-2-1" : {
            navrules : [{
                targetScreenId : "s1-1",
                conditions : [{
                    "key" : "back",
                    "value" : true
                }]
            }]
        }
    };
};
