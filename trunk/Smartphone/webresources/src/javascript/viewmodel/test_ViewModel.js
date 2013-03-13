resourceHandler.registerFunction('test_ViewModel.js', function(parent) {
    console.log('instance created for: test_ViewModel');
    var that = this;
    this.conditions = {};
    
    this.selectedTest =  ko.observable();
    this.testList = ko.observableArray(["core","user","robot", "map", "scheduler", "canvas"]);
    
    var geoData = {"geographies" : [{
                    "id" : "0",
                    "base" : [[158, 233, 192, 237], [503, 243, 537, 247]],
                    "nogo" : [[120, 30, 150, 45], [65, 110, 85, 140]],
                    "boundingBox" : [0, 0, 525, 360],
                    "visibleMap" : {
                        "img" : ""
                    },
                    "name" : "TwoSimpleRooms",
                    "rooms" : [{
                        "id" : "0",
                        "coord" : [{
                            "y" : 0,
                            "x" : 0
                        }, {
                            "y" : 0,
                            "x" : 360
                        }, {
                            "y" : 240,
                            "x" : 360
                        }, {
                            "y" : 240,
                            "x" : 0
                        }],
                        "icon" : "4",
                        "boundingBox" : [0, 0, 360, 240],
                        "color" : "6",
                        "name" : "Room1"
                    }, {
                        "id" : "1",
                        "coord" : [{
                            "y" : 160,
                            "x" : 360
                        }, {
                            "y" : 160,
                            "x" : 525
                        }, {
                            "y" : 360,
                            "x" : 525
                        }, {
                            "y" : 360,
                            "x" : 375
                        }, {
                            "y" : 240,
                            "x" : 375
                        }, {
                            "y" : 240,
                            "x" : 360
                        }],
                        "icon" : "2",
                        "boundingBox" : [360, 160, 525, 360],
                        "color" : "4",
                        "name" : "Room2"
                    }]
                }]
            };
                            
    this.testScenario = {
        "core":[
            {label:"send 1",exe:function(){parent.communicationWrapper.exec(UserPluginManager.login, ['doh@nos.com', '1234567'], that.callbackSuccess, that.callbackError);}},
            {label:"send 2",exe:function(){parent.communicationWrapper.exec(UserPluginManager.login, ['mrX@ade.com', '954244'], that.callbackSuccess, that.callbackError);}},
            {label:"clear callbacks",exe:function(){ parent.communicationWrapper.callbacks = {};}}
        ],
        
        "user":[
            // login failed, unknown user
            {label:"login unknown user",exe:function(){UserPluginManager.login("doh@no.com","1234567", that.callbackSuccess, that.callbackError);}},
            {label:"isUserLoggedIn unknown user",exe:function(){UserPluginManager.isUserLoggedIn("doh@no.com", that.callbackSuccess, that.callbackError);}},
            {label:"getUserDetail unknown user",exe:function(){UserPluginManager.getUserDetail("doh@no.com");}},
            {label:"logout unknown user",exe:function(){UserPluginManager.logout(that.callbackSuccess, that.callbackError);}},
            //login demo user
            {label:"login demo user",exe:function(){UserPluginManager.login("demo1@demo.com","demo123", that.callbackSuccess, that.callbackError);}},
            {label:"isUserLoggedIn demo user",exe:function(){UserPluginManager.isUserLoggedIn("demo1@demo.com", that.callbackSuccess, that.callbackError);}},
            {label:"getUserDetail demo user",exe:function(){UserPluginManager.getUserDetail("demo1@demo.com", that.callbackSuccess, that.callbackError);}},
            {label:"getAssociatedRobots demo user",exe:function(){UserPluginManager.getAssociatedRobots("demo1@demo.com",that.callbackSuccess, that.callbackError);}},
            {label:"associateRobot rr1001", exe:function(){UserPluginManager.associateRobot("demo1@demo.com","rr1001", that.callbackSuccess, that.callbackError);}},
            {label:"logout demo user",exe:function(){UserPluginManager.logout(that.callbackSuccess, that.callbackError);}},
                        
            // create a new user
            {label:"create new user",exe:function(){UserPluginManager.createUser("homer1@uid.com", "pwd123", "Homer", that.callbackSuccess, that.callbackError);}},
            {label:"isUserLoggedIn new user",exe:function(){UserPluginManager.isUserLoggedIn("homer@uid.com", that.callbackSuccess, that.callbackError);}},
            {label:"getUserDetail new user",exe:function(){UserPluginManager.getUserDetail("homer@uid.com", that.callbackSuccess, that.callbackError);}},
            {label:"logout new user",exe:function(){UserPluginManager.logout(that.callbackSuccess, that.callbackError);}},
            // login with new user and associate robots
            {label:"login new user",exe:function(){UserPluginManager.login("homer@uid.com", "pwd123", that.callbackSuccess, that.callbackError);}},
            {label:"associateRobot", exe:function(){UserPluginManager.associateRobot("homer@uid.com", "arr", that.callbackSuccess, that.callbackError);}},
            {label:"disassociateRobot", exe:function(){UserPluginManager.disassociateRobot("homer@uid.com", "arr", that.callbackSuccess, that.callbackError);}},
            {label:"disassociateAllRobots", exe:function(){UserPluginManager.disassociateAllRobots("homer@uid.com", that.callbackSuccess, that.callbackError);}}
                        
        ],
        
        "robot":[
            //login demo user
            {label:"login demo user",exe:function(){UserPluginManager.login("demo1@demo.com","demo123", that.callbackSuccess, that.callbackError);}},
            {label:"discover Nearby Robots",exe:function(){RobotPluginManager.discoverNearbyRobots(that.callbackSuccess, that.callbackError);}},
            {label:"try direct connect to rr1234",exe:function(){RobotPluginManager.tryDirectConnection("arr", that.callbackSuccess, that.callbackError);}},
            {label:"disconnect from rr1234",exe:function(){RobotPluginManager.disconnectDirectConnection("arr", that.callbackSuccess, that.callbackError);}},
            {label:"rename Robot wall-e to Eve", exe:function(){RobotPluginManager.setRobotName("wall-e", "Eve", that.callbackSuccess, that.callbackError);}},
            
            // send robot commands
            {label:"send start to robot",exe:function(){RobotPluginManager.sendCommandToRobot("arr", COMMAND_ROBOT_START, "", that.callbackSuccess, that.callbackError);}},
            {label:"send stop to robot",exe:function(){RobotPluginManager.sendCommandToRobot("arr", COMMAND_ROBOT_STOP, "", that.callbackSuccess, that.callbackError);}},
            {label:"send robot to base",exe:function(){RobotPluginManager.sendCommandToRobot("arr", COMMAND_SEND_BASE, "", that.callbackSuccess, that.callbackError);}}
        ],
        
        "map": [
            {label:"getMaps arr",exe:function(){RobotPluginManager.getMaps("arr", that.callbackSuccess, that.callbackError);}},
            {label:"getAtlasMetaData arr",exe:function(){RobotPluginManager.getRobotAtlasMetadata("arr", that.callbackSuccess, that.callbackError);}},
            {label:"getMaps wall-e",exe:function(){RobotPluginManager.getMaps("wall-e", that.callbackSuccess, that.callbackError);}},
            {label:"getAtlasMetaData wall-e",exe:function(){RobotPluginManager.getRobotAtlasMetadata("wall-e", that.callbackSuccess, that.callbackError);}},
            {label:"getAtlasGridData wall-e",exe:function(){RobotPluginManager.getAtlasGridData("wall-e", "", that.callbackSuccess, that.callbackError);}},
            {label:"updateAtlasMetaData wall-e",exe:function(){RobotPluginManager.updateAtlasMetaData("wall-e", geoData, that.callbackSuccess, that.callbackError);}}
        ],
        
        "scheduler":[
            {label:"getSchedule BASIC arr",exe:function(){RobotPluginManager.getSchedule("wall-e", SCHEDULE_TYPE_BASIC, that.callbackSuccess, that.callbackError);}},
            {label:"getSchedule ADVANCED arr",exe:function(){RobotPluginManager.getSchedule("wall-e", SCHEDULE_TYPE_ADVANCED, that.callbackSuccess, that.callbackError);}}
        ],
        
        "canvas":[
            {label:"clear",exe:function(){
                var objCanvas = document.getElementById("testCcanvas");
                var objContext = objCanvas.getContext('2d');
                objContext.clearRect(0,0,450,250);
                }},
            {label:"draw rectangle",exe:function(){
                var objCanvas = document.getElementById("testCcanvas");
                var objContext = objCanvas.getContext('2d');
                objContext.globalAlpha = 1.0;
                objContext.beginPath();
                objContext.rect(10, 10, 50, 50);
                objContext.fillStyle = "#ff0";
                objContext.fill();
                }},
            {label:"draw transparent rectangle",exe:function(){
                var objCanvas = document.getElementById("testCcanvas");
                var objContext = objCanvas.getContext('2d');
                objContext.globalAlpha = 0.4;
                objContext.beginPath();
                objContext.rect(10, 10, 150, 150);
                objContext.fillStyle = "#ff0";
                objContext.fill();
            }},
            {label:"strokeRect",exe:function(){
                var objCanvas = document.getElementById("testCcanvas");
                var objContext = objCanvas.getContext('2d');
                objContext.globalAlpha = 1.0;
                objContext.lineWidth = 1.0;
                objContext.strokeStyle = "#000000";
                objContext.strokeRect(10, 10, 50, 50);
                }},
            {label:"draw nogo",exe:function(){
                var objCanvas = document.getElementById("testCcanvas");
                var objContext = objCanvas.getContext('2d');
                var nogo = [[120,30,150,45],[65,110,85,140]];
                var nogoImg = new Image(); 
                nogoImg.src = "img/nogo.png";
                var objPattern = objContext.createPattern(nogoImg, "repeat");
                for (var i = 0, maxN = nogo.length; i < maxN; i++) {
                
                    objContext.globalAlpha = 1.0;
                    objContext.fillStyle = objPattern;
                    objContext.strokeStyle = "#000000";
                    objContext.lineJoin = "round";
                    objContext.lineWidth = 1.0;
                    // create polygon
                    objContext.beginPath();
                    drawRect(nogo[i], objContext);
                    objContext.closePath();
                     // fill room with pattern
                    objContext.fill();
                    // draw room border
                    objContext.stroke();
                }
                
            }},
            {label:"draw line",exe:function(){
                var objCanvas = document.getElementById("testCcanvas");
                var objContext = objCanvas.getContext('2d');
                objContext.save()
                objContext.lineWidth = 1.0;
                drawLine(objContext, 20, 100, 70, 100, "#ff0");
                drawLine(objContext, 70, 100, 20, 50, "#FF9F00");
                drawLine(objContext, 20, 50, 70, 50, "#f00");
                drawLine(objContext, 70, 50, 45, 25, "#f0f");
                drawLine(objContext, 45, 25, 20, 50, "#00f");
                drawLine(objContext, 20, 50, 20, 100, "#0f0");
                drawLine(objContext, 20, 100, 70, 50, "#009F00");
                drawLine(objContext, 70, 50, 70, 100, "#7F7F7F");
                objContext.restore();
            }},
            {label:"line width",exe:function(){
                var objCanvas = document.getElementById("testCcanvas");
                var objContext = objCanvas.getContext('2d');
                var dblStart = 1.0;
                objContext.globalAlpha = 1.0;
                objContext.save()
                for(var i=0; i<20; i++){
                  var dblKorr = (i%2 == 0)? 0.5 : 0;
                  objContext.translate(15+dblKorr, 0);
                  objContext.beginPath();
                  objContext.moveTo(0, 0);
                  objContext.lineTo(0, 100);
                  objContext.lineWidth = dblStart + i*0.5;
                  objContext.stroke();
                }
                objContext.restore();
            }},
        ]
       
    }
    this.tests = ko.computed(function() {
        return that.testScenario[that.selectedTest()];
    }, this);
    
    
    this.callbackSuccess = function(result) {
        console.log("callbackSuccess\n\t " + JSON.stringify(result))
    }
    
    this.callbackError = function(result) {
        console.log("callbackError\n\t " + JSON.stringify(result))
    }
    
    this.back = function() {
        that.conditions['back'] = true;
        parent.flowNavigator.previous();
    };

    this.init = function() {
        
    }

    this.reload = function() {
        // remove conditions
        that.conditions = {};
    }

    this.deinit = function() {

    }

    this.add = function() {

    }
    
    // helper functions
    function drawRect(arrBox, contextView) {
        // first entry is always the new start position
        contextView.moveTo(arrBox[0], arrBox[1]);
        contextView.lineTo(arrBox[2], arrBox[1]);
        contextView.lineTo(arrBox[2], arrBox[3]);
        contextView.lineTo(arrBox[0], arrBox[3]);
    }

    function drawPolygon(arrCoord, contextView) {
        if (arrCoord.length > 0) {
            // first entry is always the new start position
            contextView.moveTo(arrCoord[0].x, arrCoord[0].y);
            for (var i = 1, maxC = arrCoord.length; i < maxC; i++) {
                contextView.lineTo(arrCoord[i].x, arrCoord[i].y);
            }
        }
    }
    
    function drawLine(objContext, intMoveX, intMoveY, intDestX, intDestY, strColor){
      objContext.beginPath();
      objContext.moveTo(intMoveX, intMoveY);
      objContext.lineTo(intDestX, intDestY);
      objContext.strokeStyle = strColor;
      objContext.stroke();
    }
    
    
    
})
console.log('loaded file: test_ViewModel.js'); 