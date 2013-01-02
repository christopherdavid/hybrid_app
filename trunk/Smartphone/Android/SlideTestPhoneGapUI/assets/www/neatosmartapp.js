

/*
 * Initialization of the plugins  
 */

var neatoSmartApp = (function() {
	
	var WELCOME_PAGE = 101;
	var USER_LOGIN_PAGE = 102;
	var REGISTER_USER_PAGE = 103;
	var USER_HOME_PAGE = 104;
	var ROBOT_ASSOCIATION_PAGE = 105;
	var ROBOT_SCHEDULE_PAGE = 106;
	var ROBOT_MAP_PAGE = 107;
	var CURRENT_PAGE = USER_HOME_PAGE;
	return {

		setResponseText: function(result) {
			if (result == null) {
				document.querySelector('#responseText').innerHTML = '';
				return;
			}
			document.querySelector('#responseText').innerHTML ="Response: "+JSON.stringify(result, null, 4);
		},
		// Click listeners with success and error callbacks.
		loginWithDemo: function() {
			var email = "demo1@demo.com";
			var password = "demo123";
			neatoSmartApp.showProgressBar();

			UserPluginManager.login(email, password, neatoSmartApp.successLoginDemo, neatoSmartApp.errorLoginDemo);

		},

		successLoginDemo: function(result) {	
			neatoSmartApp.hideProgressBar();
			neatoSmartApp.setResponseText(result);
			localStorage.setItem('email', result.email);
			localStorage.setItem('loggedIn', 1);
			neatoSmartApp.hideWelcomeShowHomePage();
		},

		errorLoginDemo: function(error) {
			neatoSmartApp.hideProgressBar();
			neatoSmartApp.setResponseText(error);
		},

		login: function() {

			var email = document.querySelector('#mailid').value;
			var password = document.querySelector('#passkey').value;
			localStorage.setItem('email', email);
			UserPluginManager.login(email, password, neatoSmartApp.successLogin, neatoSmartApp.errorLogin);
			neatoSmartApp.showProgressBar();
		},

		successLogin: function(result) {	 
			neatoSmartApp.setResponseText(result);
			localStorage.setItem('loggedIn', 1);
			neatoSmartApp.hideProgressBar();
			neatoSmartApp.hideLoginShowHomePage();
		},

		errorLogin: function(error) {
			neatoSmartApp.hideProgressBar();
			neatoSmartApp.setResponseText(error);
		},

		register: function() {
			var email = document.querySelector('#regmailid').value;
			var password = document.querySelector('#regpasskey').value;
			var name = document.querySelector('#regname').value;
			neatoSmartApp.showProgressBar();
			//TODO: Put validation of 2 passwords and email id
			var passwordconfirm = document.querySelector('#regpasskeyconfirm').value;
			UserPluginManager.createUser(email, password, name, neatoSmartApp.successRegister, neatoSmartApp.errorRegister);
		},

		successRegister: function(result) {
			neatoSmartApp.setResponseText(result);
			neatoSmartApp.hideProgressBar();
			neatoSmartApp.hideRegisterShowHomePage();
		},


		errorRegister: function(error) {  
			neatoSmartApp.hideProgressBar();
			neatoSmartApp.setResponseText(error);
		},

		discoverRobots: function() {
			RobotPluginManager.discoverNearbyRobots(neatoSmartApp.discoverRobotSuccess, neatoSmartApp.discoverRobotError);
			neatoSmartApp.showProgressBar();
		},

		discoverRobotSuccess: function(result) {
			neatoSmartApp.setResponseText(result);
			neatoSmartApp.hideProgressBar();
			var showList = false;
			for (var i in result) {
				showList = true;
				var robot = result[i];
				neatoSmartApp.addRobotToList(robot.robot_name, robot.robotId);
			}

			if(showList) {
				neatoSmartApp.displayRobotList();
			} else {
				alert("No Neato Robots found! Try again");
			}
		}, 

		addRobotToList: function(name, robotId) {
			 $('#robotsFound').append('<input type="button" id= "'+robotId +'" value="'+ name+'" class="robotItemButton"></input>');
		//	 document.querySelector('#'+robotId).addEventListener('click', neatoSmartApp.clickRobot, true);
		},
		
		clickRobot: function(robotId) {
			
			RobotPluginManager.tryDirectConnection(robotId, neatoSmartApp.successTest, neatoSmartApp.errorTest); 
			 var myNode = document.getElementById("robotsFound");
			 while (myNode.firstChild) {
			     myNode.removeChild(myNode.firstChild);
			 }
			neatoSmartApp.hideDisplayList();
		},
		successPeerConnect: function(result) {
			neatoSmartApp.hideProgressBar();
		},
		
		errorPeerConnect: function(error) {
			neatoSmartApp.hideProgressBar();
			neatoSmartApp.setResponseText(error);
		},
		displayRobotList: function() {
			document.querySelector('#robotsFound').setAttribute('aria-hidden', 'false');
			document.querySelector('#robotoverlay').setAttribute('aria-hidden', 'false');
			document.querySelector('#robotoverlay').addEventListener('click', neatoSmartApp.hideDisplayList, false);
			$('.robotItemButton').click(function() {
				
				neatoSmartApp.clickRobot(this.id); 
			});
		},
	
		hideDisplayList: function() {
			document.querySelector('#robotsFound').setAttribute('aria-hidden', 'true');
			document.querySelector('#robotoverlay').setAttribute('aria-hidden', 'true');
			document.querySelector('#robotoverlay').removeEventListener('click', neatoSmartApp.hideDisplayList, false);

		},
		discoverRobotError: function(error) {
			neatoSmartApp.setResponseText(error);
		},

		associateRobot: function() {
			var robotId = document.querySelector('#robotId').value;
			localStorage.setItem('robotId', robotId);
			UserPluginManager.associateRobot("", robotId, neatoSmartApp.associateRobotSuccess, neatoSmartApp.associateRobotError);
			neatoSmartApp.showProgressBar();
		},

		associateRobotSuccess: function(result) {
			neatoSmartApp.setResponseText(result);
			neatoSmartApp.hideProgressBar();
			neatoSmartApp.hideAssociateShowHome();
		},

		associateRobotError: function(error) {
			neatoSmartApp.hideProgressBar();
			neatoSmartApp.setResponseText(error);
		},
		
		disassociateRobot: function() {
			var robotId = document.querySelector('#robotId').value;
			var email = localStorage.getItem('email');
			neatoSmartApp.showProgressBar();
			UserPluginManager.disassociateRobot(email, robotId, neatoSmartApp.successTest, neatoSmartApp.errorTest);
		},
		
		disassociateAllRobots: function() {
			var email = localStorage.getItem('email');
			neatoSmartApp.showProgressBar();
			UserPluginManager.disassociateAllRobots(email, neatoSmartApp.successTest, neatoSmartApp.errorTest);
		},
		
		getAssociatedRobots: function() {
			neatoSmartApp.showProgressBar();
			var email = localStorage.getItem('email');
			UserPluginManager.getAssociatedRobots(email, neatoSmartApp.successTest, neatoSmartApp.errorTest);
		},
		
		
		startStopCleaningServer: function() {
			
			//TODO: Sending junk serial id for now. Later this serial id should be the serial id of the robot 
			neatoSmartApp.showProgressBar();
			var robotId = 0;
			// TODO: Temp variable. Later the robot status will be sent by the robot. Store in local storage as of now.
			var robotStarted = localStorage.getItem('isRobotStarted');
			
			if (robotStarted == null) {
				robotStarted = "false";
			}
			
			var isRobotStarted =  ((robotStarted == "true") ? true : false);
			
			if(isRobotStarted) {
				RobotPluginManager.sendCommandToRobot(robotId, COMMAND_ROBOT_STOP, [], neatoSmartApp.startStopRobotSuccess, neatoSmartApp.startStopRobotError);
				localStorage.setItem('isRobotStarted', "false");
				// set the text as Stop
				document.querySelector('#btnStartStopCleaningServer').value = "Start Cleaning";
				document.querySelector('#btnStartStopCleaningPeer').value = "Start Cleaning";

			} 
			else {
				RobotPluginManager.sendCommandToRobot(robotId, COMMAND_ROBOT_START, [], neatoSmartApp.startStopRobotSuccess, neatoSmartApp.startStopRobotError);
				localStorage.setItem('isRobotStarted', "true");
				// set the text as Start
				document.querySelector('#btnStartStopCleaningPeer').value = "Stop Cleaning";
				document.querySelector('#btnStartStopCleaningServer').value = "Stop Cleaning";

			}
		},
		
		startStopCleaningPeer: function() {
			//TODO: Sending junk serial id for now. Later this serial id should be the serial id of the robot 	
			neatoSmartApp.showProgressBar();
			var robotId = 0;
			// TODO: Temp variable. Later the robot status will be sent by the robot. Store in local storage as of now.
			var robotStarted = localStorage.getItem('isRobotStarted');
			
			if (robotStarted == null) {
				robotStarted = "false";
			}
			
			var isRobotStarted =  ((robotStarted == "true") ? true : false);
			
			if (isRobotStarted) {
				RobotPluginManager.sendCommandToRobot(robotId, COMMAND_ROBOT_STOP, [], neatoSmartApp.startStopRobotSuccess, neatoSmartApp.startStopRobotError);
				localStorage.setItem('isRobotStarted', "false");
				// set the text as Stop
				document.querySelector('#btnStartStopCleaningServer').value = "Start Cleaning";
				document.querySelector('#btnStartStopCleaningPeer').value = "Start Cleaning";

			} else {
				RobotPluginManager.sendCommandToRobot(robotId, COMMAND_ROBOT_START, [], neatoSmartApp.startStopRobotSuccess, neatoSmartApp.startStopRobotError);
				localStorage.setItem('isRobotStarted', "true");
				// set the text as Start
				document.querySelector('#btnStartStopCleaningServer').value = "Stop Cleaning";
				document.querySelector('#btnStartStopCleaningPeer').value = "Stop Cleaning";

			}
		},

		startStopRobotSuccess: function(result) {
			neatoSmartApp.hideProgressBar();
		},
		
		startStopRobotError: function(error) {
			neatoSmartApp.hideProgressBar();
		},
		
		logoutUser: function() {
			// 0 indicates logout.
			localStorage.setItem('loggedIn' , 0);
			UserPluginManager.logout(neatoSmartApp.successLogout, neatoSmartApp.errorLogout);
			
		},
		
		test: function() {
			neatoSmartApp.showProgressBar();
			RobotPluginManager.tryDirectConnection('Robot_1001', neatoSmartApp.successTest, neatoSmartApp.errorTest);
		},
		
		successTest: function(result) {
			neatoSmartApp.hideProgressBar();
			neatoSmartApp.setResponseText(result);
		},
		errorTest: function(error) {
			neatoSmartApp.hideProgressBar();
			neatoSmartApp.setResponseText(error);
		},
		successLogout: function(result) {
			neatoSmartApp.hideProgressBar();
			document.querySelector('#displayResult').innerHTML = result;
			neatoSmartApp.hideUserShowWelcome();
		},

		errorLogout: function(error) {
			neatoSmartApp.hideProgressBar();
			alert(JSON.stringify(error));
		},
				
		//Note: Here the value signifies the key corresponding to the variable.
		// The necessary keys are defined in neatosmartapphelper.js.
		//Example:var DAY_SUNDAY = 0;
		//		  var DAY_MONDAY = 1;
		// These values are used here to signify day. These shouldbe used while
		//passing on to the pluginmanager.
		// Mins attribute is also supported by the plugin. Here its sending default 0.

		//		addToAdvancedSchedule: function(scheduleJsonArray, day, startTimeHrs, startTimeMins, 
		//      endTimeHrs, endTimeMins, eventType, area) {

		scheduleRobotAlternateWeekdays: function() {
			var scheduleJsonArray = [];
			var days = [1,2,5];
			var event = 1;
			
			scheduleJsonArray = PluginManagerHelper.addToAdvancedSchedule(scheduleJsonArray, days, "10:30", "12:30", event, "Kitchen");
			neatoSmartApp.scheduleEvent(scheduleJsonArray);
		},
		
		scheduleRobotBabyNap: function() {
			var scheduleJsonArray = [];
			var days = [1,2,3];
			var event = 0;
			scheduleJsonArray = PluginManagerHelper.addToAdvancedSchedule(scheduleJsonArray, days, "13:00", "16:00" , event, "");
			
			var days2 = [4,5];
			var event2 = 0;
			scheduleJsonArray = PluginManagerHelper.addToAdvancedSchedule(scheduleJsonArray, days2, "14:00", "17:00", event2, "");
			
			var days3 = [1,2,3,4,5];
			var event3 = 1;
			scheduleJsonArray = PluginManagerHelper.addToAdvancedSchedule(scheduleJsonArray, days3, "9:00", "11:00", event3, "Bedroom");
			neatoSmartApp.scheduleEvent(scheduleJsonArray);
		},
		scheduleRobotOnlyWeekends: function() {
			var scheduleJsonArray = [];
			var days = [6,0];
			var event = 1;
			scheduleJsonArray = PluginManagerHelper.addToAdvancedSchedule(scheduleJsonArray, days, "12:00", "14:00", event, "Kitchen");
			
			var days2 = [6,0];
			var event2 = 1;
			scheduleJsonArray = PluginManagerHelper.addToAdvancedSchedule(scheduleJsonArray, days2, "15:00", "18:00", event2, "Garage");
			neatoSmartApp.scheduleEvent(scheduleJsonArray);

		},
		scheduleRobotWorkingCouples: function() {
			var scheduleJsonArray = [];
			var days = [1,2,3,4];
			var event = 1;
			scheduleJsonArray = PluginManagerHelper.addToAdvancedSchedule(scheduleJsonArray, days, "10:30", "12:30", event, "Kitchen");
			
			var days2 = [1,2,3,4];
			var event2 = 1;
			scheduleJsonArray = PluginManagerHelper.addToAdvancedSchedule(scheduleJsonArray, days2, "18:30", "22:30", event2, "Garage");
			
			var days3 = [6,0];
			var event3 = 0;
			scheduleJsonArray = PluginManagerHelper.addToAdvancedSchedule(scheduleJsonArray, days3, "8:30", "12:30", event3, "");

			neatoSmartApp.scheduleEvent(scheduleJsonArray);
		},
		scheduleClear:function() {
			alert("To be implemented");
		},
		

		scheduleEvent: function(scheduleJsonArray) {			
			neatoSmartApp.showProgressBar();
			var robotId = localStorage.getItem('robotId');
			if (robotId == null) {
				robotId = "Robot_1001";
			}
			RobotPluginManager.setSchedule(robotId, scheduleJsonArray, neatoSmartApp.scheduleEventSuccess, neatoSmartApp.scheduleEventErr);
		},
		
		scheduleEventSuccess: function(result) {
			neatoSmartApp.hideProgressBar();
			alert("Schedule updated successfully");
			neatoSmartApp.setResponseText(result);
		}, 
		
		scheduleEventErr: function(error) {
			neatoSmartApp.hideProgressBar();
			neatoSmartApp.setResponseText(error);
			alert("Updating schedule failed");
		},
		
		getSchedule: function () {
			neatoSmartApp.showProgressBar();
			var robotId = localStorage.getItem('robotId');
			//TODO:Add advanced schedule type.
			if (robotId == null) {
				alert("Robot is not assocaited. Cannot retrieve schedule.");
				neatoSmartApp.hideProgressBar();
				return;
			}
			RobotPluginManager.getSchedule(robotId, "", neatoSmartApp.successTest, neatoSmartApp.errorTest);
		},
		
		getRobotMap: function() {
			neatoSmartApp.showProgressBar();
			var robotId = localStorage.getItem('robotId');
			if (robotId == null) {
				robotId = "Robot_1001";
			}
			RobotPluginManager.getMaps(robotId, neatoSmartApp.getRobotMapSuccess, neatoSmartApp.getRobotMapError);
		},
		
		
		//These constants are defined in jshelper.
		//TODO: have a struture like discovery. Is in place, yet to test.
		getRobotMapSuccess: function(result) {

			neatoSmartApp.hideProgressBar();
			// Map will be array of mapObjects. Right now we are assuming that its 1 element only.
			for (var i in result) {
				localStorage.setItem('mapId', result[i].mapId);
				localStorage.setItem('mapOverlayInfo', JSON.stringify(result[i].mapOverlayInfo));
				document.getElementById('robotMapImage').src=result[i].mapImage;
			}
		},
		
		
		getRobotMapError: function(error) {
			neatoSmartApp.hideProgressBar();
			neatoSmartApp.setResponseText(error);
		},

		setMapOverlayData: function() {
			neatoSmartApp.showProgressBar();
			var robotId = localStorage.getItem('robotId');
			if (robotId == null) {
				robotId = "Robot_1001";
			}
			
			var mapId = localStorage.getItem('mapId');
			
			if (mapId == null) {
				mapId = "12";
			}
			
			var mapOverlayInfoStr = localStorage.getItem('mapOverlayInfo');
			
			if (mapOverlayInfoStr == null) {
				mapOverlayInfoStr = "{\"geographies\":[{\"id\":\"floor1\",\"noGo\":[[120,30,150,45],[65,110,85,140]],\"base\":[[20,5,25,10]]}]}";
			}
			
			var mapOverlayInfo = JSON.parse(mapOverlayInfoStr);
			
			if (robotId == null) {
				alert("Robot is not assocaited. Cannot retrieve map.");
				return;
			}
			RobotPluginManager.setMapOverlayData(robotId, mapId, mapOverlayInfo, neatoSmartApp.setMapOverlayDataSuccess, neatoSmartApp.setMapOverlayDataError);
		},
		
		setMapOverlayDataSuccess: function(result) {
			neatoSmartApp.hideProgressBar();
		},
		
		
		setMapOverlayDataError: function(error) {
			neatoSmartApp.hideProgressBar();
			neatoSmartApp.setResponseText(error);
		},
		
		//##################FUNCTIONS RELATED TO HIDE-SHOW SECTIONS ON HTML#####################################
		
		showWelcomePage: function() {
			
			CURRENT_PAGE = WELCOME_PAGE;
			document.querySelector('#welcomeScreen').setAttribute('aria-hidden', 'false');
			document.querySelector('#btnGoToLogin').addEventListener('click', neatoSmartApp.hideWelcomeShowLogin , true);
			document.querySelector('#btnGoToRegister').addEventListener('click', neatoSmartApp.hideWelcomeShowRegister , true);
			document.querySelector('#btnDemoLogin').addEventListener('click', neatoSmartApp.loginWithDemo , true);
		},

		hideWelcomePage: function() {
			neatoSmartApp.setResponseText(null);
			document.querySelector('#welcomeScreen').setAttribute('aria-hidden', 'true');
			document.querySelector('#btnGoToLogin').removeEventListener('click', neatoSmartApp.hideWelcomeShowLogin , true);
			document.querySelector('#btnGoToRegister').removeEventListener('click', neatoSmartApp.hideWelcomeShowRegister , true);
			document.querySelector('#btnDemoLogin').removeEventListener('click', neatoSmartApp.loginWithDemo , true);
		},

		hideWelcomeShowLogin: function() {
			neatoSmartApp.hideWelcomePage();
			neatoSmartApp.showLoginPage();
		},
		
		

		hideWelcomeShowRegister: function() {
			neatoSmartApp.hideWelcomePage();
			neatoSmartApp.showRegisterPage();
		},


		hideWelcomeShowHomePage : function() {
			neatoSmartApp.hideWelcomePage();
			neatoSmartApp.showUserHomepage();

		},
		
		showLoginPage: function() {
			CURRENT_PAGE = USER_LOGIN_PAGE;
			document.querySelector('#loginPage').setAttribute('aria-hidden', 'false');
			document.querySelector('#loginButton').addEventListener('click', neatoSmartApp.login , true);
		},


		hideLoginShowWelcome: function() {
			neatoSmartApp.hideLoginPage();
			neatoSmartApp.showWelcomePage();
		},


		hideLoginPage: function() {
			neatoSmartApp.setResponseText(null);
			document.querySelector('#loginButton').removeEventListener('click', neatoSmartApp.login, true);
			document.querySelector('#loginPage').setAttribute('aria-hidden', 'true');
		},

		hideLoginShowHomePage: function() {
			neatoSmartApp.hideLoginPage();
			neatoSmartApp.showUserHomepage();
		},

	
		showRegisterPage: function() {
			CURRENT_PAGE = REGISTER_USER_PAGE;
			document.querySelector('#registerUserPage').setAttribute('aria-hidden', 'false');
			document.querySelector('#registerUserButton').addEventListener('click', neatoSmartApp.register , true);
		},

		hideRegisterPage: function() {
			neatoSmartApp.setResponseText(null);
			document.querySelector('#registerUserPage').setAttribute('aria-hidden', 'true');
			document.querySelector('#registerUserButton').removeEventListener('click', neatoSmartApp.register, true);
		},

		
		showUserHomepage: function() {
			CURRENT_PAGE = USER_HOME_PAGE;
			document.querySelector('#userHomePage').setAttribute('aria-hidden', 'false');
			document.querySelector('#btnDiscoverRobot').addEventListener('click', neatoSmartApp.discoverRobots , true);
			document.querySelector('#btnGoAssociatePage').addEventListener('click', neatoSmartApp.goAssociateRobotPage , true);
			document.querySelector('#btnStartStopCleaningServer').addEventListener('click', neatoSmartApp.startStopCleaningServer , true);
			document.querySelector('#btnStartStopCleaningPeer').addEventListener('click', neatoSmartApp.startStopCleaningPeer , true);
			document.querySelector('#btnGoToRobotMap').addEventListener('click', neatoSmartApp.hideHomeShowMap, true);
			document.querySelector('#btnSetMap').addEventListener('click', neatoSmartApp.setMapOverlayData, true);
			document.querySelector('#btnLogout').addEventListener('click', neatoSmartApp.logoutUser , true);
			document.querySelector('#btnTest').addEventListener('click', neatoSmartApp.test , true);
		},

		hideUserHomePage: function() {
			neatoSmartApp.setResponseText(null);
			document.querySelector('#userHomePage').setAttribute('aria-hidden', 'true');
			document.querySelector('#btnDiscoverRobot').removeEventListener('click', neatoSmartApp.discoverRobots, true);
			document.querySelector('#btnGoAssociatePage').removeEventListener('click', neatoSmartApp.goAssociateRobotPage , true);
			document.querySelector('#btnStartStopCleaningServer').removeEventListener('click', neatoSmartApp.startStopCleaningServer , true);
			document.querySelector('#btnStartStopCleaningPeer').removeEventListener('click', neatoSmartApp.startStopCleaningPeer , true);
			document.querySelector('#btnGoToRobotMap').removeEventListener('click', neatoSmartApp.hideHomeShowMap, true);
			document.querySelector('#btnSetMap').removeEventListener('click', neatoSmartApp.setMapOverlayData, true);
			document.querySelector('#btnLogout').removeEventListener('click', neatoSmartApp.logoutUser , true);
			document.querySelector('#btnTest').removeEventListener('click', neatoSmartApp.test , true);
		},
		
		hideUserShowWelcome: function() {
			neatoSmartApp.hideUserHomePage();
			neatoSmartApp.showWelcomePage();
		},
		
		goToSchedule: function() {
			neatoSmartApp.hideUserHomePage();
			neatoSmartApp.showSchedulePage();
		},


		
		showSchedulePage: function() {
			CURRENT_PAGE = ROBOT_SCHEDULE_PAGE;
			document.querySelector('#scheduleRobot').setAttribute('aria-hidden', 'false');
			document.querySelector('#btnScheduleAlternateWeekdays').addEventListener('click', neatoSmartApp.scheduleRobotAlternateWeekdays , true);
			document.querySelector('#btnScheduleBabyNap').addEventListener('click', neatoSmartApp.scheduleRobotBabyNap , true);
			document.querySelector('#btnScheduleOnlyWeekends').addEventListener('click', neatoSmartApp.scheduleRobotOnlyWeekends , true);
			document.querySelector('#btnScheduleWorkingCouple').addEventListener('click', neatoSmartApp.scheduleRobotWorkingCouples , true);
			document.querySelector('#btnScheduleClear').addEventListener('click', neatoSmartApp.scheduleClear , true);
			document.querySelector('#btnGetSchedule').addEventListener('click', neatoSmartApp.getSchedule , true);
		},
		
		
		hideScheduleShowHome: function() {
			neatoSmartApp.hideSchedulePage();
			neatoSmartApp.showUserHomepage();
		},
		
		hideSchedulePage: function() {
			neatoSmartApp.setResponseText(null);
			document.querySelector('#scheduleRobot').setAttribute('aria-hidden', 'true');
			document.querySelector('#btnScheduleAlternateWeekdays').removeEventListener('click', neatoSmartApp.scheduleRobotAlternateWeekdays , true);
			document.querySelector('#btnScheduleBabyNap').removeEventListener('click', neatoSmartApp.scheduleRobotBabyNap , true);
			document.querySelector('#btnScheduleOnlyWeekends').removeEventListener('click', neatoSmartApp.scheduleRobotOnlyWeekends , true);
			document.querySelector('#btnScheduleWorkingCouple').removeEventListener('click', neatoSmartApp.scheduleRobotWorkingCouples , true);
			document.querySelector('#btnScheduleClear').removeEventListener('click', neatoSmartApp.scheduleClear , true);
			document.querySelector('#btnGetSchedule').removeEventListener('click', neatoSmartApp.getSchedule , true);
		},
		
		goAssociateRobotPage: function() {
			neatoSmartApp.hideUserHomePage();
			neatoSmartApp.showAssociateRobotPage();
		},

		hideRegisterShowHomePage: function() {
			neatoSmartApp.hideRegisterPage();
			neatoSmartApp.showUserHomepage();

		}, 
		hideRegisterShowWelcome: function() {
			neatoSmartApp.hideRegisterPage();
			neatoSmartApp.showWelcomePage();
		},
		showAssociateRobotPage: function() {
			neatoSmartApp.setResponseText(null);
			CURRENT_PAGE = ROBOT_ASSOCIATION_PAGE;
			document.querySelector('#associateRobot').setAttribute('aria-hidden', 'false');
			document.querySelector('#btnAssociateRobot').addEventListener('click', neatoSmartApp.associateRobot , true);
			document.querySelector('#btnDisassociateRobot').addEventListener('click', neatoSmartApp.disassociateRobot , true);
			document.querySelector('#btnDisassociateAllRobot').addEventListener('click', neatoSmartApp.disassociateAllRobots , true);
			document.querySelector('#btnGetAssociatedRobots').addEventListener('click', neatoSmartApp.getAssociatedRobots , true);
		},

		hideAssociateRobotPage: function() {
			document.querySelector('#associateRobot').setAttribute('aria-hidden', 'true');
			document.querySelector('#btnAssociateRobot').removeEventListener('click', neatoSmartApp.associateRobot , true);
			document.querySelector('#btnDisassociateRobot').removeEventListener('click', neatoSmartApp.disassociateRobot , true);
			document.querySelector('#btnDisassociateAllRobot').removeEventListener('click', neatoSmartApp.disassociateAllRobots , true);
			document.querySelector('#btnGetAssociatedRobots').removeEventListener('click', neatoSmartApp.getAssociatedRobots , true);
		},

		hideAssociateShowHome: function() {
			neatoSmartApp.hideAssociateRobotPage();
			neatoSmartApp.showUserHomepage();
		},
		
		showRobotMapPage: function() {
			CURRENT_PAGE = ROBOT_MAP_PAGE;
			document.querySelector('#robotMapSection').setAttribute('aria-hidden', 'false');
			var robotId = localStorage.getItem('robotId');
			if (robotId == null) {
				robotId = "Robot_1001";
			}
			$('#robotSectionHeader').text("Map of "+robotId);
			document.querySelector('#robotSectionHeader')
			document.querySelector('#btnGetRobotMap').addEventListener('click', neatoSmartApp.getRobotMap , true);
		},
		
		hideRobotMapPage: function() {
			neatoSmartApp.setResponseText(null);
			document.querySelector('#robotMapSection').setAttribute('aria-hidden', 'true');
			document.querySelector('#btnGetRobotMap').removeEventListener('click', neatoSmartApp.getRobotMap , true);

		},

		hideHomeShowMap: function() {
			neatoSmartApp.hideUserHomePage();
			neatoSmartApp.showRobotMapPage();
		},
		hideMapShowHomePage: function() {
			neatoSmartApp.hideRobotMapPage();
			neatoSmartApp.showUserHomepage();
		},
		backButtonPressed: function() {  
			/*e.preventDefault();*/
		 	if (CURRENT_PAGE == ROBOT_ASSOCIATION_PAGE) {
		 		neatoSmartApp.hideAssociateShowHome();
		 	} else if (CURRENT_PAGE == USER_LOGIN_PAGE) {
		 		neatoSmartApp.hideLoginShowWelcome();
		 	} else if (CURRENT_PAGE == REGISTER_USER_PAGE) {
		 		neatoSmartApp.hideRegisterShowWelcome();
		 	} else if(CURRENT_PAGE == ROBOT_SCHEDULE_PAGE) {
		 		neatoSmartApp.hideScheduleShowHome();
		 	} else if(CURRENT_PAGE == ROBOT_MAP_PAGE) {
		 		neatoSmartApp.hideMapShowHomePage();
		 	} else {
		 		 neatoSmartApp.setResponseText(null);
		 		 navigator.app.exitApp();
		 	}
		 	
		}, 
		

		showProgressBar: function() {
			document.getElementById('spinnerImg').style.display = "block";
		},
		
		hideProgressBar: function() {
			document.getElementById('spinnerImg').style.display = "none";
		},
		

		menuButtonPressed: function() {

		},
		loaded: function() {	
			if (neatoSmartApp.isLoggedIn() == 1) {
				// TODO: right now checking in JS. Has to check within native.

				neatoSmartApp.showUserHomepage();
				//neatoSmartApp.showWelcomePage();
			}
			else {
				neatoSmartApp.showWelcomePage();
			}
			
			var robotStarted = localStorage.getItem('isRobotStarted');
			
			if (robotStarted == null) {
				robotStarted = "false";
			}
			
			var isRobotStarted =  ((robotStarted == "true") ? true : false);
			
			if(isRobotStarted) {
				// set the text as Stop
				document.querySelector('#btnStartStopCleaningServer').value = "Stop Cleaning";
				document.querySelector('#btnStartStopCleaningPeer').value = "Stop Cleaning";

			} else if(!isRobotStarted) {
				// set the text as Start
				document.querySelector('#btnStartStopCleaningServer').value = "Start Cleaning";
				document.querySelector('#btnStartStopCleaningPeer').value = "Start Cleaning";

			}
			document.addEventListener("backbutton", neatoSmartApp.backButtonPressed, false);
			document.addEventListener("menubutton", neatoSmartApp.menuButtonPressed, false);
		},

		isLoggedIn: function() {
			return localStorage.getItem('loggedIn');
		}

	}
}());

document.addEventListener("deviceready", neatoSmartApp.loaded, false);
