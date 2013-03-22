

/*
 * Initialization of the plugins  
 */

var neatoSmartApp = (function() {
	
	// Robot notification events constants
	var EVENT_ID_BASE 	 		= 20000; 
	var EVENT_ID_REGISTER 		= EVENT_ID_BASE;
	var EVENT_ID_UNREGISTER 	= EVENT_ID_BASE + 1;
	var EVENT_ID_STATUS		 	= EVENT_ID_BASE + 2;
	
	// Robot state codes
	var ROBOT_STATE_UNKNOWN 	= 10001;
	var ROBOT_STATE_CLEANING 	= 10002;
	var ROBOT_STATE_IDLE 		= 10003;
	var ROBOT_STATE_CHARGING 	= 10004;
	var ROBOT_STATE_STOPPED 	= 10005;
	var ROBOT_STATE_STUCK 		= 10006;
	var ROBOT_STATE_PAUSED 		= 10007;
	
	var WELCOME_PAGE = 101;
	var USER_LOGIN_PAGE = 102;
	var REGISTER_USER_PAGE = 103;
	var USER_HOME_PAGE = 104;
	var ROBOT_ASSOCIATION_PAGE = 105;
	var ROBOT_SCHEDULE_PAGE = 106;
	var ROBOT_MAP_PAGE = 107;
	var ROBOT_ATLAS_PAGE = 108;
	var ROBOT_COMMAND_PAGE = 109;
	var DEBUG_OPTIONS_PAGE = 110;
	var ROBOT_MANAGE_PAGE = 111;
	var SETTINGS_PAGE = 112;
	var ROBOT_SETTINGS_PAGE = 113;
	var USER_SETTINGS_PAGE = 114;
	var ADD_ROBOT_PAGE = 115;
	var DIRECT_ASSOCIATE_ROBOT_PAGE = 116;
	var ROBOT_SET_NAME_PAGE = 117;
	var USER_SETTINGS_PAGE = 118;
	var CHANGE_PASSWORD_PAGE = 119;
	var FORGET_PASSWORD_PAGE = 120;
	var ROBOT_NEW_SCHEDULE_PAGE = 121;
	
	var CURRENT_PAGE = USER_HOME_PAGE;
	var eventIdList = [];
	
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


		forgetPassword: function() {
			neatoSmartApp.showProgressBar();
			var email = document.querySelector('#forgetPassMailId').value;
			UserPluginManager.forgetPassword(email, neatoSmartApp.forgetPassSuccess, neatoSmartApp.forgetPassErr);
		},
		
		forgetPassSuccess: function(result) {
			neatoSmartApp.hideProgressBar();
			neatoSmartApp.setResponseText("Check your email for recovery steps");
		//	neatoSmartApp.hideForgetPasswordShowWelcome();
		},
		
		forgetPassErr: function(error) {
			neatoSmartApp.hideProgressBar();
			neatoSmartApp.setResponseText(error);
		},
		
		changePassword: function() {
			neatoSmartApp.showProgressBar();
			var email = document.querySelector('#regmailid').value;
			var currentPassword = document.querySelector('#currentPassword').value;
			var newPassword = document.querySelector('#newPassword').value;
			UserPluginManager.changePassword(email, currentPassword, newPassword, neatoSmartApp.changePassSuccess, neatoSmartApp.changePassErr);
		},
		
		changePassSuccess: function(result) {
			neatoSmartApp.hideProgressBar();
			neatoSmartApp.setResponseText(result);
//			neatoSmartApp.hideChangePasswordShowUserSetting();
		},
		
		changePassErr: function() {
			neatoSmartApp.hideProgressBar();
			neatoSmartApp.setResponseText(error);
		},
		
		register: function() {
			var email = document.querySelector('#regmailid').value;
			var password = document.querySelector('#regpasskey').value;
			var name = document.querySelector('#regname').value;
			
			localStorage.setItem('email', email);
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
				neatoSmartApp.addRobotToList(robot.robotName, robot.robotId);
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
			localStorage.setItem('robotId', robotId);
			var emailId = localStorage.getItem('email');
			UserPluginManager.associateRobot(emailId, robotId, neatoSmartApp.associateFoundRobotSuccess, neatoSmartApp.associateFoundRobotError);
			 
			
			neatoSmartApp.hideDisplayList();
		},
		
		associateFoundRobotSuccess: function(result) {
			neatoSmartApp.setResponseText(result);
			neatoSmartApp.hideProgressBar();			
			neatoSmartApp.hideAddRobotShowCommandsPage();
			
			localStorage.setItem('isRobotStarted', "false");
			localStorage.setItem('isPeerConnection', "false");
			
			neatoSmartApp.refreshUI();
		},

		associateFoundRobotError: function(error) {
			neatoSmartApp.hideProgressBar();
			neatoSmartApp.setResponseText(error);
			
			localStorage.setItem('isRobotStarted', "false");
			localStorage.setItem('robotId', '');
			localStorage.setItem('isPeerConnection', "false");
		},
		
		openClosePeerConn: function() {
			var robotId = localStorage.getItem('robotId');
			
			if ((robotId == null) || (robotId.length == 0)) {
				alert("Please associate a Robot");
				return;
			}
			
			neatoSmartApp.showProgressBar();
			var directConn = localStorage.getItem('isPeerConnection');			
			if ((directConn == null) || (directConn == "false")) {
				localStorage.setItem('isPeerConnection', "false");
				RobotPluginManager.tryDirectConnection(robotId, neatoSmartApp.successPeerConnect, neatoSmartApp.errorPeerConnect);
			} 
			else {
				RobotPluginManager.disconnectDirectConnection(robotId, neatoSmartApp.successPeerConnect, neatoSmartApp.errorPeerConnect);				
			}			
		},
		
		successPeerConnect: function(result) {
			neatoSmartApp.hideProgressBar();
			neatoSmartApp.setResponseText(result);
			var directConn = localStorage.getItem('isPeerConnection');			
			if (directConn == "true") {			
				localStorage.setItem('isPeerConnection', "false");
				document.querySelector('#btnOpenClosePeerConn').value = "Open Connection";
			}
			else {				
				localStorage.setItem('isPeerConnection', "true");
				document.querySelector('#btnOpenClosePeerConn').value = "Close Connection";
			}
			
			directConn = localStorage.getItem('isPeerConnection');			
		},
		
		errorPeerConnect: function(error) {
			neatoSmartApp.hideProgressBar();
			neatoSmartApp.setResponseText(error);
		},
		
		
		openClosePeerConn2: function() {
			var robotId = localStorage.getItem('robotId');
			
			if ((robotId == null) || (robotId.length == 0)) {
				alert("Please associate a Robot");
				return;
			}
			
			neatoSmartApp.showProgressBar();
			var directConn = localStorage.getItem('isPeerConnection');			
			if ((directConn == null) || (directConn == "false")) {
				localStorage.setItem('isPeerConnection', "false");
				RobotPluginManager.tryDirectConnection2(robotId, neatoSmartApp.successPeerConnect2, neatoSmartApp.errorPeerConnect2);
			} 
			else {
				RobotPluginManager.disconnectDirectConnection(robotId, neatoSmartApp.successPeerConnect2, neatoSmartApp.errorPeerConnect2);				
			}			
		},
		
		successPeerConnect2: function(result) {
			neatoSmartApp.hideProgressBar();
			neatoSmartApp.setResponseText(result);
			var directConn = localStorage.getItem('isPeerConnection');			
			if (directConn == "true") {			
				localStorage.setItem('isPeerConnection', "false");
				document.querySelector('#btnOpenClosePeerConn2').value = "Open Connection (New)";
			}
			else {				
				localStorage.setItem('isPeerConnection', "true");
				document.querySelector('#btnOpenClosePeerConn2').value = "Close Connection (New)";
			}
			
			directConn = localStorage.getItem('isPeerConnection');			
		},
		
		errorPeerConnect2: function(error) {
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
			var myNode = document.getElementById("robotsFound");
			while (myNode.firstChild) {
			    myNode.removeChild(myNode.firstChild);
			}
			document.querySelector('#robotoverlay').setAttribute('aria-hidden', 'true');
			document.querySelector('#robotoverlay').removeEventListener('click', neatoSmartApp.hideDisplayList, false);

		},
		discoverRobotError: function(error) {
			neatoSmartApp.setResponseText(error);
		},

		associateRobot: function() {
			var robotId = document.querySelector('#robotId').value;			
			localStorage.setItem('robotId', robotId);
			var email = localStorage.getItem('email')
			UserPluginManager.associateRobot(email, robotId, neatoSmartApp.associateRobotSuccess, neatoSmartApp.associateRobotError);
			neatoSmartApp.showProgressBar();
		},

		associateRobotSuccess: function(result) {
			neatoSmartApp.setResponseText(result);
			neatoSmartApp.hideProgressBar();
			neatoSmartApp.hideDirectAssociateRobotShowRobotCommandPage();
			
			localStorage.setItem('isRobotStarted', "false");			
			localStorage.setItem('isPeerConnection', "false");
			
			neatoSmartApp.refreshUI();
		},

		associateRobotError: function(error) {
			neatoSmartApp.hideProgressBar();
			neatoSmartApp.setResponseText(error);
			
			localStorage.setItem('robotId', "");
			localStorage.setItem('isRobotStarted', "false");
			localStorage.setItem('isPeerConnection', "false");
		},
		
		disassociateRobot: function() {
			var robotId = localStorage.getItem('robotId');
			if ((robotId == null) || (robotId.length == 0)) {
				alert("Please associate a Robot");
				return;
			}
			
			var email = localStorage.getItem('email');
			neatoSmartApp.showProgressBar();
			UserPluginManager.disassociateRobot(email, robotId, neatoSmartApp.dissociateRobotSuccess, neatoSmartApp.dissociateRobotError);
		},
		
		dissociateRobotSuccess: function(result) {
			neatoSmartApp.hideProgressBar();
			localStorage.removeItem('robotId');
			neatoSmartApp.setResponseText(result);
		},
		
		dissociateRobotError: function(result) {
			neatoSmartApp.hideProgressBar();
			neatoSmartApp.setResponseText(result);
		},		
		
		disassociateAllRobots: function() {
			var email = localStorage.getItem('email');
			neatoSmartApp.showProgressBar();
			UserPluginManager.disassociateAllRobots(email, neatoSmartApp.dissociateAllRobotSuccess, neatoSmartApp.dissociateAllRobotError);
		},
		
		dissociateAllRobotSuccess: function(result) {
			neatoSmartApp.hideProgressBar();
			localStorage.removeItem('robotId');
			neatoSmartApp.setResponseText(result);
		},
		
		dissociateAllRobotError: function(result) {
			neatoSmartApp.hideProgressBar();
			neatoSmartApp.setResponseText(result);
		},
		
		getAssociatedRobots: function() {
			neatoSmartApp.showProgressBar();
			var email = localStorage.getItem('email');
			UserPluginManager.getAssociatedRobots(email, neatoSmartApp.successTest, neatoSmartApp.errorTest);
		},
		
		sendToBaseCommand: function() {
			var robotId = localStorage.getItem('robotId');
			if ((robotId == null) || (robotId.length == 0)) {
				alert("Please associate a Robot");
				return;
			}
			
			neatoSmartApp.showProgressBar();
			RobotPluginManager.sendCommandToRobot2(robotId, COMMAND_SEND_BASE, [], neatoSmartApp.sendToBaseSuccess, neatoSmartApp.sendToBaseError);			
		},
		
		sendToBaseSuccess: function(result) {
			neatoSmartApp.hideProgressBar();
			neatoSmartApp.setResponseText(result);
		},
		
		sendToBaseError: function(error) {
			neatoSmartApp.hideProgressBar();
			neatoSmartApp.setResponseText(error);
		},
		
		getRobotState: function() {
			var robotId = localStorage.getItem('robotId');
			if ((robotId == null) || (robotId.length == 0)) {
				alert("Please associate a Robot");
				return;
			}
			
			neatoSmartApp.showProgressBar();
			RobotPluginManager.sendCommandToRobot2(robotId, COMMAND_GET_ROBOT_STATE, [], neatoSmartApp.getRobotStateSuccess, neatoSmartApp.getRobotStateError);			
		},
		
		getRobotStateSuccess: function(result) {
			neatoSmartApp.hideProgressBar();
			neatoSmartApp.setResponseText(result);
		},
		
		getRobotStateError: function(error) {
			neatoSmartApp.hideProgressBar();
			neatoSmartApp.setResponseText(error);
		},
		
		startStopCleaningPeer: function() {
			
			var robotId = localStorage.getItem('robotId');
			if ((robotId == null) || (robotId.length == 0)) {
				alert("Please associate a Robot");
				return;
			}
			
			// TODO: Temp variable. Later the robot status will be sent by the robot. Store in local storage as of now.
			var robotStarted = localStorage.getItem('isRobotStarted');
			
			if (robotStarted == null) {
				robotStarted = "false";
				localStorage.setItem('isRobotStarted', "false");
			}
			
			var isRobotStarted =  ((robotStarted == "true") ? true : false);
			
			neatoSmartApp.showProgressBar();
			if (isRobotStarted) {
				RobotPluginManager.sendCommandToRobot(robotId, COMMAND_ROBOT_STOP, [], neatoSmartApp.startStopRobotSuccess, neatoSmartApp.startStopRobotError);
				// localStorage.setItem('isRobotStarted', "false");
				// set the text as Stop
				// document.querySelector('#btnStartStopCleaningServer').value = "Start Cleaning";
				// document.querySelector('#btnStartStopCleaningPeer').value = "Start Cleaning";
				// document.querySelector('#btnStartStopCleaning').value = "Start Cleaning";

			} else {
				RobotPluginManager.sendCommandToRobot(robotId, COMMAND_ROBOT_START, [], neatoSmartApp.startStopRobotSuccess, neatoSmartApp.startStopRobotError);
				// localStorage.setItem('isRobotStarted', "true");
				// set the text as Start
				// document.querySelector('#btnStartStopCleaningServer').value = "Stop Cleaning";
				// document.querySelector('#btnStartStopCleaningPeer').value = "Stop Cleaning";
				// document.querySelector('#btnStartStopCleaning').value = "Stop Cleaning";
			}
		},

		startStopRobotSuccess: function(result) {
			neatoSmartApp.hideProgressBar();
			
			var robotStarted = localStorage.getItem('isRobotStarted');
			if (robotStarted == "true") {
				localStorage.setItem('isRobotStarted', "false");
				document.querySelector('#btnStartStopCleaning').value = "Start Cleaning";
			}
			else {
				localStorage.setItem('isRobotStarted', "true");
				document.querySelector('#btnStartStopCleaning').value = "Stop Cleaning";
			}
		},
		
		startStopRobotError: function(error) {
			neatoSmartApp.hideProgressBar();
		},
		
		
		startStopCleaning: function() {
			
			var robotId = localStorage.getItem('robotId');
			if ((robotId == null) || (robotId.length == 0)) {
				alert("Please associate a Robot");
				return;
			}
			
			// TODO: Temp variable. Later the robot status will be sent by the robot. Store in local storage as of now.
			var robotStarted = localStorage.getItem('isRobotStarted');
			
			if (robotStarted == null) {
				robotStarted = "false";
				localStorage.setItem('isRobotStarted', "false");
			}
			
			var isRobotStarted =  ((robotStarted == "true") ? true : false);
			
			neatoSmartApp.showProgressBar();
			if (isRobotStarted) {
				RobotPluginManager.sendCommandToRobot(robotId, COMMAND_ROBOT_STOP, [], neatoSmartApp.startStopCleaningSuccess, neatoSmartApp.startStopCleaningError);

			} else {
				RobotPluginManager.sendCommandToRobot(robotId, COMMAND_ROBOT_START, [], neatoSmartApp.startStopCleaningSuccess, neatoSmartApp.startStopCleaningError);
			}
		},

		startStopCleaningSuccess: function(result) {
			neatoSmartApp.hideProgressBar();
			var robotStarted = localStorage.getItem('isRobotStarted');
			if (robotStarted == "true") {
				localStorage.setItem('isRobotStarted', "false");
				document.querySelector('#btnSendStartStopCleanCommand').value = "Start Cleaning";
			}
			else {
				localStorage.setItem('isRobotStarted', "true");
				document.querySelector('#btnSendStartStopCleanCommand').value = "Stop Cleaning";
			}
		},
		
		startStopCleaningError: function(error) {
			neatoSmartApp.hideProgressBar();
		},
		
		startStopCleaning2: function() {
			
			var robotId = localStorage.getItem('robotId');
			if ((robotId == null) || (robotId.length == 0)) {
				alert("Please associate a Robot");
				return;
			}
			
			// TODO: Temp variable. Later the robot status will be sent by the robot. Store in local storage as of now.
			var robotStarted = localStorage.getItem('isRobotStarted');
			
			if (robotStarted == null) {
				robotStarted = "false";
				localStorage.setItem('isRobotStarted', "false");
			}
			
			var isRobotStarted =  ((robotStarted == "true") ? true : false);
			
			neatoSmartApp.showProgressBar();
			if (isRobotStarted) {
				RobotPluginManager.sendCommandToRobot2(robotId, COMMAND_ROBOT_STOP, {}, neatoSmartApp.startStopCleaningSuccess2, neatoSmartApp.startStopCleaningError2);

			} else {
				RobotPluginManager.sendCommandToRobot2(robotId, COMMAND_ROBOT_START, {'cleanMode':START_CLEAN_TYPE_NORMAL}, neatoSmartApp.startStopCleaningSuccess2, neatoSmartApp.startStopCleaningError2);
			}
		},

		startStopCleaningSuccess2: function(result) {
			neatoSmartApp.hideProgressBar();
			
			var robotStarted = localStorage.getItem('isRobotStarted');

			if (robotStarted == "true") {
				localStorage.setItem('isRobotStarted', "false");
				document.querySelector('#btnSendStartStopCleanCommand2').value = "Start Cleaning (New)";
			}
			else {
				localStorage.setItem('isRobotStarted', "true");
				document.querySelector('#btnSendStartStopCleanCommand2').value = "Stop Cleaning (New)";
			}
		},
		
		startStopCleaningError2: function(error) {
			
			neatoSmartApp.hideProgressBar();
		},
		
		pauseCleaning: function() {
			var robotId = localStorage.getItem('robotId');
			if ((robotId == null) || (robotId.length == 0)) {
				alert("Please associate a Robot");
				return;
			}
			
			var robotStarted = localStorage.getItem('isRobotStarted');
			if (robotStarted == null) {
				robotStarted = "false";
				localStorage.setItem('isRobotStarted', "false");
			}
			var isRobotStarted =  ((robotStarted == "true") ? true : false);
			if (isRobotStarted) {
				neatoSmartApp.showProgressBar();
				RobotPluginManager.sendCommandToRobot2(robotId, COMMAND_PAUSE_CLEANING, {}, neatoSmartApp.pauseCleaningSuccess, neatoSmartApp.pauseCleaningError);
			} else {
				alert("Robot is already stopped.")
;			}
			
		},
		
		pauseCleaningSuccess: function(result) {
			neatoSmartApp.hideProgressBar();
			var robotStarted = localStorage.getItem('isRobotStarted');
			localStorage.setItem('isRobotStarted', "false");
			document.querySelector('#btnSendStartStopCleanCommand2').value = "Start Cleaning (New)";
		},
		
		pauseCleaningError: function(error) {
			neatoSmartApp.hideProgressBar();
		},
		
		enableSchedule: function() {
			var robotId = localStorage.getItem('robotId');
			if ((robotId == null) || (robotId.length == 0)) {
				alert("Please associate a Robot");
				return;
			}
			neatoSmartApp.showProgressBar();
			RobotPluginManager.sendCommandToRobot2(robotId, COMMAND_ENABLE_SCHEDULE, {'enableSchedule':true, 'scheduleType':1}, neatoSmartApp.enableDisableScheduleSuccess, neatoSmartApp.enableDisableScheduleErr);
		},
		
		disableSchedule: function() {
			var robotId = localStorage.getItem('robotId');
			if ((robotId == null) || (robotId.length == 0)) {
				alert("Please associate a Robot");
				return;
			}
			neatoSmartApp.showProgressBar();
			RobotPluginManager.sendCommandToRobot2(robotId, COMMAND_ENABLE_SCHEDULE, {'enableSchedule':false, 'scheduleType':1}, neatoSmartApp.enableDisableScheduleSuccess, neatoSmartApp.enableDisableScheduleErr);
		},
		
		enableDisableScheduleSuccess: function(result) {
			neatoSmartApp.hideProgressBar();
			neatoSmartApp.setResponseText(result);
		},
		
		enableDisableScheduleErr: function(error) {
			neatoSmartApp.hideProgressBar();
			neatoSmartApp.setResponseText(error);
		},
		
		setRobotClock: function() {
			var robotId = localStorage.getItem('robotId');
			if ((robotId == null) || (robotId.length == 0)) {
				alert("Please associate a Robot");
				return;
			}
			neatoSmartApp.showProgressBar();
			var time=Date.UTC(2012,03,20, 11, 30, 00);
			RobotPluginManager.sendCommandToRobot2(robotId, COMMAND_SET_ROBOT_TIME, {'time':time}, neatoSmartApp.setRobotClockSuccess, neatoSmartApp.setRobotClockErr);
		},
		
		setRobotClockSuccess: function(result) {
			neatoSmartApp.hideProgressBar();
			neatoSmartApp.setResponseText(result);
		},
		
		setRobotClockErr: function(error) {
			neatoSmartApp.hideProgressBar();
			neatoSmartApp.setResponseText(error);
		},
		
		logoutUser: function() {
			// 0 indicates logout.
			localStorage.setItem('loggedIn' , 0);
			localStorage.setItem('isNotificationsON', false);
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
		
		useBasicScheduleNew: function() {
			 var chkBox = document.getElementById('newUseBasicSchedule');
			 return chkBox.checked;
		},
		
		useBasicScheduleNewOnClick: function() {
			neatoSmartApp.getScheduleEvents();
		},
		
		successScheduleDelete: function(result) {
			neatoSmartApp.hideProgressBar();
			neatoSmartApp.setResponseText(result);
		},
		errorScheduleDelete: function(error) {
			neatoSmartApp.hideProgressBar();
			neatoSmartApp.setResponseText(error);
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
			neatoSmartApp.showProgressBar();
			var robotId = localStorage.getItem('robotId');			
			if (robotId == null) {
				alert("Robot is not assocaited. Cannot delete schedule.");
				neatoSmartApp.hideProgressBar();
				return;
			}
			RobotPluginManager.deleteSchedule(robotId, 1, neatoSmartApp.successScheduleDelete, neatoSmartApp.errorScheduleDelete);
		},
		

		scheduleEvent: function(scheduleJsonArray) {			
			neatoSmartApp.showProgressBar();
			var robotId = localStorage.getItem('robotId');
			if (robotId == null) {
				robotId = "Robot_1001";
			}
			RobotPluginManager.setSchedule(robotId, 1, scheduleJsonArray, neatoSmartApp.scheduleEventSuccess, neatoSmartApp.scheduleEventErr);
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
		
		createSchedule: function() {
			neatoSmartApp.showProgressBar();
			var robotId = localStorage.getItem('robotId');
			if (robotId == null) {
				alert("Robot is not assocaited. Cannot retrieve schedule.");
				neatoSmartApp.hideProgressBar();
				return;
			}
			if (neatoSmartApp.useBasicScheduleNew()) {
				RobotPluginManager.createSchedule(robotId, 0, neatoSmartApp.createScheduleSuccess, neatoSmartApp.createScheduleError);
			} else {
				RobotPluginManager.createSchedule(robotId, 1, neatoSmartApp.createScheduleSuccess, neatoSmartApp.createScheduleError);
			}
		},
		createScheduleSuccess: function(result) {
			neatoSmartApp.hideProgressBar();
			neatoSmartApp.setResponseText(result);
			var scheduleId = result.scheduleId;
			localStorage.setItem('scheduleId', scheduleId);
		},
		createScheduleError: function(error) {
			neatoSmartApp.hideProgressBar();
			neatoSmartApp.setResponseText(error);
		},
		
		getScheduleEvents: function() {
			neatoSmartApp.showProgressBar();
			var robotId = localStorage.getItem('robotId');
			if (robotId == null) {
				alert("Robot is not assocaited. Cannot retrieve schedule.");
				neatoSmartApp.hideProgressBar();
				return;
			}
			if (neatoSmartApp.useBasicScheduleNew()) {
				RobotPluginManager.getScheduleEvents(robotId, 0, neatoSmartApp.getScheduleEventsSuccess, neatoSmartApp.getScheduleEventsError);
			} else {
				RobotPluginManager.getScheduleEvents(robotId, 1, neatoSmartApp.getScheduleEventsSuccess, neatoSmartApp.getScheduleEventsError);
			}
		},
		getScheduleEventsSuccess: function(result) {
			neatoSmartApp.hideProgressBar();
			neatoSmartApp.setResponseText(result);
			var scheduleId = result.scheduleId;
			var eventIds = result.scheduleEventLists;
			localStorage.setItem('scheduleId', scheduleId);
			//localStorage.setItem('scheduleEventId', eventIds[0]);
			eventIdList = eventIds;
		},
		
		getScheduleEventsError: function(error) {
			neatoSmartApp.hideProgressBar();
			neatoSmartApp.setResponseText(error);
		},
		
		addScheduleEvent: function() {
			neatoSmartApp.showProgressBar();
			var robotId = localStorage.getItem('robotId');
			if (robotId == null) {
				alert("Robot is not assocaited. Cannot retrieve schedule.");
				neatoSmartApp.hideProgressBar();
				return;
			}
			var scheduleId = localStorage.getItem('scheduleId');
			if (scheduleId == null || (scheduleId.length == 0)) {
				alert("Get Schedule Details");
				neatoSmartApp.hideProgressBar();
				return;
			}
			if (neatoSmartApp.useBasicScheduleNew()) {
				var	scheduleJson = PluginManagerHelper.getBasicScheduleEvent(1, "10:30");
				RobotPluginManager.addScheduleEvent(scheduleId, scheduleJson, neatoSmartApp.addScheduleEventSuccess, neatoSmartApp.addScheduleEventError);
			} else {
				var	scheduleJson = PluginManagerHelper.getAdvancedScheduleEvent(2, "02:30", "4:30", 0, "Kitchen");
				RobotPluginManager.addScheduleEvent(scheduleId, scheduleJson, neatoSmartApp.addScheduleEventSuccess, neatoSmartApp.addScheduleEventError);
			}
		},
		
		addScheduleEventSuccess: function(result) {
			neatoSmartApp.hideProgressBar();
			eventIdList = scheduleEventHelper.addEventId(eventIdList, result.scheduleEventId);
			neatoSmartApp.setResponseText(result);
		},
		addScheduleEventError: function(error) {
			neatoSmartApp.hideProgressBar();
			neatoSmartApp.setResponseText(error);
		},
		
		updateScheduleEvent: function() {

			neatoSmartApp.showProgressBar();
			var robotId = localStorage.getItem('robotId');
			if (robotId == null) {
				alert("Robot is not assocaited. Cannot retrieve schedule.");
				neatoSmartApp.hideProgressBar();
				return;
			}
			var scheduleId = localStorage.getItem('scheduleId');
			if (scheduleId == null) {
				alert("Get Schedule Details");
				neatoSmartApp.hideProgressBar();
				return;
			}
			
			//var scheduleEventId = localStorage.getItem('scheduleEventId');
			var scheduleEventId = scheduleEventHelper.getEventId(eventIdList, 0);
			if (scheduleEventId == null || (scheduleEventId.length == 0) || (typeof scheduleEventId === 'undefined')) {
				alert("No schedule events");
				neatoSmartApp.hideProgressBar();
				return;
			}
			if (neatoSmartApp.useBasicScheduleNew()) {
				var	scheduleJson = PluginManagerHelper.getBasicScheduleEvent(2, "12:30");
				RobotPluginManager.updateScheduleEvent(scheduleId, scheduleEventId, scheduleJson, neatoSmartApp.updateScheduleEventSuccess, neatoSmartApp.updateScheduleEventError);
			} else {
				var	scheduleJson = PluginManagerHelper.getAdvancedScheduleEvent(2, "04:30", "6:30", 0, "Kitchen");
				RobotPluginManager.updateScheduleEvent(scheduleId, scheduleEventId, scheduleJson, neatoSmartApp.updateScheduleEventSuccess, neatoSmartApp.updateScheduleEventError);
			}
		},
		updateScheduleEventSuccess: function(result) {
			neatoSmartApp.hideProgressBar();
			//localStorage.setItem('scheduleEventId', result.scheduleEventId);
			neatoSmartApp.setResponseText(result);
		},
		updateScheduleEventError: function(error) {
			neatoSmartApp.hideProgressBar();
			neatoSmartApp.setResponseText(error);
		},
		
		getScheduleEventData: function() {

			neatoSmartApp.showProgressBar();
			var robotId = localStorage.getItem('robotId');
			if (robotId == null) {
				alert("Robot is not assocaited. Cannot retrieve schedule.");
				neatoSmartApp.hideProgressBar();
				return;
			}
			var scheduleId = localStorage.getItem('scheduleId');
			if (scheduleId == null) {
				alert("Get Schedule Details");
				neatoSmartApp.hideProgressBar();
				return;
			}
			
			//var scheduleEventId = localStorage.getItem('scheduleEventId');
			var scheduleEventId = scheduleEventHelper.getEventId(eventIdList, 0);
			if (scheduleEventId == null || (scheduleEventId.length == 0) || (typeof scheduleEventId === 'undefined')) {
				alert("No schedule events");
				neatoSmartApp.hideProgressBar();
				return;
			}
			if (neatoSmartApp.useBasicScheduleNew()) {
				RobotPluginManager.getScheduleEventData(scheduleId, scheduleEventId, neatoSmartApp.getScheduleEventDataSuccess, neatoSmartApp.getScheduleEventDataError);
			} else {
				RobotPluginManager.getScheduleEventData(scheduleId, scheduleEventId, neatoSmartApp.getScheduleEventDataSuccess, neatoSmartApp.getScheduleEventDataError);
			}
		},
		
		getScheduleEventDataSuccess: function(result) {
			neatoSmartApp.hideProgressBar();
			neatoSmartApp.setResponseText(result);
			//localStorage.setItem('scheduleEventId', result.scheduleEventId);
		},
		getScheduleEventDataError: function(error) {
			neatoSmartApp.hideProgressBar();
			neatoSmartApp.setResponseText(error);
		},
		
		deleteScheduleEvent: function() {
			neatoSmartApp.showProgressBar();
			var robotId = localStorage.getItem('robotId');
			if (robotId == null) {
				alert("Robot is not assocaited. Cannot retrieve schedule.");
				neatoSmartApp.hideProgressBar();
				return;
			}
			var scheduleId = localStorage.getItem('scheduleId');
			if (scheduleId == null || (typeof scheduleId === 'undefined')) {
				alert("Get Schedule Details");
				neatoSmartApp.hideProgressBar();
				return;
			}
			
			//var scheduleEventId = localStorage.getItem('scheduleEventId');
			var scheduleEventId = scheduleEventHelper.getEventId(eventIdList, 0);
			if (scheduleEventId == null || (typeof scheduleEventId === 'undefined')) {
				alert("No schedule events");
				neatoSmartApp.hideProgressBar();
				return;
			}
			if (neatoSmartApp.useBasicScheduleNew()) {
				RobotPluginManager.deleteScheduleEvent(scheduleId, scheduleEventId, neatoSmartApp.deleteScheduleEventSuccess, neatoSmartApp.deleteScheduleEventError);
			} else {
				RobotPluginManager.deleteScheduleEvent(scheduleId, scheduleEventId, neatoSmartApp.deleteScheduleEventSuccess, neatoSmartApp.deleteScheduleEventError);
			}
		},
		
		deleteScheduleEventSuccess: function(result) {
			neatoSmartApp.hideProgressBar();
			//localStorage.setItem('scheduleEventId', null);
			eventIdList = scheduleEventHelper.removeEventId(eventIdList, result.scheduleEventId);
			neatoSmartApp.setResponseText(result);
		},
		deleteScheduleEventError: function(error) {
			neatoSmartApp.hideProgressBar();
			neatoSmartApp.setResponseText(error);
		},
		
		updateSchedule: function() {
			neatoSmartApp.showProgressBar();
			var scheduleId = localStorage.getItem('scheduleId');
			if (scheduleId == null) {
				alert("Get Schedule Details");
				neatoSmartApp.hideProgressBar();
				return;
			}
			if (neatoSmartApp.useBasicScheduleNew()) {
				RobotPluginManager.updateSchedule(scheduleId, neatoSmartApp.updateScheduleSuccess, neatoSmartApp.updateScheduleError);
			} else {
				RobotPluginManager.updateSchedule(scheduleId, neatoSmartApp.updateScheduleSuccess, neatoSmartApp.updateScheduleError);
			}
		},
		updateScheduleSuccess: function(result) {
			neatoSmartApp.hideProgressBar();
			neatoSmartApp.setResponseText(result);
		},
		updateScheduleError: function(error) {
			neatoSmartApp.hideProgressBar();
			neatoSmartApp.setResponseText(error);
		},
		
		getScheduleData: function() {
			neatoSmartApp.showProgressBar();
			var scheduleId = localStorage.getItem('scheduleId');
			if (scheduleId == null) {
				alert("Get Schedule Details");
				neatoSmartApp.hideProgressBar();
				return;
			}
			if (neatoSmartApp.useBasicScheduleNew()) {
				RobotPluginManager.getScheduleData(scheduleId, neatoSmartApp.getScheduleDataSuccess, neatoSmartApp.getScheduleDataError);
			} else {
				RobotPluginManager.getScheduleData(scheduleId, neatoSmartApp.getScheduleDataSuccess, neatoSmartApp.getScheduleDataError);
			}
		},
		
		getScheduleDataSuccess: function(result) {
			neatoSmartApp.hideProgressBar();
			neatoSmartApp.setResponseText(result);
		},
		getScheduleDataError: function(error) {
			neatoSmartApp.hideProgressBar();
			neatoSmartApp.setResponseText(error);
		},
		
		// ****************** Map ****************
		getRobotMap: function() {			
			$('#robotMapImage').replaceWith('<img src="" id="robotMapImage"/>');
			var robotId = localStorage.getItem('robotId');
			if ((robotId == null) || (robotId.length == 0)) {
				alert("Robot is not associated. Can't fetch map.");				
				return;
			}
			
			neatoSmartApp.showProgressBar();
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
	
		//ATLAS test buttons
		
		getRobotAtlasMetadata: function() {			
			var robotId = localStorage.getItem('robotId');
			if ((robotId == null) || (robotId.length == 0)) {
				alert("Robot is not associated.");				
				return;
			}
			
			neatoSmartApp.showProgressBar();
			RobotPluginManager.getRobotAtlasMetadata(robotId, neatoSmartApp.atlasSuccess, neatoSmartApp.atlasErr);
		}, 
		
			
		updateRobotAtlasMetadata: function() {
			var robotId = localStorage.getItem('robotId');
			if ((robotId == null) || (robotId.length == 0)) {
				alert("Please associate a Robot.");				
				return;
			}
			
			neatoSmartApp.showProgressBar();
			atlasMetadataInfoStr = "{\"geographies\":[{\"id\":\"floor1\",\"noGo\":[[120,30,150,45],[65,110,85,140]],\"base\":[[20,5,25,10]]}]}";
			var atlasMetadataInfo = JSON.parse(atlasMetadataInfoStr);
			RobotPluginManager.updateAtlasMetaData(robotId, atlasMetadataInfo, neatoSmartApp.atlasUpdateSuccess, neatoSmartApp.atlasErr);
		},
		
		getAtlasGridData: function() {
			var robotId = localStorage.getItem('robotId');
			if ((robotId == null) || (robotId.length == 0)) {
				alert("Please associate a Robot.");				
				return;
			}
			neatoSmartApp.showProgressBar();			
			var gridId = "";
			RobotPluginManager.getAtlasGridData(robotId, gridId, neatoSmartApp.gridSuccess, neatoSmartApp.gridErr);
		},
		
		gridSuccess: function(result) {
			neatoSmartApp.hideProgressBar();
			document.getElementById('robotAtlasGridImage').src=result[0].gridData;
		},
		
		gridErr: function(error) {
			neatoSmartApp.hideProgressBar();
			neatoSmartApp.setResponseText(result);
			alert("Error in retrieving grid data");
		},
		
		atlasUpdateSuccess: function(result) {
			alert("Updated atlas metadata successfully");
			neatoSmartApp.hideProgressBar();
		},
		
		atlasSuccess: function(result) {
			alert("Retrieved atlas metadata successfully");
			neatoSmartApp.hideProgressBar();
		},
		
		atlasErr: function(error) {
			alert("Error in retrieving atlas metadata");
			neatoSmartApp.hideProgressBar();
			neatoSmartApp.setResponseText(error);
		},
		
		getUserDetails: function() {
			neatoSmartApp.showProgressBar();
			var email = "demo1@demo.com";
			UserPluginManager.getUserDetail(email, neatoSmartApp.getUserDetailsSuccess, neatoSmartApp.getUserDetailsErr);
		},
		
		getUserDetailsSuccess: function(result) {
			neatoSmartApp.setResponseText(result);
			neatoSmartApp.hideProgressBar();
		},
		getUserDetailsErr: function(error) {
			neatoSmartApp.hideProgressBar();
		},
		
		setRobotName: function() {
			neatoSmartApp.showProgressBar();
			var robotName = document.querySelector('#robotName').value;
			var robotId = localStorage.getItem('robotId');
			if (robotId == null) {
				robotId = "Robot_1001";
			}
			RobotPluginManager.setRobotName(robotId, robotName, neatoSmartApp.setRobotNameSuccess, neatoSmartApp.setRobotNameErr);
		},
		
		setRobotName2: function() {			
			var robotName = document.querySelector('#robotName').value;
			var robotId = localStorage.getItem('robotId');
			if ((robotId == null) || (robotId.length == 0)) {
				alert("Please associate a robot");
			}
			else if ((robotName == null) || (robotName.length == 0)) {
				alert("Please enter a valid robot name");
			}
			else {
				neatoSmartApp.showProgressBar();
				RobotPluginManager.setRobotName2(robotId, robotName, neatoSmartApp.setRobotNameSuccess, neatoSmartApp.setRobotNameErr);
			}
		},
		
		setRobotNameSuccess: function(result) {
			neatoSmartApp.setResponseText(result);
			neatoSmartApp.hideProgressBar();
		},
		
		setRobotNameErr: function(error) {
			neatoSmartApp.setResponseText(error);
			neatoSmartApp.hideProgressBar();
		},
		
		getAssociatedRobotDetail: function() {			
			var robotId = localStorage.getItem('robotId');
			if ((robotId == null) || (robotName.length == 0)) {
				alert("Please associate a robot");
			}			
			else {
				neatoSmartApp.showProgressBar();
				RobotPluginManager.getRobotDetail(robotId, neatoSmartApp.setRobotDetailSuccess, neatoSmartApp.setRobotDetailError);
			}
		},
		
		getAssociatedRobotOnlineStatus: function() {			
			var robotId = localStorage.getItem('robotId');
			if ((robotId == null) || (robotName.length == 0)) {
				alert("Please associate a robot");
			}			
			else {
				neatoSmartApp.showProgressBar();
				RobotPluginManager.getRobotOnlineStatus(robotId, neatoSmartApp.getRobotOnlineStatusSuccess, neatoSmartApp.getRobotOnlineStatusError);
			}
		},
		
		setNotifications: function() {			
			var robotId = localStorage.getItem('robotId');
			if ((robotId == null) || (robotName.length == 0)) {
				alert("Please associate a robot");
			}			
			else {
				neatoSmartApp.showProgressBar();				
				var isNotificationsON = localStorage.getItem('isNotificationsON');				
				if (isNotificationsON == "false") {					
					RobotPluginManager.registerNotifications(robotId, neatoSmartApp.notificationStatusSuccess, neatoSmartApp.notificationStatusError);					
				}
				else {					
					RobotPluginManager.unregisterNotifications(robotId, neatoSmartApp.notificationStatusSuccess, neatoSmartApp.notificationStatusError);					
				}
			}
		},
		
		notificationStatusSuccess: function(result) {
			var eventId = result['eventId'];
			if ((eventId == EVENT_ID_REGISTER) || (eventId == EVENT_ID_UNREGISTER)) {
				var isNotificationsON = localStorage.getItem('isNotificationsON');				
				if (isNotificationsON == "false") {
					localStorage.setItem('isNotificationsON', "true");
					document.querySelector('#btnEnableNotifications').value = "OFF Notifications";
				}
				else {
					document.querySelector('#btnEnableNotifications').value = "ON Notifications";
					localStorage.setItem('isNotificationsON', "false");
				}
			}
			
			neatoSmartApp.hideProgressBar();			
			neatoSmartApp.setResponseText(result);
		},
		
		notificationStatusError: function(error) {
			neatoSmartApp.hideProgressBar();			
			neatoSmartApp.setResponseText(error);
		},
		
		getRobotOnlineStatusSuccess: function(result) {
			neatoSmartApp.setResponseText(result);
			neatoSmartApp.hideProgressBar();
		},
		
		getRobotOnlineStatusError: function(error) {
			neatoSmartApp.setResponseText(error);
			neatoSmartApp.hideProgressBar();
		},
		
		setRobotDetailSuccess: function(result) {
			neatoSmartApp.setResponseText(result);
			neatoSmartApp.hideProgressBar();
		},
		
		setRobotDetailError: function(error) {
			neatoSmartApp.setResponseText(error);
			neatoSmartApp.hideProgressBar();
		},

		
		//##################FUNCTIONS RELATED TO HIDE-SHOW SECTIONS ON HTML#####################################
		
		showWelcomePage: function() {
			
			CURRENT_PAGE = WELCOME_PAGE;
			document.querySelector('#welcomeScreen').setAttribute('aria-hidden', 'false');
			document.querySelector('#btnGoToLogin').addEventListener('click', neatoSmartApp.hideWelcomeShowLogin , true);
			document.querySelector('#btnGoToRegister').addEventListener('click', neatoSmartApp.hideWelcomeShowRegister , true);
			document.querySelector('#btnForgetPassword').addEventListener('click', neatoSmartApp.hideWelcomeShowForgetPassword , true);
			document.querySelector('#btnDemoLogin').addEventListener('click', neatoSmartApp.loginWithDemo , true);			
		},

		hideWelcomePage: function() {
			neatoSmartApp.setResponseText(null);
			document.querySelector('#welcomeScreen').setAttribute('aria-hidden', 'true');
			document.querySelector('#btnGoToLogin').removeEventListener('click', neatoSmartApp.hideWelcomeShowLogin , true);
			document.querySelector('#btnGoToRegister').removeEventListener('click', neatoSmartApp.hideWelcomeShowRegister , true);
			document.querySelector('#btnForgetPassword').removeEventListener('click', neatoSmartApp.hideWelcomeShowForgetPassword , true);
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
		
		showChangePasswordPage: function() {
			CURRENT_PAGE = CHANGE_PASSWORD_PAGE;
			document.querySelector('#changePasswordPage').setAttribute('aria-hidden', 'false');
			document.querySelector('#changePasswordButton').addEventListener('click', neatoSmartApp.changePassword , true);
		},
		
		hideChangePasswordPage: function() {
			neatoSmartApp.setResponseText(null);
			document.querySelector('#changePasswordPage').setAttribute('aria-hidden', 'true');
			document.querySelector('#changePasswordButton').removeEventListener('click', neatoSmartApp.changePassword, true);
		},
		
		hideSettingShowChangePasswordPage: function() {
			neatoSmartApp.hideUserSettingsPage();
			neatoSmartApp.showChangePasswordPage();
		},
		
		hideChangePasswordShowUserSetting: function() {
			neatoSmartApp.hideChangePasswordPage();
			neatoSmartApp.showUserSettingsPage();
		},
		showForgetPasswordPage: function() {
			CURRENT_PAGE = FORGET_PASSWORD_PAGE;
			document.querySelector('#forgetPasswordPage').setAttribute('aria-hidden', 'false');
			document.querySelector('#forgetPasswordButton').addEventListener('click', neatoSmartApp.forgetPassword , true);
		},

		hideForgetPasswordPage: function() {
			neatoSmartApp.setResponseText(null);
			document.querySelector('#forgetPasswordPage').setAttribute('aria-hidden', 'true');
			document.querySelector('#forgetPasswordButton').removeEventListener('click', neatoSmartApp.forgetPassword, true);

		},
		
		hideForgetPasswordShowWelcome: function() {
			neatoSmartApp.hideForgetPasswordPage();
			neatoSmartApp.showWelcomePage();
		},
		
		hideWelcomeShowForgetPassword: function() {
			neatoSmartApp.hideWelcomePage();
			neatoSmartApp.showForgetPasswordPage();
		},
		
		hideAddRobotShowCommandsPage: function() {
			neatoSmartApp.hideAddRobotPage();
			neatoSmartApp.showRobotCommandPage();
		},
		
		showUserHomepage: function() {
			CURRENT_PAGE = USER_HOME_PAGE;
			document.querySelector('#userHomePage').setAttribute('aria-hidden', 'false');
			document.querySelector('#btnGoToAddRobot').addEventListener('click', neatoSmartApp.goToAddRobotPage , true);
			document.querySelector('#btnGoToCommandRobot').addEventListener('click', neatoSmartApp.goToRobotCommandsPage , true);
			document.querySelector('#btnGoToDebug').addEventListener('click', neatoSmartApp.goToDebugOptionsPage , true);
			document.querySelector('#btnGoToSchedule').addEventListener('click', neatoSmartApp.goToSchedule , true);
			document.querySelector('#btnGoToSettings').addEventListener('click', neatoSmartApp.goToSettingsPage , true);			
			document.querySelector('#btnLogout').addEventListener('click', neatoSmartApp.logoutUser , true);
			document.querySelector('#btnGoToNewSchedule').addEventListener('click', neatoSmartApp.goToNewSchedulePage , true);
		},

		hideUserHomePage: function() {
			neatoSmartApp.setResponseText(null);
			document.querySelector('#userHomePage').setAttribute('aria-hidden', 'true');
			document.querySelector('#btnGoToAddRobot').removeEventListener('click', neatoSmartApp.goToAddRobotPage , true);
			document.querySelector('#btnGoToCommandRobot').removeEventListener('click', neatoSmartApp.goToRobotCommandsPage , true);
			document.querySelector('#btnGoToDebug').removeEventListener('click', neatoSmartApp.goToDebugOptionsPage , true);
			document.querySelector('#btnGoToSchedule').removeEventListener('click', neatoSmartApp.goToSchedule , true);
			document.querySelector('#btnGoToSettings').removeEventListener('click', neatoSmartApp.goToSettingsPage , true);
			document.querySelector('#btnLogout').removeEventListener('click', neatoSmartApp.logoutUser , true);
			document.querySelector('#btnGoToNewSchedule').removeEventListener('click', neatoSmartApp.goToNewSchedulePage , true);
		},
		
		hideUserShowWelcome: function() {
			neatoSmartApp.hideUserHomePage();
			neatoSmartApp.showWelcomePage();
		},
		goToNewSchedulePage: function() {
			neatoSmartApp.hideUserHomePage();
			neatoSmartApp.showNewSchedulePage();
		},
		showNewSchedulePage: function() {
			neatoSmartApp.setResponseText(null);
			CURRENT_PAGE = ROBOT_NEW_SCHEDULE_PAGE;
			document.querySelector('#newScheduleRobot').setAttribute('aria-hidden', 'false');
			document.querySelector('#btnCreateSchedue').addEventListener('click', neatoSmartApp.createSchedule , true);
			document.querySelector('#btnGetScheduleEvents').addEventListener('click', neatoSmartApp.getScheduleEvents , true);
			document.querySelector('#btnAddScheduleEvent').addEventListener('click', neatoSmartApp.addScheduleEvent , true);
			document.querySelector('#btnUpdateScheduleEvent').addEventListener('click', neatoSmartApp.updateScheduleEvent , true);
			document.querySelector('#getScheduleEventData').addEventListener('click', neatoSmartApp.getScheduleEventData , true);			
			document.querySelector('#btnDeleteScheduleEvent').addEventListener('click', neatoSmartApp.deleteScheduleEvent , true);
			document.querySelector('#updateSchedule').addEventListener('click', neatoSmartApp.updateSchedule , true);
			document.querySelector('#btnGetScheduleData').addEventListener('click', neatoSmartApp.getScheduleData, true);
			document.querySelector('#newUseBasicSchedule').addEventListener('click', neatoSmartApp.useBasicScheduleNewOnClick, true);
			neatoSmartApp.getScheduleEvents();
		},
		
		hideNewSchedulePage: function() {
			neatoSmartApp.setResponseText(null);
			document.querySelector('#newScheduleRobot').setAttribute('aria-hidden', 'true');
			document.querySelector('#btnCreateSchedue').removeEventListener('click', neatoSmartApp.createSchedule , true);
			document.querySelector('#btnGetScheduleEvents').removeEventListener('click', neatoSmartApp.getScheduleEvents , true);
			document.querySelector('#btnAddScheduleEvent').removeEventListener('click', neatoSmartApp.addScheduleEvent , true);
			document.querySelector('#btnUpdateScheduleEvent').removeEventListener('click', neatoSmartApp.updateScheduleEvent , true);
			document.querySelector('#getScheduleEventData').removeEventListener('click', neatoSmartApp.getScheduleEventData , true);			
			document.querySelector('#btnDeleteScheduleEvent').removeEventListener('click', neatoSmartApp.deleteScheduleEvent , true);
			document.querySelector('#updateSchedule').removeEventListener('click', neatoSmartApp.updateSchedule , true);
			document.querySelector('#btnGetScheduleData').removeEventListener('click', neatoSmartApp.useBasicScheduleNewOnClick, true);
			document.querySelector('#newUseBasicSchedule').removeEventListener('click', neatoSmartApp.useBasicScheduleNewOnClick, true);
		},
		
		hideNewScheduleShowHome: function() {
			neatoSmartApp.hideNewSchedulePage();
			neatoSmartApp.showUserHomepage();
		},
		
		
		goToSchedule: function() {
			neatoSmartApp.hideUserHomePage();
			neatoSmartApp.showSchedulePage();
		},

		goToRobotCommandsPage: function() {
			neatoSmartApp.hideUserHomePage();
			neatoSmartApp.showRobotCommandPage();
		},
		
		goToDebugOptionsPage: function() {
			neatoSmartApp.hideUserHomePage();
			neatoSmartApp.showDebugOptionsPage();
		},
		
		goToSettingsPage: function() {
			neatoSmartApp.hideUserHomePage();
			neatoSmartApp.showSettingsPage();
		},
		
		goToAddRobotPage: function() {
			neatoSmartApp.hideUserHomePage();
			neatoSmartApp.showAddRobotPage();
		},
		
		goToAssociateDirect: function() {
			neatoSmartApp.hideAddRobotPage();
			neatoSmartApp.showAssociateDirectPage();
		},
		
		goToRobotSettingsPage: function() {
			neatoSmartApp.hideSettingsPage();
			neatoSmartApp.showRobotSettingsPage();
		},
		
		goToUserSettingsPage: function() {
			neatoSmartApp.hideSettingsPage();
			neatoSmartApp.showUserSettingsPage();
		},
		
		goToRobotAtlas: function() {
			neatoSmartApp.hideRobotSettingsPage();
			neatoSmartApp.showRobotAtlasPage();
		},
		
		goToRobotSetName: function() {
			neatoSmartApp.hideRobotSettingsPage();
			neatoSmartApp.showRobotSetNamePage();
		},
		
		goToRobotMap: function() {
			neatoSmartApp.hideDebugOptionsPage();
			neatoSmartApp.showRobotMapPage();
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
		
		hideRobotCommandShowHomePage: function() {
			neatoSmartApp.hideRobotCommandPage();
			neatoSmartApp.showUserHomepage();
		},
		
		hideDebugOptionsShowHomePage: function() {
			neatoSmartApp.hideDebugOptionsPage();
			neatoSmartApp.showUserHomepage();
		},
		
		hideSettingsShowHomePage: function() {
			neatoSmartApp.hideSettingsPage();
			neatoSmartApp.showUserHomepage();
		},
		
		hideAddRobotShowHomePage: function() {
			neatoSmartApp.hideAddRobotPage();
			neatoSmartApp.showUserHomepage();
		},
		
		hideDirectAssociateRobotShowAddRobotPage: function() {			
			neatoSmartApp.hideAssociateDirectPage();
			neatoSmartApp.showAddRobotPage();
		},
		
		hideDirectAssociateRobotShowHomePage: function() {
			neatoSmartApp.hideAssociateDirectPage();
			neatoSmartApp.showUserHomepage();
		},
		
		hideRobotSettingsShowSettingsPage: function() {
			neatoSmartApp.hideRobotSettingsPage();
			neatoSmartApp.showSettingsPage();
		},
		
		hideAtlasShowRobotSettingsPage: function() {
			neatoSmartApp.hideRobotAtlasPage();
			neatoSmartApp.showRobotSettingsPage();
		},
		
		hideSetRobotNameShowRobotSettingsPage: function() {
			neatoSmartApp.hideRobotSetNamePage();
			neatoSmartApp.showRobotSettingsPage();
		},
		
		hideUserSettingsShowSettingsPage: function() {
			neatoSmartApp.hideUserSettingsPage();
			neatoSmartApp.showSettingsPage();
		},
		
		hideDirectAssociateRobotShowRobotCommandPage: function() {
			neatoSmartApp.hideAssociateDirectPage();
			neatoSmartApp.showRobotCommandPage();
		},
		
		hideRobotMapShowDebugOptionsPage: function() {
			neatoSmartApp.hideRobotMapPage();
			neatoSmartApp.showDebugOptionsPage();
		},
		
		showAssociateRobotPage: function() {
			neatoSmartApp.setResponseText(null);
			CURRENT_PAGE = ROBOT_ASSOCIATION_PAGE;
			document.querySelector('#associateRobot').setAttribute('aria-hidden', 'false');
			document.querySelector('#btnAssociateRobot').addEventListener('click', neatoSmartApp.associateRobot , true);
			document.querySelector('#btnDisassociateRobot').addEventListener('click', neatoSmartApp.disassociateRobot , true);
			document.querySelector('#btnDisassociateAllRobot').addEventListener('click', neatoSmartApp.disassociateAllRobots , true);
			document.querySelector('#btnGetAssociatedRobots').addEventListener('click', neatoSmartApp.getAssociatedRobots , true);
			document.querySelector('#btnSetRobotName').addEventListener('click', neatoSmartApp.setRobotName , true);
			document.querySelector('#btnSetRobotName2').addEventListener('click', neatoSmartApp.setRobotName2 , true);
			document.querySelector('#btnGetAssociatedRobotDetail').addEventListener('click', neatoSmartApp.getAssociatedRobotDetail , true);
		},

		hideAssociateRobotPage: function() {
			document.querySelector('#associateRobot').setAttribute('aria-hidden', 'true');
			document.querySelector('#btnAssociateRobot').removeEventListener('click', neatoSmartApp.associateRobot , true);
			document.querySelector('#btnDisassociateRobot').removeEventListener('click', neatoSmartApp.disassociateRobot , true);
			document.querySelector('#btnDisassociateAllRobot').removeEventListener('click', neatoSmartApp.disassociateAllRobots , true);
			document.querySelector('#btnGetAssociatedRobots').removeEventListener('click', neatoSmartApp.getAssociatedRobots , true);
			document.querySelector('#btnSetRobotName').removeEventListener('click', neatoSmartApp.setRobotName , true);
			document.querySelector('#btnSetRobotName2').removeEventListener('click', neatoSmartApp.setRobotName2 , true);			
			document.querySelector('#btnGetAssociatedRobotDetail').removeEventListener('click', neatoSmartApp.getAssociatedRobotDetail , true);
		},

		showDebugOptionsPage: function() {
			neatoSmartApp.setResponseText(null);
			CURRENT_PAGE = DEBUG_OPTIONS_PAGE;
			document.querySelector('#debugOptions').setAttribute('aria-hidden', 'false');
			document.querySelector('#btnDisassociateAllRobot').addEventListener('click', neatoSmartApp.disassociateAllRobots , true);
			document.querySelector('#btnTestGoToRobotMap').addEventListener('click', neatoSmartApp.goToRobotMap , true);
			document.querySelector('#btnTestDissociateRobot').addEventListener('click', neatoSmartApp.disassociateRobot , true);
			document.querySelector('#btnTestAllAssociatedRobots').addEventListener('click', neatoSmartApp.getAssociatedRobots , true);
			document.querySelector('#btnTestRobotDetail').addEventListener('click', neatoSmartApp.getAssociatedRobotDetail , true);
			document.querySelector('#btnTestUserDetail').addEventListener('click', neatoSmartApp.getUserDetails , true);
			
			
			document.querySelector('#btnOpenClosePeerConn').addEventListener('click', neatoSmartApp.openClosePeerConn , true);
			document.querySelector('#btnOpenClosePeerConn2').addEventListener('click', neatoSmartApp.openClosePeerConn2 , true);
			document.querySelector('#btnSendStartStopCleanCommand').addEventListener('click', neatoSmartApp.startStopCleaning , true);
			document.querySelector('#btnSendStartStopCleanCommand2').addEventListener('click', neatoSmartApp.startStopCleaning2 , true);
			document.querySelector('#btnPauseCleaningCommand').addEventListener('click', neatoSmartApp.pauseCleaning , true);
			document.querySelector('#btnEnableSchedule').addEventListener('click', neatoSmartApp.enableSchedule , true);
			document.querySelector('#btnDisableSchedule').addEventListener('click', neatoSmartApp.disableSchedule , true);
			document.querySelector('#btnSetRobotClock').addEventListener('click', neatoSmartApp.setRobotClock , true);
			document.querySelector('#btnRefreshUI').addEventListener('click', neatoSmartApp.refreshUIByState, true);
			
			var directConn = localStorage.getItem('isPeerConnection');
			if ((directConn == null) || (directConn == "false")) {
				document.querySelector('#btnOpenClosePeerConn').value = "Open Connection";
			}
			else {
				document.querySelector('#btnOpenClosePeerConn').value = "Close Connection";
			}
		},
			
		hideDebugOptionsPage: function() {
			neatoSmartApp.setResponseText(null);			
			document.querySelector('#debugOptions').setAttribute('aria-hidden', 'true');			
			document.querySelector('#btnDisassociateAllRobot').removeEventListener('click', neatoSmartApp.disassociateAllRobots , true);
			document.querySelector('#btnOpenClosePeerConn').removeEventListener('click', neatoSmartApp.openClosePeerConn , true);
			
			document.querySelector('#btnTestGoToRobotMap').removeEventListener('click', neatoSmartApp.goToRobotMap , true);
			document.querySelector('#btnTestDissociateRobot').removeEventListener('click', neatoSmartApp.disassociateRobot , true);
			document.querySelector('#btnTestAllAssociatedRobots').removeEventListener('click', neatoSmartApp.getAssociatedRobots , true);
			document.querySelector('#btnTestRobotDetail').removeEventListener('click', neatoSmartApp.getAssociatedRobotDetail , true);
			document.querySelector('#btnTestUserDetail').removeEventListener('click', neatoSmartApp.getUserDetails , true);
			
			document.querySelector('#btnOpenClosePeerConn2').removeEventListener('click', neatoSmartApp.openClosePeerConn2 , true);
			document.querySelector('#btnSendStartStopCleanCommand').removeEventListener('click', neatoSmartApp.startStopCleaning , true);
			document.querySelector('#btnSendStartStopCleanCommand2').removeEventListener('click', neatoSmartApp.startStopCleaning2 , true);
			document.querySelector('#btnPauseCleaningCommand').removeEventListener('click', neatoSmartApp.pauseCleaning , true);
			document.querySelector('#btnEnableSchedule').removeEventListener('click', neatoSmartApp.enableSchedule , true);
			document.querySelector('#btnDisableSchedule').removeEventListener('click', neatoSmartApp.disableSchedule , true);
			document.querySelector('#btnSetRobotClock').removeEventListener('click', neatoSmartApp.setRobotClock , true);
			document.querySelector('#btnRefreshUI').removeEventListener('click', neatoSmartApp.refreshUIByState, true);
		},
		
		showRobotCommandPage: function() {
			neatoSmartApp.setResponseText(null);
			CURRENT_PAGE = ROBOT_COMMAND_PAGE;
			document.querySelector('#robotCommands').setAttribute('aria-hidden', 'false');
			document.querySelector('#btnSendToBase').addEventListener('click', neatoSmartApp.sendToBaseCommand, true);
			document.querySelector('#btnGetState').addEventListener('click', neatoSmartApp.getRobotState, true);
			
			document.querySelector('#btnStartStopCleaning').addEventListener('click', neatoSmartApp.startStopCleaningPeer, true);			
			var robotStarted = localStorage.getItem('isRobotStarted');
			if ((robotStarted == null) || (robotStarted == "false")) {
				document.querySelector('#btnStartStopCleaning').value = "Start Cleaning";
			} 
			else {
				document.querySelector('#btnStartStopCleaning').value = "Stop Cleaning";
			}
		},
		
		hideRobotCommandPage: function() {
			neatoSmartApp.setResponseText(null);			
			document.querySelector('#robotCommands').setAttribute('aria-hidden', 'true');			
			document.querySelector('#btnStartStopCleaning').removeEventListener('click', neatoSmartApp.startStopCleaningPeer, true);
			document.querySelector('#btnSendToBase').removeEventListener('click', neatoSmartApp.sendToBaseCommand, true);
			document.querySelector('#btnGetState').removeEventListener('click', neatoSmartApp.getRobotState, true);
		},
		
		showSettingsPage: function() {
			neatoSmartApp.setResponseText(null);
			CURRENT_PAGE = SETTINGS_PAGE;
			document.querySelector('#settings').setAttribute('aria-hidden', 'false');				
			document.querySelector('#btnRobotSettings').addEventListener('click',  neatoSmartApp.goToRobotSettingsPage, true);
			document.querySelector('#btnUserSettings').addEventListener('click',  neatoSmartApp.goToUserSettingsPage, true);
		},
		
		hideSettingsPage: function() {
			neatoSmartApp.setResponseText(null);			
			document.querySelector('#settings').setAttribute('aria-hidden', 'true');
			document.querySelector('#btnRobotSettings').removeEventListener('click',  neatoSmartApp.goToRobotSettingsPage, true);
			document.querySelector('#btnUserSettings').removeEventListener('click',  neatoSmartApp.goToUserSettingsPage, true);
		},
		
		showAddRobotPage: function() {
			neatoSmartApp.setResponseText(null);	
			CURRENT_PAGE = ADD_ROBOT_PAGE;
			document.querySelector('#addRobot').setAttribute('aria-hidden', 'false');
			document.querySelector('#btnDiscoverRobots').addEventListener('click', neatoSmartApp.discoverRobots , true);
			document.querySelector('#btnAssociateRobotDirect').addEventListener('click', neatoSmartApp.goToAssociateDirect , true);			
			document.querySelector('#btnGetAllAssociated').addEventListener('click', neatoSmartApp.getAssociatedRobots , true);
		},
		
		hideAddRobotPage: function() {
			neatoSmartApp.setResponseText(null);			
			document.querySelector('#addRobot').setAttribute('aria-hidden', 'true');				
			document.querySelector('#btnDiscoverRobots').removeEventListener('click', neatoSmartApp.discoverRobots , true);
			document.querySelector('#btnAssociateRobotDirect').removeEventListener('click', neatoSmartApp.goToAssociateDirect , true);
			document.querySelector('#btnGetAllAssociated').removeEventListener('click', neatoSmartApp.getAssociatedRobots , true);
		},
		
		
		showAssociateDirectPage: function() {
			neatoSmartApp.setResponseText(null);
			CURRENT_PAGE = DIRECT_ASSOCIATE_ROBOT_PAGE;
			document.querySelector('#associateDirect').setAttribute('aria-hidden', 'false');				
			document.querySelector('#btnAssociateDirect').addEventListener('click', neatoSmartApp.associateRobot , true);			
		},
		
		hideAssociateDirectPage: function() {
			neatoSmartApp.setResponseText(null);			
			document.querySelector('#associateDirect').setAttribute('aria-hidden', 'true');				
			document.querySelector('#btnAssociateDirect').removeEventListener('click', neatoSmartApp.associateRobot , true);			
		},
		
		showRobotSettingsPage: function() {
			neatoSmartApp.setResponseText(null);
			CURRENT_PAGE = ROBOT_SETTINGS_PAGE;
			document.querySelector('#robotSettings').setAttribute('aria-hidden', 'false');				
			document.querySelector('#btnGoToSetRobotName').addEventListener('click', neatoSmartApp.goToRobotSetName , true);			
			document.querySelector('#btnGoToAtlas').addEventListener('click', neatoSmartApp.goToRobotAtlas, true);
			document.querySelector('#btnGetRobotDetail').addEventListener('click', neatoSmartApp.getAssociatedRobotDetail, true);			
			document.querySelector('#btnEnableNotifications').addEventListener('click', neatoSmartApp.setNotifications, true);
			
			var isNotificationsON = localStorage.getItem('isNotificationsON');			
			if ((isNotificationsON == null) || (isNotificationsON == "false")) {
				localStorage.setItem('isNotificationsON', "false");
				document.querySelector('#btnEnableNotifications').value = "ON Notifications";				
			}
			else {
				document.querySelector('#btnEnableNotifications').value = "OFF Notifications";
			}
			
			// document.querySelector('#btnGetRobotOnlineStatus').addEventListener('click', neatoSmartApp.getAssociatedRobotOnlineStatus, true);
		},
		
		hideRobotSettingsPage: function() {
			neatoSmartApp.setResponseText(null);
			document.querySelector('#robotSettings').setAttribute('aria-hidden', 'true');
			document.querySelector('#btnGoToSetRobotName').removeEventListener('click', neatoSmartApp.goToRobotSetName , true);
			document.querySelector('#btnGoToAtlas').removeEventListener('click', neatoSmartApp.goToRobotAtlas , true);
			document.querySelector('#btnGetRobotDetail').removeEventListener('click', neatoSmartApp.getAssociatedRobotDetail, true);
			document.querySelector('#btnEnableNotifications').removeEventListener('click', neatoSmartApp.setNotifications, true);
			// document.querySelector('#btnGetRobotOnlineStatus').removeEventListener('click', neatoSmartApp.getAssociatedRobotOnlineStatus, true);
		},
		
		showRobotSetNamePage: function() {
			neatoSmartApp.setResponseText(null);
			CURRENT_PAGE = ROBOT_SET_NAME_PAGE;			
			document.querySelector('#setRobotName').setAttribute('aria-hidden', 'false');
			document.querySelector('#btnSetRobotName').addEventListener('click', neatoSmartApp.setRobotName2, true);
		},
		
		hideRobotSetNamePage: function() {
			neatoSmartApp.setResponseText(null);
			document.querySelector('#setRobotName').setAttribute('aria-hidden', 'true');
			document.querySelector('#btnSetRobotName').removeEventListener('click', neatoSmartApp.setRobotName2, true);
		},
		
		showUserSettingsPage: function() {
			neatoSmartApp.setResponseText(null);
			CURRENT_PAGE = USER_SETTINGS_PAGE;			
			document.querySelector('#userSettings').setAttribute('aria-hidden', 'false');
			document.querySelector('#btnGetUserDetail').addEventListener('click', neatoSmartApp.getUserDetails, true);
			document.querySelector('#goToChangePassword').addEventListener('click', neatoSmartApp.hideSettingShowChangePasswordPage, true)
		},
		
		hideUserSettingsPage: function() {
			neatoSmartApp.setResponseText(null);
			document.querySelector('#userSettings').setAttribute('aria-hidden', 'true');
			document.querySelector('#btnGetUserDetail').removeEventListener('click', neatoSmartApp.getUserDetails, true);
			document.querySelector('#goToChangePassword').removeEventListener('click', neatoSmartApp.hideSettingShowChangePasswordPage, true)
		},
		
		hideAssociateShowHome: function() {
			neatoSmartApp.hideAssociateRobotPage();
			neatoSmartApp.showUserHomepage();
		},
		
		showRobotMapPage: function() {
			CURRENT_PAGE = ROBOT_MAP_PAGE;
			document.querySelector('#robotMapSection').setAttribute('aria-hidden', 'false');
			var robotId = localStorage.getItem('robotId');
			if ((robotId == null) || (robotId.length == 0)) {
				$('#robotSectionHeader').text("Robot Map");
			}
			else {
				$('#robotSectionHeader').text("Map of "+robotId);
			}
			
			//document.querySelector('#robotSectionHeader')
			document.querySelector('#btnGetRobotMap').addEventListener('click', neatoSmartApp.getRobotMap , true);
		},
		
		hideRobotMapPage: function() {
			neatoSmartApp.setResponseText(null);
			document.querySelector('#robotMapSection').setAttribute('aria-hidden', 'true');
			document.querySelector('#btnGetRobotMap').removeEventListener('click', neatoSmartApp.getRobotMap , true);
			$('#robotMapImage').replaceWith('<img src="" id="robotMapImage"/>');
		},

		hideHomeShowMap: function() {
			neatoSmartApp.hideUserHomePage();
			neatoSmartApp.showRobotMapPage();
		},
		hideMapShowHomePage: function() {
			neatoSmartApp.hideRobotMapPage();
			neatoSmartApp.showUserHomepage();
		},
		
		//Atlas
		showRobotAtlasPage: function() {
			CURRENT_PAGE = ROBOT_ATLAS_PAGE;
			document.querySelector('#robotAtlasSection').setAttribute('aria-hidden', 'false');
			
			var robotId = localStorage.getItem('robotId');
			if ((robotId == null) || (robotId.length == 0)) {
				$('#robotAtlasHeader').text("Robot Atlas");
			}
			else {
				$('#robotAtlasHeader').text("Atlas of "+robotId);
			}
		
			document.querySelector('#btnGetRobotAtlasMetadata').addEventListener('click', neatoSmartApp.getRobotAtlasMetadata , true);
			document.querySelector('#btnUpdateRobotAtlasMetadata').addEventListener('click', neatoSmartApp.updateRobotAtlasMetadata , true);
			document.querySelector('#btnGetGridData').addEventListener('click', neatoSmartApp.getAtlasGridData , true);
		},
		
		hideRobotAtlasPage: function() {
			neatoSmartApp.setResponseText(null);
			document.querySelector('#robotAtlasSection').setAttribute('aria-hidden', 'true');
			document.querySelector('#btnGetRobotAtlasMetadata').removeEventListener('click', neatoSmartApp.getRobotAtlasMetadata , true);
			document.querySelector('#btnUpdateRobotAtlasMetadata').removeEventListener('click', neatoSmartApp.updateRobotAtlasMetadata , true);
			document.querySelector('#btnGetGridData').removeEventListener('click', neatoSmartApp.getAtlasGridData , true);
		},

		hideHomeShowAtlas: function() {
			neatoSmartApp.hideUserHomePage();
			neatoSmartApp.showRobotAtlasPage();
		},
		hideAtlasShowHomePage: function() {
			neatoSmartApp.hideRobotAtlasPage();
			neatoSmartApp.showUserHomepage();
		},
		backButtonPressed: function() {  
			/*e.preventDefault();*/
		 	if (CURRENT_PAGE == ROBOT_ASSOCIATION_PAGE) {
		 		neatoSmartApp.hideAssociateShowHome();
		 	} 
		 	else if (CURRENT_PAGE == USER_LOGIN_PAGE) {
		 		neatoSmartApp.hideLoginShowWelcome();
		 	} 
		 	else if (CURRENT_PAGE == REGISTER_USER_PAGE) {
		 		neatoSmartApp.hideRegisterShowWelcome();
		 	} 
		 	else if(CURRENT_PAGE == ROBOT_SCHEDULE_PAGE) {
		 		neatoSmartApp.hideScheduleShowHome();
		 	} 
		 	else if(CURRENT_PAGE == ROBOT_MAP_PAGE) {
		 		neatoSmartApp.hideRobotMapShowDebugOptionsPage();
		 	} 
		 	else if (CURRENT_PAGE == ROBOT_ATLAS_PAGE) {
		 		neatoSmartApp.hideAtlasShowRobotSettingsPage();
		 	} 
		 	else if (CURRENT_PAGE == ROBOT_COMMAND_PAGE) {
		 		neatoSmartApp.hideRobotCommandShowHomePage();		 	
			} 
		 	else if (CURRENT_PAGE == DEBUG_OPTIONS_PAGE) {
				neatoSmartApp.hideDebugOptionsShowHomePage();
			}
			else if (CURRENT_PAGE == SETTINGS_PAGE) {
				neatoSmartApp.hideSettingsShowHomePage();
			}
			else if (CURRENT_PAGE == ADD_ROBOT_PAGE) {
				neatoSmartApp.hideAddRobotShowHomePage();
			}		 	
			else if (CURRENT_PAGE == DIRECT_ASSOCIATE_ROBOT_PAGE) {
				neatoSmartApp.hideDirectAssociateRobotShowAddRobotPage();
			}
			else if (CURRENT_PAGE == ROBOT_SETTINGS_PAGE) {
				neatoSmartApp.hideRobotSettingsShowSettingsPage();
			}
			else if (CURRENT_PAGE == ROBOT_SET_NAME_PAGE) {
				neatoSmartApp.hideSetRobotNameShowRobotSettingsPage();
			}		 	
			else if (CURRENT_PAGE == USER_SETTINGS_PAGE) {
				neatoSmartApp.hideUserSettingsShowSettingsPage();
			}
			else if (CURRENT_PAGE == FORGET_PASSWORD_PAGE) {
				neatoSmartApp.hideForgetPasswordShowWelcome();
			}
			else if (CURRENT_PAGE == CHANGE_PASSWORD_PAGE) {
				neatoSmartApp.hideChangePasswordShowUserSetting();
			}
			else if (CURRENT_PAGE == ROBOT_NEW_SCHEDULE_PAGE) {
				neatoSmartApp.hideNewScheduleShowHome();
			}
		 	else {
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
				// document.querySelector('#btnStartStopCleaningServer').value = "Stop Cleaning";
				// document.querySelector('#btnStartStopCleaningPeer').value = "Stop Cleaning";
				document.querySelector('#btnStartStopCleaning').value = "Stop Cleaning";

			} else if(!isRobotStarted) {
				// set the text as Start
				// document.querySelector('#btnStartStopCleaningServer').value = "Start Cleaning";
				// document.querySelector('#btnStartStopCleaningPeer').value = "Start Cleaning";
				document.querySelector('#btnStartStopCleaning').value = "Start Cleaning";

			}
			document.addEventListener("backbutton", neatoSmartApp.backButtonPressed, false);
			document.addEventListener("menubutton", neatoSmartApp.menuButtonPressed, false);
		},

		isLoggedIn: function() {
			return localStorage.getItem('loggedIn');
		},
		
		refreshUI: function() {
			var robotId = localStorage.getItem('robotId');
			RobotPluginManager.getRobotOnlineStatus(robotId, neatoSmartApp.refreshGetOnlineStatusSuccess, neatoSmartApp.refreshGetOnlineStatusError);			
		},
		
		refreshUIByState: function() {
			var robotId = localStorage.getItem('robotId');
			if ((robotId == null) || (robotId.length == 0)) {
				Alert("Please associate a Robot");
			}			
			
			neatoSmartApp.showProgressBar();
			RobotPluginManager.getRobotOnlineStatus(robotId, neatoSmartApp.refreshGetOnlineStatusSuccess, neatoSmartApp.refreshGetOnlineStatusError);			
		},
		
		refreshGetOnlineStatusSuccess: function(result) {
			var isOnline = result['online'];			
			if (isOnline == true) {				
				// Getting current state of the robot
				var robotId = result['robotId'];
				RobotPluginManager.sendCommandToRobot2(robotId, COMMAND_GET_ROBOT_STATE, {}, neatoSmartApp.refreshGetStateSuccess, neatoSmartApp.refreshGetStateError);
			}
			else {
				neatoSmartApp.hideProgressBar();				
			}
		},
		
		refreshGetOnlineStatusError: function(result) {			
			neatoSmartApp.hideProgressBar();			
		},
		
		refreshGetStateSuccess: function(result) {
			neatoSmartApp.hideProgressBar();
			// Reset UI based on the robot state code
			var currentStateCode = result['state_code'];
			if (currentStateCode == ROBOT_STATE_CLEANING) {
				localStorage.setItem('isRobotStarted', "true");
				document.querySelector('#btnSendStartStopCleanCommand').value = "Stop Cleaning";
				document.querySelector('#btnSendStartStopCleanCommand2').value = "Stop Cleaning (New)";
				
			}
			else {
				localStorage.setItem('isRobotStarted', "false");				
				document.querySelector('#btnSendStartStopCleanCommand').value = "Start Cleaning";
				document.querySelector('#btnSendStartStopCleanCommand2').value = "Start Cleaning (New)";
			}
		},
		
		refreshGetStateError: function(result) {
			neatoSmartApp.hideProgressBar();			
		}
	}
}());

document.addEventListener("deviceready", neatoSmartApp.loaded, false);
