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
            }]
        },
        "header" : {
            navrules : [{
                targetScreenId : "header",
                conditions : [{
                    "key" : "header",
                    "value" : true
                }]
            }]
        }
    };
};
