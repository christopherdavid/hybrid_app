/**
 * development workflow
 * used to set easily a new style
 */
Application.prototype.loadWorkflow = function() {
    return {
        "start" : {
            navrules : [{
                targetScreenId : "header",
                conditions : [{
                    "key" : "header",
                    "value" : true
                }]
            },
            {
                targetScreenId : "button",
                conditions : [{
                    "key" : "button",
                    "value" : true
                }]
            },
            {
                targetScreenId : "daypicker",
                conditions : [{
                    "key" : "daypicker",
                    "value" : true
                }]
            },
            {
                targetScreenId : "listview",
                conditions : [{
                    "key" : "listview",
                    "value" : true
                }]
            },
            {
                targetScreenId : "dialog",
                conditions : [{
                    "key" : "dialogview",
                    "value" : true
                }]
            }
            ]
        },
        "header" : {
            navrules : []
        },
        "button" : {
            navrules : []
        },
        "daypicker" : {
            navrules : []
        },
        "listview" : {
            navrules : []
        },
        "dialog" : {
            navrules : []
        }
    };
};
