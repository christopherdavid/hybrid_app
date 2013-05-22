

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
	var ROBOT_STATE_RESUMED		= 10008;
	
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
	var TEST_CLEANING_PAGE = 122;
	var SPOT_DEFINITION_PAGE = 123;
	var MANAGE_ROBOT_WIFI_PAGE = 124;
	var NOTIFICATION_SETTINGS_PAGE = 125;
	
	// List of cleaning modes
	var CLEANING_MODE_ECO_TEXT = "Eco";
	var CLEANING_MODE_NORMAL_TEXT = "Normal";

	// List of cleaning categories
	var CLEANING_CATEGORY_ALL_TEXT = "All";
	var CLEANING_CATEGORY_SPOT_TEXT = "Spot";
	var CLEANING_CATEGORY_MANUAL_TEXT = "Manual";
	
	// List of fixed spot area-length
	var SPOT_AREA_LENGTH_5 = "5";
	var SPOT_AREA_LENGTH_2 = "2";
	var SPOT_AREA_LENGTH_1 = "1";

	// List of fixed spot area-width
	var SPOT_AREA_HEIGHT_3 = "3";
	var SPOT_AREA_HEIGHT_4 = "4";
	var SPOT_AREA_HEIGHT_1 = "1";
	
	// List of fixed cleaning modifiers
	var CLEANING_MODIFIER_1x = "1";
	var CLEANING_MODIFIER_2x = "2";

	// List of fixed WiFi turn off durations
	var WIFI_TURN_OFF_DURATION_5MIN = 300;
	var WIFI_TURN_OFF_DURATION_10MIN = 600;
	var WIFI_TURN_OFF_DURATION_15MIN = 900;
	
	var CURRENT_PAGE = USER_HOME_PAGE;
	var eventIdList = [];
	var cleaningModeForBasicSchedule = CLEANING_MODE_ECO;
	var cleaningModifier = CLEANING_MODIFIER_1x;
	
	return {

		setResponseText: function(result) {
			if (result == null) {
				document.querySelector('#responseText').innerHTML = '';
				return;
			}
			document.querySelector('#responseText').innerHTML ="Response: "+JSON.stringify(result, null, 4);
		},
		
		setResponseTextAppend: function(result) {
			if (result == null) {
				return;
			}
			document.querySelector('#responseText').innerHTML += "<br /> Response: "+JSON.stringify(result, null, 4);
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
			neatoSmartApp.registerForRobotMessages();
			neatoSmartApp.registerRobotNotification2();
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
			neatoSmartApp.registerForRobotMessages();
			neatoSmartApp.registerRobotNotification2();
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
			//neatoSmartApp.hideChangePasswordShowUserSetting();
		},
		
		changePassErr: function(error) {
			neatoSmartApp.hideProgressBar();
			neatoSmartApp.setResponseText(error);
		},
		
		registerForRobotMessages: function() {
			RobotPluginManager.registerForRobotMessages(neatoSmartApp.successNotifyPushMessage, neatoSmartApp.errorNotifyPushMessage);
		},
		
		successNotifyPushMessage: function(result) {
			neatoSmartApp.setResponseText(result);
			alert(JSON.stringify(result));
		},
		
		errorNotifyPushMessage: function(error) {
			neatoSmartApp.setResponseText(error);
		},
		
		unregisterForRobotMessages: function() {
			RobotPluginManager.unregisterForRobotMessages(neatoSmartApp.successUnregisterPushMessage, neatoSmartApp.errorUnregisterPushMessage);
		},
		
		successUnregisterPushMessage: function(result) {
			neatoSmartApp.setResponseText(result);
		},
		
		errorUnregisterPushMessage: function(error) {
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
		
		registerEmailValidation: function() {
			var email = document.querySelector('#regmailid').value;
			var password = document.querySelector('#regpasskey').value;
			var name = document.querySelector('#regname').value;
			
			localStorage.setItem('email', email);
			neatoSmartApp.showProgressBar();
			
			var passwordConfirm = document.querySelector('#regpasskeyconfirm').value;
			UserPluginManager.createUser2(email, password, name, "", neatoSmartApp.successRegister, neatoSmartApp.errorRegister);
		},

		successRegister: function(result) {
			localStorage.setItem('loggedIn', 1);
			neatoSmartApp.setResponseText(result);
			neatoSmartApp.hideProgressBar();
			neatoSmartApp.hideRegisterShowHomePage();
		},


		errorRegister: function(error) {  
			neatoSmartApp.hideProgressBar();
			neatoSmartApp.setResponseText(error);
		},
		
		resendValidationMail: function() {
			var email = document.querySelector('#regmailid').value;
			
			localStorage.setItem('email', email);
			neatoSmartApp.showProgressBar();
			
			UserPluginManager.resendValidationMail(email, neatoSmartApp.successResendValidationMail, neatoSmartApp.errorResendValidationMail);
		},

		successResendValidationMail: function(result) {
			neatoSmartApp.setResponseText(result);
			neatoSmartApp.hideProgressBar();
		},

		errorResendValidationMail: function(error) {  
			neatoSmartApp.hideProgressBar();
			neatoSmartApp.setResponseText(error);
		},
		
		isUserValidated: function() {
			var email = localStorage.getItem('email');
			
			neatoSmartApp.showProgressBar();
			
			UserPluginManager.isUserValidated(email, neatoSmartApp.successValidation, neatoSmartApp.errorValidation);
		},
		
		successValidation: function(result) {
			neatoSmartApp.setResponseText(result);
			neatoSmartApp.hideProgressBar();
		},
		
		errorValidation: function(error) {
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
			localStorage.setItem('isCleaningPaused', "false");
			localStorage.setItem('isPeerConnection', "false");
			
			neatoSmartApp.refreshUI();
		},

		associateFoundRobotError: function(error) {
			neatoSmartApp.hideProgressBar();
			neatoSmartApp.setResponseText(error);
			
			localStorage.setItem('isRobotStarted', "false");
			localStorage.setItem('isCleaningPaused', "false");			
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
			localStorage.setItem('isCleaningPaused', "false");
			localStorage.setItem('isPeerConnection', "false");
			
			neatoSmartApp.refreshUI();
		},

		associateRobotError: function(error) {
			neatoSmartApp.hideProgressBar();
			neatoSmartApp.setResponseText(error);
			
			localStorage.setItem('robotId', "");
			localStorage.setItem('isRobotStarted', "false");
			localStorage.setItem('isCleaningPaused', "false");
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
			RobotPluginManager.sendCommandToRobot2(robotId, COMMAND_SEND_BASE, {}, neatoSmartApp.sendToBaseSuccess, neatoSmartApp.sendToBaseError);			
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
				document.querySelector('#btnSendStartStopCleanCommand2').value = "Start Cleaning (Deprecated)";
			}
			else {
				localStorage.setItem('isRobotStarted', "true");
				document.querySelector('#btnSendStartStopCleanCommand2').value = "Stop Cleaning (Deprecated)";
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
				alert("Robot is already stopped.");
			}
		},
		
		pauseCleaningSuccess: function(result) {
			neatoSmartApp.hideProgressBar();
			var robotStarted = localStorage.getItem('isRobotStarted');
			localStorage.setItem('isRobotStarted', "false");
			document.querySelector('#btnSendStartStopCleanCommand2').value = "Start Cleaning (Deprecated)";
		},
		
		pauseCleaningError: function(error) {
			neatoSmartApp.hideProgressBar();
		},

		startStopCleaning3: function() {
			var cleaningCategoryList = document.querySelector('#CleaningCategoryList');
			var cleaningModeList = document.querySelector('#CleaningModeList');
			var showNavigationControls = false;

			// default values
			var cleaningModeId = CLEANING_MODE_NORMAL;
			var cleaningCategoryId = CLEANING_CATEGORY_ALL;
			
			// Get category and mode selected by user
			var cleaningCategory = cleaningCategoryList.options[cleaningCategoryList.selectedIndex].text;
			var cleaningMode = cleaningModeList.options[cleaningModeList.selectedIndex].text;
			
			var robotId = localStorage.getItem('robotId');
			
			if ((robotId == null) || (robotId.length == 0)) {
				alert("Please associate a Robot");
				return;
			}

			if (cleaningCategory == null) {
				alert("Please select a cleaning category");
				return;
			}

			if (cleaningMode == null) {
				alert("Please select a cleaning mode");
				return;
			}
			
			// set cleaning categoryid
			if (cleaningCategory == CLEANING_CATEGORY_ALL_TEXT) {
				cleaningCategoryId = CLEANING_CATEGORY_ALL;
			}
			else if (cleaningCategory == CLEANING_CATEGORY_SPOT_TEXT) {
				cleaningCategoryId = CLEANING_CATEGORY_SPOT;
			}
			else if (cleaningCategory == CLEANING_CATEGORY_MANUAL_TEXT) {
				showNavigationControls = true;
				cleaningCategoryId = CLEANING_CATEGORY_MANUAL;
			}
			
			// set cleaning modeid
			if (cleaningMode == CLEANING_MODE_ECO_TEXT) {
				cleaningModeId = CLEANING_MODE_ECO;
			}
			
			
			// TODO: Temp variable. Later the robot status will be sent by the robot. Store in local storage as of now.
			var robotStarted = localStorage.getItem('isRobotStarted');
			
			if (robotStarted == null) {
				robotStarted = "false";
				localStorage.setItem('isRobotStarted', "false");
			}
			
			var divNavigationControls = document.querySelector('#divNavigationControls');

			neatoSmartApp.showProgressBar();
			if (robotStarted == "true") {
				RobotPluginManager.stopCleaning(robotId, neatoSmartApp.startStopCleaningSuccess3, 
							neatoSmartApp.startStopCleaningError3);
				
				divNavigationControls.style.visibility='hidden';

			} else {
				RobotPluginManager.startCleaning(robotId, cleaningCategoryId, cleaningModeId, cleaningModifier,
							neatoSmartApp.startStopCleaningSuccess3, neatoSmartApp.startStopCleaningError3);
				
				if (showNavigationControls == true) {
					divNavigationControls.style.visibility='visible';
				}
			}
		},

		startStopCleaningSuccess3: function(result) {
			neatoSmartApp.hideProgressBar();
			
			var robotStarted = localStorage.getItem('isRobotStarted');

			if (robotStarted == "true") {
				localStorage.setItem('robotStateUpdate', ROBOT_STATE_STOPPED);
				localStorage.setItem('isRobotStarted', "false");
				document.querySelector('#btnSendStartStopCleanCommand3').value = "Start Cleaning";
				localStorage.setItem('isCleaningPaused', "false");
				document.querySelector('#btnPauseResumeCleaningCommand3').value = "Pause Cleaning";
			}
			else {
				localStorage.setItem('robotStateUpdate', ROBOT_STATE_CLEANING);
				localStorage.setItem('isRobotStarted', "true");
				document.querySelector('#btnSendStartStopCleanCommand3').value = "Stop Cleaning";
			}
			neatoSmartApp.toggleStartStop();
			neatoSmartApp.setResponseTextAppend(result);
		},
		
		startStopCleaningError3: function(error) {
			neatoSmartApp.setResponseTextAppend(error);
			neatoSmartApp.hideProgressBar();
		},
		
		pauseResumeCleaning3: function() {
			var robotId = localStorage.getItem('robotId');
			if ((robotId == null) || (robotId.length == 0)) {
				alert("Please associate a Robot");
				return;
			}

			// First check if cleaning is started
			var robotStarted = localStorage.getItem('isRobotStarted');
			if ( (robotStarted == null) || (robotStarted == "false") ) {
				alert("Robot is already stopped.");
				return;
			}
			
			var cleaningPaused = localStorage.getItem('isCleaningPaused');
			if (cleaningPaused == null) {
				cleaningPaused = "false";
				localStorage.setItem('isCleaningPaused', "false");
			}

			neatoSmartApp.showProgressBar();
			if (cleaningPaused == "true") {
				RobotPluginManager.resumeCleaning(robotId, neatoSmartApp.pauseResumeCleaningSuccess3, 
						neatoSmartApp.pauseResumeCleaningError3);
			} else {
				RobotPluginManager.pauseCleaning(robotId, neatoSmartApp.pauseResumeCleaningSuccess3, 
						neatoSmartApp.pauseResumeCleaningError3);
			}
		},
		
		pauseResumeCleaningSuccess3: function(result) {
			neatoSmartApp.hideProgressBar();
			
			var cleaningPaused = localStorage.getItem('isCleaningPaused');
			if (cleaningPaused == "true") {
				localStorage.setItem('robotStateUpdate', ROBOT_STATE_RESUMED);
				localStorage.setItem('isCleaningPaused', "false");
				document.querySelector('#btnPauseResumeCleaningCommand3').value = "Pause Cleaning";
			}
			else {
				localStorage.setItem('robotStateUpdate', ROBOT_STATE_PAUSED);
				localStorage.setItem('isCleaningPaused', "true");
				document.querySelector('#btnPauseResumeCleaningCommand3').value = "Resume Cleaning";
			}
			neatoSmartApp.toggleStartStop();
			neatoSmartApp.setResponseTextAppend(result);
		},
		
		pauseResumeCleaningError3: function(error) {
			neatoSmartApp.setResponseTextAppend(error);
			neatoSmartApp.hideProgressBar();
		},
		
		setSpotDefinition: function() {
			var robotId = localStorage.getItem('robotId');
			if ((robotId == null) || (robotId.length == 0)) {
				alert("Please associate a Robot");
				return;
			}
			
			// Get length
			var spotAreaLengthList = document.querySelector('#SpotAreaLengthList');
			var spotCleaningAreaLength = spotAreaLengthList.options[spotAreaLengthList.selectedIndex].text;

			// Get height
			var spotAreaHeightList = document.querySelector('#SpotAreaHeightList');
			var spotCleaningAreaHeight = spotAreaHeightList.options[spotAreaHeightList.selectedIndex].text;
			
			RobotPluginManager.setSpotDefinition(robotId, spotCleaningAreaLength, spotCleaningAreaHeight, 
					neatoSmartApp.setSpotDefinitionSuccess, neatoSmartApp.setSpotDefinitionError);
		},
	
		setSpotDefinitionSuccess: function(result) {
			neatoSmartApp.setResponseText(result);
			neatoSmartApp.hideProgressBar();
		},
		
		setSpotDefinitionError: function(error) {
			neatoSmartApp.setResponseText(error);
			neatoSmartApp.hideProgressBar();
		},
		
		getSpotDefinition: function() {
			var robotId = localStorage.getItem('robotId');
			if ((robotId == null) || (robotId.length == 0)) {
				alert("Please associate a Robot");
				return;
			}
			RobotPluginManager.getSpotDefinition(robotId, neatoSmartApp.getSpotDefinitionSuccess, 
					neatoSmartApp.getSpotDefinitionError);
		},
	
		getSpotDefinitionSuccess: function(result) {
			neatoSmartApp.setResponseText(result);
			neatoSmartApp.hideProgressBar();
		},
		
		getSpotDefinitionError: function(error) {
			neatoSmartApp.setResponseText(error);
			neatoSmartApp.hideProgressBar();
		},
		
		enableSchedule: function() {
			var robotId = localStorage.getItem('robotId');
			if ((robotId == null) || (robotId.length == 0)) {
				alert("Please associate a Robot");
				return;
			}
			neatoSmartApp.showProgressBar();
			var isEnabled = localStorage.getItem('enableSchedule');
			if (isEnabled == 1) {
				RobotPluginManager.enableSchedule(robotId, 0, false, neatoSmartApp.enableDisableScheduleSuccess,
						neatoSmartApp.enableDisableScheduleErr);
			} else {
				RobotPluginManager.enableSchedule(robotId, 0, true, neatoSmartApp.enableDisableScheduleSuccess,
						neatoSmartApp.enableDisableScheduleErr);
			}
		
		},
		
		isScheduleEnabled: function() {
			var robotId = localStorage.getItem('robotId');
			if ((robotId == null) || (robotId.length == 0)) {
				alert("Please associate a Robot");
				return;
			}
			neatoSmartApp.showProgressBar();
			var scheduleType = 0; // For basic schedule type
			neatoSmartApp.showProgressBar();
			RobotPluginManager.isScheduleEnabled(robotId, scheduleType, neatoSmartApp.enableDisableScheduleSuccess, neatoSmartApp.enableDisableScheduleErr);
		},
		
		enableDisableScheduleSuccess: function(result) {
			if (result.isScheduleEnabled == true) {
				localStorage.setItem('enableSchedule', 1);
				document.querySelector('#btnEnableSchedule').value = "Disable Schedule (New)";
			}
			else {
				localStorage.setItem('enableSchedule', 0);
				document.querySelector('#btnEnableSchedule').value = "Enable Schedule (New)";
			}
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
			eventIdList = [];
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
		
		// TODO: We should have separate buttons to add event items
		addScheduleEventNew: function() {
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
				var	scheduleJson = PluginManagerHelper.createBasicScheduleEventObject(1, "10:30", cleaningModeForBasicSchedule);
				RobotPluginManager.addScheduleEvent(scheduleId, scheduleJson, neatoSmartApp.addScheduleEventSuccess, neatoSmartApp.addScheduleEventError);
				cleaningModeForBasicSchedule++;
				if (cleaningModeForBasicSchedule == 3) {
					// reset the value
					cleaningModeForBasicSchedule = CLEANING_MODE_ECO;
				}
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
		updateScheduleEventNew: function() {
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
				var	scheduleJson = PluginManagerHelper.createBasicScheduleEventObject(2, "12:30", CLEANING_MODE_NORMAL);
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
		
		turnVacuumOnOff: function() {
			var robotId = localStorage.getItem('robotId');
			if ((robotId == null) || (robotId.length == 0)) {
				alert("Please associate a Robot");
				return;
			}

			neatoSmartApp.showProgressBar();

			var vacuumON = localStorage.getItem('isVacuumON');			
			if (vacuumON == "true") {
				RobotPluginManager.turnVaccumOnOff(robotId, FLAG_OFF, neatoSmartApp.turnVaccumOnOffSuccess, 
							neatoSmartApp.turnVaccumOnOffError);
				
			} else {
				RobotPluginManager.turnVaccumOnOff(robotId, FLAG_ON, neatoSmartApp.turnVaccumOnOffSuccess, 
						neatoSmartApp.turnVaccumOnOffError);
			}
		},

		turnVaccumOnOffSuccess: function(result) {
			neatoSmartApp.hideProgressBar();
			neatoSmartApp.setResponseText(result);
			
			var vacuumON = localStorage.getItem('isVacuumON');
			if (vacuumON == "true") {
				localStorage.setItem('isVacuumON', "false");
				document.querySelector('#btnTurnVacuumOnOff').value = "Turn Robot Vacuum ON";
			}
			else {
				localStorage.setItem('isVacuumON', "true");
				document.querySelector('#btnTurnVacuumOnOff').value = "Turn Robot Vacuum OFF";
			}
		},
		
		turnVaccumOnOffError: function(error) {
			neatoSmartApp.setResponseText(error);
			neatoSmartApp.hideProgressBar();
		},
				
		showManageRobotWiFiPage: function() {
			CURRENT_PAGE = MANAGE_ROBOT_WIFI_PAGE;
			neatoSmartApp.hideDebugOptionsPage();
			document.getElementById('time_5mins').checked = true;
			document.querySelector('#manageRobotWiFi').setAttribute('aria-hidden', 'false');
			document.querySelector('#btnTurnWiFiOnOff').addEventListener('click', neatoSmartApp.turnWiFiOnOff, true);
			
			// Initialize "Turn WiFi On/Off" button text
			var wiFiON = localStorage.getItem('isWiFiON');
			if (wiFiON == null) {
				wiFiON = "false";
				localStorage.setItem('isWiFiON', "false");
			}
			
			if (wiFiON == "false") {
				document.querySelector('#btnTurnWiFiOnOff').value = "Turn Robot WiFi ON";
			}
			else {
				document.querySelector('#btnTurnWiFiOnOff').value = "Turn Robot WiFi OFF";
			}
		},

		turnWiFiOnOff: function() {
			var robotId = localStorage.getItem('robotId');
			if ((robotId == null) || (robotId.length == 0)) {
				alert("Please associate a Robot");
				return;
			}

			neatoSmartApp.showProgressBar();

			// Get WiFi turn off duration
			var wiFiTurnOnDurationInSec = WIFI_TURN_OFF_DURATION_5MIN;	// default duration
			if(document.getElementById('time_10mins').checked) {
				wiFiTurnOnDurationInSec = WIFI_TURN_OFF_DURATION_10MIN;
			}
			else if (document.getElementById('time_15mins').checked) {
				wiFiTurnOnDurationInSec = WIFI_TURN_OFF_DURATION_15MIN;
			}
			
			// Based on current status, turn WiFi On/Off 
			var wiFiON = localStorage.getItem('isWiFiON');
			if (wiFiON == "true") {
				RobotPluginManager.turnWiFiOnOff(robotId, FLAG_OFF, wiFiTurnOnDurationInSec, 
						neatoSmartApp.turnWiFiOnOffSuccess, neatoSmartApp.turnWiFiOnOffError);
				
			} else {
				RobotPluginManager.turnWiFiOnOff(robotId, FLAG_ON, wiFiTurnOnDurationInSec, 
						neatoSmartApp.turnWiFiOnOffSuccess, neatoSmartApp.turnWiFiOnOffError);
			}
		},
		
		turnWiFiOnOffSuccess: function(result) {
			var wiFiON = localStorage.getItem('isWiFiON');
			if (wiFiON == "true") {
				localStorage.setItem('isWiFiON', "false");
				document.querySelector('#btnTurnWiFiOnOff').value = "Turn Robot WiFi ON";
			}
			else {
				localStorage.setItem('isWiFiON', "true");
				document.querySelector('#btnTurnWiFiOnOff').value = "Turn Robot WiFi OFF";
			}
			neatoSmartApp.hideProgressBar();
			neatoSmartApp.setResponseText(result);
		},
		
		turnWiFiOnOffError: function(error) {
			neatoSmartApp.setResponseText(error);
			neatoSmartApp.hideProgressBar();
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
			var email = localStorage.getItem('email');
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
		
		getRobotVirtualOnlineStatus: function() {
			var robotId = localStorage.getItem('robotId');
			if ((robotId == null) || (robotName.length == 0)) {
				alert("Please associate a robot");
			}			
			else {
				neatoSmartApp.showProgressBar();
				RobotPluginManager.getRobotVirtualOnlineStatus(robotId, neatoSmartApp.getRobotOnlineStatusSuccess, neatoSmartApp.getRobotOnlineStatusError);
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
		
		setGlobalNotifications: function() {			
			var email = localStorage.getItem('email');
			if ((email == null) || (email.length == 0)) {
				alert("Please login");
			}			
			else {
				neatoSmartApp.showProgressBar();				
				var isNotificationsON = localStorage.getItem('isGlobalNotificationsON');				
				if (isNotificationsON == "false") {					
					UserPluginManager.turnNotificationOnoff(email, NOTIFICATIONS_GLOBAL_OPTION, true,
							neatoSmartApp.setGlobalNotificationsSuccess, neatoSmartApp.setGlobalNotificationsEror);	
				}
				else {					
					UserPluginManager.turnNotificationOnoff(email, NOTIFICATIONS_GLOBAL_OPTION, false,
							neatoSmartApp.setGlobalNotificationsSuccess, neatoSmartApp.setGlobalNotificationsEror);
				}
			}
		},
		
		setGlobalNotificationsSuccess: function(result) {
			neatoSmartApp.hideProgressBar();			
			neatoSmartApp.setResponseText(result);			
			
			var isNotificationsON = localStorage.getItem('isGlobalNotificationsON');				
			if (isNotificationsON == "false") {
				localStorage.setItem('isGlobalNotificationsON', "true");
				document.querySelector('#btnEnableGlobalNotifications').value = "OFF Notifications";
			}
			else {
				localStorage.setItem('isGlobalNotificationsON', "false");
				document.querySelector('#btnEnableGlobalNotifications').value = "ON Notifications";
			}	
		},
		
		setGlobalNotificationsEror: function(error) {
			neatoSmartApp.hideProgressBar();			
			neatoSmartApp.setResponseText(error);
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
		
		onClickDBNotificationON:function() {
			var email = localStorage.getItem('email')
			if ((email == null) || (email.length == 0)) {
				alert("Please login");
				return false;
			}
			
			neatoSmartApp.showProgressBar();				
			UserPluginManager.turnNotificationOnoff(email, NOTIFICATION_DIRT_BIN_FULL, true,
						neatoSmartApp.turnNotificationOnOffStatusSuccess, neatoSmartApp.turnNotificationOnOffStatusError);					
		},

		onClickDBNotificationOFF:function() {
			var email = localStorage.getItem('email')
			if ((email == null) || (email.length == 0)) {
				alert("Please login");
				return false;
			}	
			
			neatoSmartApp.showProgressBar();				
			UserPluginManager.turnNotificationOnoff(email, NOTIFICATION_DIRT_BIN_FULL, false,
					neatoSmartApp.turnNotificationOnOffStatusSuccess, neatoSmartApp.turnNotificationOnOffStatusError);					
		},
		
		onClickCDNotificationON:function() {
			var email = localStorage.getItem('email')
			if ((email == null) || (email.length == 0)) {
				alert("Please login");
				return false;
			}				

			neatoSmartApp.showProgressBar();				
			UserPluginManager.turnNotificationOnoff(email, NOTIFICATION_CLEANING_DONE, true,
					neatoSmartApp.turnNotificationOnOffStatusSuccess, neatoSmartApp.turnNotificationOnOffStatusError);					
		},	

		onClickCDNotificationOFF:function() {
			var email = localStorage.getItem('email')
			if ((email == null) || (email.length == 0)) {
				alert("Please login");
				return false;
			}	

			neatoSmartApp.showProgressBar();				
			UserPluginManager.turnNotificationOnoff(email, NOTIFICATION_CLEANING_DONE, false,
					neatoSmartApp.turnNotificationOnOffStatusSuccess, neatoSmartApp.turnNotificationOnOffStatusError);					
		},	
	
		onClickRSNotificationON:function() {
			var email = localStorage.getItem('email')
			if ((email == null) || (email.length == 0)) {
				alert("Please login");
				return false;
			}	
			
			neatoSmartApp.showProgressBar();				
			UserPluginManager.turnNotificationOnoff(email, NOTIFICATION_ROBOT_STUCK, true,
					neatoSmartApp.turnNotificationOnOffStatusSuccess, neatoSmartApp.turnNotificationOnOffStatusError);					
		},	

		onClickRSNotificationOFF:function() {			
			var email = localStorage.getItem('email')
			if ((email == null) || (email.length == 0)) {
				alert("Please login");
				return false;
			}	
			
			neatoSmartApp.showProgressBar();				
			UserPluginManager.turnNotificationOnoff(email, NOTIFICATION_ROBOT_STUCK, false,
					neatoSmartApp.turnNotificationOnOffStatusSuccess, neatoSmartApp.turnNotificationOnOffStatusError);					
		},	
		
		turnNotificationOnOffStatusSuccess: function(result) {
			var notificationId = result['key'];
			var notificationValue = result['value'];

			if (notificationId == NOTIFICATION_DIRT_BIN_FULL) {				
				if (notificationValue) {
					localStorage.setItem('isDBNotificationsON', "true");
				}
				else {
					localStorage.setItem('isDBNotificationsON', "false");
				}
			}
			else if (notificationId == NOTIFICATION_CLEANING_DONE) {				
				if (notificationValue) {
					localStorage.setItem('isCDNotificationsON', "true");
				}
				else {
					localStorage.setItem('isCDNotificationsON', "false");
				}
			}
			else if (notificationId == NOTIFICATION_ROBOT_STUCK) {				
				if (notificationValue) {
					localStorage.setItem('isRSNotificationsON', "true");
				}
				else {
					localStorage.setItem('isRSNotificationsON', "false");
				}
			}
			
			neatoSmartApp.hideProgressBar();			
			neatoSmartApp.setResponseText(result);
		},
		
		turnNotificationOnOffStatusError: function(error) {
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

		registerRobotNotification2 : function() {
			RobotPluginManager.registerNotifications2(neatoSmartApp.notificationStatusSuccess2, neatoSmartApp.notificationStatusError2);
		},
		
		notificationStatusSuccess2: function(result) {		
			neatoSmartApp.hideProgressBar();			
			var dataKeyCode =  (result['robotDataKeyId']);
			var robotId = (result['robotId']);
			var data = result['robotData'];
			
			if (dataKeyCode == ROBOT_CURRENT_STATE_CHANGED) {
				var state = data['robotCurrentState'];
				localStorage.setItem('robotCurrentState', state);
			
			}
			if (dataKeyCode == ROBOT_STATE_UPDATE) {
				var state = data['robotStateUpdate'];
				localStorage.setItem('robotStateUpdate', state);
			
			}
			if (dataKeyCode == ROBOT_SCHEDULE_STATE_CHANGED) {
				//TODO:
			}
			if (dataKeyCode == ROBOT_NAME_UPDATE) {
				//TODO:
			}
			
			neatoSmartApp.toggleStartStop();
		},
		
		toggleStartStop: function() {
			var currentState = localStorage.getItem('robotCurrentState');
			document.querySelector('#currentRobotState').innerHTML ="Actual State: " + neatoSmartApp.getStateFromCode(currentState);
			var state = localStorage.getItem('robotStateUpdate');
			document.querySelector('#robotState').innerHTML ="State: " + neatoSmartApp.getStateFromCode(state);
		},
		
		getStateFromCode: function(stateCode) {
			if (stateCode == ROBOT_STATE_CLEANING) {
				return "Started Cleaning";
			}
			else if (stateCode == ROBOT_STATE_STOPPED) {
				return "Stopped Cleaning";
			}
			else if (stateCode == ROBOT_STATE_PAUSED) {
				return "Paused Cleaning";
			}
			else if (stateCode == ROBOT_STATE_RESUMED) {
				return "Resumed Cleaning";
			}
			return "Not Available"
		},
		
		notificationStatusError2: function(error) {
			neatoSmartApp.hideProgressBar();			
			neatoSmartApp.setResponseTextAppend(error);
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
			document.querySelector('#registerUserEmailValidationButton').addEventListener('click', neatoSmartApp.registerEmailValidation, true);
			document.querySelector('#resendValidationMailButton').addEventListener('click', neatoSmartApp.resendValidationMail, true);
		},

		hideRegisterPage: function() {
			neatoSmartApp.setResponseText(null);
			document.querySelector('#registerUserPage').setAttribute('aria-hidden', 'true');
			document.querySelector('#registerUserButton').removeEventListener('click', neatoSmartApp.register, true);
			document.querySelector('#registerUserEmailValidationButton').removeEventListener('click', neatoSmartApp.registerEmailValidation, true);
			document.querySelector('#resendValidationMailButton').removeEventListener('click', neatoSmartApp.resendValidationMail, true);
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
		
		hideManageRobotWiFiPage: function() {
			document.querySelector('#manageRobotWiFi').setAttribute('aria-hidden', 'true');
			neatoSmartApp.showDebugOptionsPage();
			document.querySelector('#btnTurnWiFiOnOff').removeEventListener('click', neatoSmartApp.turnWiFiOnOff, true);
		},
		
		showUserHomepage: function() {
			CURRENT_PAGE = USER_HOME_PAGE;
			document.querySelector('#userHomePage').setAttribute('aria-hidden', 'false');
			document.querySelector('#btnGoToAddRobot').addEventListener('click', neatoSmartApp.goToAddRobotPage , true);
			document.querySelector('#btnGoToCommandRobot').addEventListener('click', neatoSmartApp.goToRobotCommandsPage , true);
			document.querySelector('#btnGoToDebug').addEventListener('click', neatoSmartApp.goToDebugOptionsPage , true);
			document.querySelector('#btnGoToTestCleaningPage').addEventListener('click', neatoSmartApp.goToTestCleaningPage , true);
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
			document.querySelector('#btnGoToTestCleaningPage').removeEventListener('click', neatoSmartApp.goToTestCleaningPage , true);
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
			document.querySelector('#btnAddScheduleEventNew').addEventListener('click', neatoSmartApp.addScheduleEventNew, true);
			document.querySelector('#btnUpdateScheduleEvent').addEventListener('click', neatoSmartApp.updateScheduleEvent , true);
			document.querySelector('#btnUpdateScheduleEventNew').addEventListener('click', neatoSmartApp.updateScheduleEventNew , true);
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
			document.querySelector('#btnAddScheduleEventNew').removeEventListener('click', neatoSmartApp.addScheduleEventNew , true);
			document.querySelector('#btnUpdateScheduleEvent').removeEventListener('click', neatoSmartApp.updateScheduleEvent , true);
			document.querySelector('#btnUpdateScheduleEventNew').removeEventListener('click', neatoSmartApp.updateScheduleEventNew , true);
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

		goToTestCleaningPage: function() {
			neatoSmartApp.hideUserHomePage();
			neatoSmartApp.showTestCleaningPage();
		},

		goToSpotDefinitionPage: function() {
			neatoSmartApp.hideTestCleaningPage();
			neatoSmartApp.showSpotDefinitionAPIPage();
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
		
		gotoNotificationSettingsPage:function() {
			if (neatoSmartApp.showNotificationSettingsPage() == true) {
				neatoSmartApp.hideRobotSettingsPage();				
			}			
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
		
		hideTestCleaningShowHomePage: function() {
			neatoSmartApp.hideTestCleaningPage();
			neatoSmartApp.showUserHomepage();
		},

		hideSpotDefinitionShowTestCleaning: function() {
			neatoSmartApp.hideSpotDefinitionAPIPage();
			neatoSmartApp.showTestCleaningPage();
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
		
		hideNotificationSettingsPage: function() {
			neatoSmartApp.hideRobotNotificationSettingsPage();
			neatoSmartApp.showRobotSettingsPage();
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
			document.querySelector('#btnTurnVacuumOnOff').addEventListener('click',neatoSmartApp.turnVacuumOnOff, true);
			document.querySelector('#btnManageRobotWiFi').addEventListener('click', neatoSmartApp.showManageRobotWiFiPage, true);
			
			document.querySelector('#btnOpenClosePeerConn').addEventListener('click', neatoSmartApp.openClosePeerConn , true);
			document.querySelector('#btnOpenClosePeerConn2').addEventListener('click', neatoSmartApp.openClosePeerConn2 , true);
			document.querySelector('#btnSendStartStopCleanCommand').addEventListener('click', neatoSmartApp.startStopCleaning , true);
			document.querySelector('#btnSendStartStopCleanCommand2').addEventListener('click', neatoSmartApp.startStopCleaning2 , true);
			document.querySelector('#btnPauseCleaningCommand').addEventListener('click', neatoSmartApp.pauseCleaning , true);
			document.querySelector('#btnEnableSchedule').addEventListener('click', neatoSmartApp.enableSchedule , true);
			document.querySelector('#btnIsScheduleEnabled').addEventListener('click', neatoSmartApp.isScheduleEnabled , true);
			document.querySelector('#btnSetRobotClock').addEventListener('click', neatoSmartApp.setRobotClock , true);
			document.querySelector('#btnRefreshUI').addEventListener('click', neatoSmartApp.refreshUIByState, true);
			document.querySelector('#btnValidateUser').addEventListener('click', neatoSmartApp.isUserValidated, true);
			document.querySelector('#btnUnregisterPushMessage').addEventListener('click', neatoSmartApp.unregisterForRobotMessages, true);
			document.querySelector('#btnIsRobotOnlineVirtual').addEventListener('click', neatoSmartApp.getRobotVirtualOnlineStatus, true);
			
			var directConn = localStorage.getItem('isPeerConnection');
			if ((directConn == null) || (directConn == "false")) {
				document.querySelector('#btnOpenClosePeerConn').value = "Open Connection";
			}
			else {
				document.querySelector('#btnOpenClosePeerConn').value = "Close Connection";
			}
			
			// Initialize "Turn Vacuum On/Off" button text 
			var vacuumON = localStorage.getItem('isVacuumON');
			if (vacuumON == null) {
				vacuumON = "false";
				localStorage.setItem('isVacuumON', "false");
			}
			
			if (vacuumON == "false") {
				document.querySelector('#btnTurnVacuumOnOff').value = "Turn Robot Vacuum ON";
			}
			else {
				document.querySelector('#btnTurnVacuumOnOff').value = "Turn Robot Vacuum OFF";
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
			document.querySelector('#btnTurnVacuumOnOff').removeEventListener('click',neatoSmartApp.turnVacuumOnOff, true);
			document.querySelector('#btnManageRobotWiFi').removeEventListener('click', neatoSmartApp.showManageRobotWiFiPage, true);

			document.querySelector('#btnOpenClosePeerConn2').removeEventListener('click', neatoSmartApp.openClosePeerConn2 , true);
			document.querySelector('#btnSendStartStopCleanCommand').removeEventListener('click', neatoSmartApp.startStopCleaning , true);
			document.querySelector('#btnSendStartStopCleanCommand2').removeEventListener('click', neatoSmartApp.startStopCleaning2 , true);
			document.querySelector('#btnPauseCleaningCommand').removeEventListener('click', neatoSmartApp.pauseCleaning , true);
			document.querySelector('#btnEnableSchedule').removeEventListener('click', neatoSmartApp.enableSchedule , true);
			document.querySelector('#btnIsScheduleEnabled').removeEventListener('click', neatoSmartApp.isScheduleEnabled , true);
			document.querySelector('#btnSetRobotClock').removeEventListener('click', neatoSmartApp.setRobotClock , true);
			document.querySelector('#btnRefreshUI').removeEventListener('click', neatoSmartApp.refreshUIByState, true);
			document.querySelector('#btnValidateUser').removeEventListener('click', neatoSmartApp.isUserValidated, true);
			document.querySelector('#btnUnregisterPushMessage').removeEventListener('click', neatoSmartApp.unregisterForRobotMessages, true);
			document.querySelector('#btnIsRobotOnlineVirtual').removeEventListener('click', neatoSmartApp.getRobotVirtualOnlineStatus, true);
		},
		
		populateCleaningCategoryList: function () {
			var cleaningCategoryList = document.querySelector('#CleaningCategoryList');
			for (var i = 0; i < cleaningCategoryList.options.length; i++) {
				cleaningCategoryList.options[i] = null;
			}
			cleaningCategoryList.options[0] = new Option(CLEANING_CATEGORY_ALL_TEXT, '');
			cleaningCategoryList.options[1] = new Option(CLEANING_CATEGORY_SPOT_TEXT, '');
			cleaningCategoryList.options[2] = new Option(CLEANING_CATEGORY_MANUAL_TEXT, '');
			cleaningCategoryList.options[cleaningCategoryList.selectedIndex].selected = true;
		},

		populateCleaningModeList: function () {
			var cleaningModeList = document.querySelector('#CleaningModeList');
			for (var i = 0; i < cleaningModeList.options.length; i++) {
				cleaningModeList.options[i] = null;
			}
			cleaningModeList.options[0] = new Option(CLEANING_MODE_ECO_TEXT, '');
			cleaningModeList.options[1] = new Option(CLEANING_MODE_NORMAL_TEXT, '');
			cleaningModeList.options[cleaningModeList.selectedIndex].selected = true;
		},

		populateNavigationControlList: function () {
			var navigationControlId = document.querySelector('#NavigationControlList');
			for (var i = 0; i < navigationControlId.options.length; i++) {
				navigationControlId.options[i] = null;
			}
			navigationControlId.options[0] = new Option(NAVIGATION_CONTROL_1, '');
			navigationControlId.options[1] = new Option(NAVIGATION_CONTROL_2, '');
			navigationControlId.options[2] = new Option(NAVIGATION_CONTROL_3, '');
			navigationControlId.options[3] = new Option(NAVIGATION_CONTROL_4, '');
			navigationControlId.options[4] = new Option(NAVIGATION_CONTROL_5, '');
			navigationControlId.options[5] = new Option(NAVIGATION_CONTROL_BACK, '');
			navigationControlId.options[navigationControlId.selectedIndex].selected = true;
		},

		showTestCleaningPage: function() {
			neatoSmartApp.setResponseText(null);

			document.querySelector('#btnSendStartStopCleanCommand3').addEventListener('click', neatoSmartApp.startStopCleaning3, true);
			document.querySelector('#btnPauseResumeCleaningCommand3').addEventListener('click', neatoSmartApp.pauseResumeCleaning3, true);
			document.querySelector('#btnGoToSpotDefinitionAPIPage').addEventListener('click', neatoSmartApp.goToSpotDefinitionPage , true);
			document.querySelector('#btnNavigateRobot').addEventListener('click', neatoSmartApp.navigateRobot, true);
			
			neatoSmartApp.populateCleaningCategoryList();
			neatoSmartApp.populateCleaningModeList();
			neatoSmartApp.populateNavigationControlList();
	
			// add click handlers for the radio buttons
			var radioCtrlCleaningModifier1x = document.querySelector('input[type="radio"][id="radioCleaningModifier1x"]');
			var radioCtrlCleaningModifier2x = document.querySelector('input[type="radio"][id="radioCleaningModifier2x"]');

			radioCtrlCleaningModifier1x.addEventListener('click', neatoSmartApp.onClickCleaningModifier1x, true);
			radioCtrlCleaningModifier2x.addEventListener('click', neatoSmartApp.onClickCleaningModifier2x, true);

			// set default value
			radioCtrlCleaningModifier1x.checked = true;
			cleaningModifier = CLEANING_MODIFIER_1x;
	
			// set the page as the current page and make it visible
			CURRENT_PAGE = TEST_CLEANING_PAGE;
			document.querySelector('#cleaningAPIs').setAttribute('aria-hidden', 'false');
		},
		
		hideTestCleaningPage: function() {
			neatoSmartApp.setResponseText(null);
			
			// hide the page
			document.querySelector('#cleaningAPIs').setAttribute('aria-hidden', 'true');
			
			document.querySelector('#btnSendStartStopCleanCommand3').removeEventListener('click', neatoSmartApp.startStopCleaning3, true);
			document.querySelector('#btnPauseResumeCleaningCommand3').removeEventListener('click', neatoSmartApp.pauseResumeCleaning3, true);
			document.querySelector('#btnGoToSpotDefinitionAPIPage').removeEventListener('click', neatoSmartApp.goToSpotDefinitionPage , true);
			document.querySelector('#btnNavigateRobot').removeEventListener('click', neatoSmartApp.navigateRobot, true);

			// remove click handlers for the radio buttons
			var radioCtrlCleaningModifier1x = document.querySelector('input[type="radio"][id="radioCleaningModifier1x"]');
			var radioCtrlCleaningModifier2x = document.querySelector('input[type="radio"][id="radioCleaningModifier2x"]');

			radioCtrlCleaningModifier1x.removeEventListener('click', neatoSmartApp.onClickCleaningModifier1x, true);
			radioCtrlCleaningModifier2x.removeEventListener('click', neatoSmartApp.onClickCleaningModifier2x, true);
		},
		
		navigateRobot:function() {
			neatoSmartApp.setResponseText(null);
			
			var robotId = localStorage.getItem('robotId');
			
			if ((robotId == null) || (robotId.length == 0)) {
				alert("Please associate a Robot");
				return;
			}
			
			neatoSmartApp.showProgressBar();

			var navigationControlList = document.querySelector('#NavigationControlList');
			var navigationControlId = navigationControlList.options[navigationControlList.selectedIndex].text;						
			
			RobotPluginManager.driveRobot(robotId, navigationControlId, neatoSmartApp.driveRobotSuccess, 
					neatoSmartApp.driveRobotError);	
		},
		
		driveRobotSuccess: function(result) {
			neatoSmartApp.hideProgressBar();
			neatoSmartApp.setResponseText(result);
		},
		
		driveRobotError: function(error) {
			neatoSmartApp.setResponseText(error);
			neatoSmartApp.hideProgressBar();
		},
			

		populateSpotAreaLists: function () {
			// Fill the Area-Length list
			var spotAreaLengthList = document.querySelector('#SpotAreaLengthList');
			for (var i = 0; i < spotAreaLengthList.options.length; i++) {
				spotAreaLengthList.options[i] = null;
			}
			spotAreaLengthList.options[0] = new Option(SPOT_AREA_LENGTH_5, '');
			spotAreaLengthList.options[1] = new Option(SPOT_AREA_LENGTH_2, '');
			spotAreaLengthList.options[2] = new Option(SPOT_AREA_LENGTH_1, '');
			spotAreaLengthList.options[spotAreaLengthList.selectedIndex].selected = true;

			// Fill the Area-Height list
			var spotAreaHeightList = document.querySelector('#SpotAreaHeightList');
			for (var i = 0; i < spotAreaHeightList.options.length; i++) {
				spotAreaHeightList.options[i] = null;
			}
			spotAreaHeightList.options[0] = new Option(SPOT_AREA_HEIGHT_3, '');
			spotAreaHeightList.options[1] = new Option(SPOT_AREA_HEIGHT_4, '');
			spotAreaHeightList.options[2] = new Option(SPOT_AREA_HEIGHT_1, '');
			spotAreaHeightList.options[spotAreaHeightList.selectedIndex].selected = true;
		},
	
		showSpotDefinitionAPIPage: function() {
			neatoSmartApp.setResponseText(null);

			document.querySelector('#btnSetSpotDefinition').addEventListener('click', neatoSmartApp.setSpotDefinition, true);
			document.querySelector('#btnGetSpotDefinition').addEventListener('click', neatoSmartApp.getSpotDefinition, true);
			
			neatoSmartApp.populateSpotAreaLists();
			
			// set the page as the current page and make it visible
			CURRENT_PAGE = SPOT_DEFINITION_PAGE;	
			document.querySelector('#spotDefinitionAPIs').setAttribute('aria-hidden', 'false');
		},
		
		hideSpotDefinitionAPIPage: function() {
			neatoSmartApp.setResponseText(null);

			// hide the page
			document.querySelector('#spotDefinitionAPIs').setAttribute('aria-hidden', 'true');

			document.querySelector('#btnSetSpotDefinition').removeEventListener('click', neatoSmartApp.setSpotDefinition, true);
			document.querySelector('#btnGetSpotDefinition').removeEventListener('click', neatoSmartApp.getSpotDefinition, true);
		},
		
		onClickCleaningModifier1x:function() {
			cleaningModifier = CLEANING_MODIFIER_1x;
		},	

		onClickCleaningModifier2x:function() {
			cleaningModifier = CLEANING_MODIFIER_2x;
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
			document.querySelector('#btnNotificationSettings').addEventListener('click', neatoSmartApp.gotoNotificationSettingsPage, true);
			
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
			document.querySelector('#btnNotificationSettings').removeEventListener('click', neatoSmartApp.gotoNotificationSettingsPage, true);
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
		
		showNotificationSettingsPage: function() {
			var email = localStorage.getItem('email')
			if ((email == null) || (email.length == 0)) {
				alert("Please associate a Robot");
				return false;
			}
			
			CURRENT_PAGE = NOTIFICATION_SETTINGS_PAGE;
			document.querySelector('#notificationSettings').setAttribute('aria-hidden', 'false');	

			// get radio controls
			var radioCtrlDBNotificationON = document.querySelector('input[type="radio"][id="radioDBNotificationON"]');
			var radioCtrlDBNotificationOFF = document.querySelector('input[type="radio"][id="radioDBNotificationOFF"]');
			var radioCtrlCDNotificationON = document.querySelector('input[type="radio"][id="radioCDNotificationON"]');
			var radioCtrlCDNotificationOFF = document.querySelector('input[type="radio"][id="radioCDNotificationOFF"]');
			var radioCtrlRSNotificationON = document.querySelector('input[type="radio"][id="radioRSNotificationON"]');
			var radioCtrlRSNotificationOFF = document.querySelector('input[type="radio"][id="radioRSNotificationOFF"]');
			
			radioCtrlDBNotificationOFF.checked = true;
			radioCtrlCDNotificationOFF.checked = true;
			radioCtrlRSNotificationOFF.checked = true;
			
			localStorage.setItem('isGlobalNotificationsON', "false");
			localStorage.setItem('isDBNotificationsON', "false");							
			localStorage.setItem('isCDNotificationsON', "false");				
			localStorage.setItem('isRSNotificationsON', "false");
			
			// Add click handler
			// 1. for "Dirt Bin Notification" radio controls
			radioCtrlDBNotificationON.addEventListener('click', neatoSmartApp.onClickDBNotificationON, true);
			radioCtrlDBNotificationOFF.addEventListener('click', neatoSmartApp.onClickDBNotificationOFF, true);
			// 2. for "Cleaning Done Notification" radio controls
			radioCtrlCDNotificationON.addEventListener('click', neatoSmartApp.onClickCDNotificationON, true);
			radioCtrlCDNotificationOFF.addEventListener('click', neatoSmartApp.onClickCDNotificationOFF, true);
			// 3. for "Robot Stuck Notification" radio controls
			radioCtrlRSNotificationON.addEventListener('click', neatoSmartApp.onClickRSNotificationON, true);
			radioCtrlRSNotificationOFF.addEventListener('click', neatoSmartApp.onClickRSNotificationOFF, true);

			document.querySelector('#btnEnableGlobalNotifications').addEventListener('click', neatoSmartApp.setGlobalNotifications, true);
			
			neatoSmartApp.showProgressBar();
			
			UserPluginManager.getNotificationSettings(email, neatoSmartApp.getNotificationSettingsSuccess, 
						neatoSmartApp.getNotificationSettingsFailure);
			
			return true;
		},		
		
		getNotificationSettingsSuccess: function (result) {
			neatoSmartApp.setResponseText(result);
			neatoSmartApp.hideProgressBar();
			
			var isNotificationsON = "false";
			var isDBNotificationsON = "false";
			var	isCDNotificationsON = "false";
			var	isRSNotificationsON = "false";

			// get radio controls
			var radioCtrlDBNotificationON = document.querySelector('input[type="radio"][id="radioDBNotificationON"]');
			var radioCtrlDBNotificationOFF = document.querySelector('input[type="radio"][id="radioDBNotificationOFF"]');
			var radioCtrlCDNotificationON = document.querySelector('input[type="radio"][id="radioCDNotificationON"]');
			var radioCtrlCDNotificationOFF = document.querySelector('input[type="radio"][id="radioCDNotificationOFF"]');
			var radioCtrlRSNotificationON = document.querySelector('input[type="radio"][id="radioRSNotificationON"]');
			var radioCtrlRSNotificationOFF = document.querySelector('input[type="radio"][id="radioRSNotificationOFF"]');
	
			// Set initial state
			isNotificationsON = result['global'];			
			if (isNotificationsON) {
				document.querySelector('#btnEnableGlobalNotifications').value = "OFF Notifications";
			}
			else {				
				document.querySelector('#btnEnableGlobalNotifications').value = "ON Notifications";
			}
			
			var notificationSettings = result['notifications'];			
			
			for (i = 0; i < notificationSettings.length; i++) {			
				var notification = notificationSettings[i];
				
				var notificationId = notification['key'];
				var notificationValue = notification['value'];
				
				if (notificationId == NOTIFICATION_DIRT_BIN_FULL)
				{
					isDBNotificationsON = notificationValue;			
					if (isDBNotificationsON) {
						radioCtrlDBNotificationON.checked = true;
					}
					else {
						radioCtrlDBNotificationOFF.checked = true;
					}
				}
				
				if (notificationId == NOTIFICATION_CLEANING_DONE)
				{
					isCDNotificationsON = notificationValue;			
					if (isCDNotificationsON) {
						radioCtrlCDNotificationON.checked = true;
					}
					else {
						radioCtrlCDNotificationOFF.checked = true;	
					}
				}
				
				if (notificationId == NOTIFICATION_ROBOT_STUCK)
				{
					isRSNotificationsON = notificationValue;			
					if (isRSNotificationsON) {
						radioCtrlRSNotificationON.checked = true;
					}
					else {
						radioCtrlRSNotificationOFF.checked = true;				
					}
				}
			}
			
			localStorage.setItem('isGlobalNotificationsON', isNotificationsON);
			localStorage.setItem('isDBNotificationsON', isDBNotificationsON);							
			localStorage.setItem('isCDNotificationsON', isCDNotificationsON);				
			localStorage.setItem('isRSNotificationsON', isRSNotificationsON);
		},

		getNotificationSettingsFailure: function (error) {
			neatoSmartApp.setResponseText(error);
			neatoSmartApp.hideProgressBar();
		},
		
		hideRobotNotificationSettingsPage: function() {
			neatoSmartApp.setResponseText(null);
			document.querySelector('#notificationSettings').setAttribute('aria-hidden', 'true');
			
			document.querySelector('#btnEnableGlobalNotifications').removeEventListener('click', neatoSmartApp.setGlobalNotifications, true);
			// Remove event handlers
			// 1. for "Dirt Bin Notification" radio controls
			var radioCtrlDBNotificationON = document.querySelector('input[type="radio"][id="radioDBNotificationON"]');
			radioCtrlDBNotificationON.removeEventListener('click', neatoSmartApp.onClickDBNotificationON, true);
			
			var radioCtrlDBNotificationOFF = document.querySelector('input[type="radio"][id="radioDBNotificationOFF"]');
			radioCtrlDBNotificationOFF.removeEventListener('click', neatoSmartApp.onClickDBNotificationOFF, true);
			
			// 2. for "Cleaning Done Notification" radio controls
			var radioCtrlCDNotificationON = document.querySelector('input[type="radio"][id="radioCDNotificationON"]');
			radioCtrlCDNotificationON.removeEventListener('click', neatoSmartApp.onClickCDNotificationON, true);

			var radioCtrlCDNotificationOFF = document.querySelector('input[type="radio"][id="radioCDNotificationOFF"]');
			radioCtrlCDNotificationOFF.removeEventListener('click', neatoSmartApp.onClickCDNotificationOFF, true);

			// 3. for "Robot Stuck Notification" radio controls
			var radioCtrlRSNotificationON = document.querySelector('input[type="radio"][id="radioRSNotificationON"]');
			radioCtrlRSNotificationON.removeEventListener('click', neatoSmartApp.onClickRSNotificationON, true);

			var radioCtrlRSNotificationOFF = document.querySelector('input[type="radio"][id="radioRSNotificationOFF"]');
			radioCtrlRSNotificationOFF.removeEventListener('click', neatoSmartApp.onClickRSNotificationOFF, true);
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
		 	else if(CURRENT_PAGE == TEST_CLEANING_PAGE) {
		 		neatoSmartApp.hideTestCleaningShowHomePage();
		 	} 
		 	else if(CURRENT_PAGE == SPOT_DEFINITION_PAGE) {
		 		neatoSmartApp.hideSpotDefinitionShowTestCleaning();
		 	}
			else if (CURRENT_PAGE == MANAGE_ROBOT_WIFI_PAGE) {
				neatoSmartApp.hideManageRobotWiFiPage();				
			}
			else if (CURRENT_PAGE == NOTIFICATION_SETTINGS_PAGE) {
		 		neatoSmartApp.hideNotificationSettingsPage();
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
				
				var email = localStorage.getItem('email');
				if (email != null) {
					neatoSmartApp.showProgressBar();
					UserPluginManager.isUserValidated(email, neatoSmartApp.successUserValidation, neatoSmartApp.errorUserValidation);
				}				
				else {
					localStorage.setItem('loggedIn', 0);
					neatoSmartApp.showWelcomePage();
				}
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

		successUserValidation: function(result) {
			neatoSmartApp.setResponseText(result);
			neatoSmartApp.hideProgressBar();
			// If validation status code is -2 means logged-in email address has not been validated.
			// then display Welcome screen 
			var validationStatus = result['validation_status'];
			if (validationStatus != USER_STATUS_NOT_VALIDATED) {
				neatoSmartApp.registerForRobotMessages();
				neatoSmartApp.registerRobotNotification2();
				neatoSmartApp.showUserHomepage();
			}
			else {
				neatoSmartApp.showWelcomePage();
			}	
		},
		
		errorUserValidation: function(error) {
			neatoSmartApp.setResponseText(error);
			neatoSmartApp.hideProgressBar();
			neatoSmartApp.showWelcomePage();
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
				document.querySelector('#btnSendStartStopCleanCommand2').value = "Stop Cleaning (Deprecated)";
				
			}
			else {
				localStorage.setItem('isRobotStarted', "false");				
				document.querySelector('#btnSendStartStopCleanCommand').value = "Start Cleaning";
				document.querySelector('#btnSendStartStopCleanCommand2').value = "Start Cleaning (Deprecated)";
			}
		},
		
		refreshGetStateError: function(result) {
			neatoSmartApp.hideProgressBar();			
		}
	}
}());

document.addEventListener("deviceready", neatoSmartApp.loaded, false);
