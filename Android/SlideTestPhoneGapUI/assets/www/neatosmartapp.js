

/*
 * Initialisation of the plugins  
 */

var neatoSmartApp = (function() {
	
	var WELCOME_PAGE = 101;
	var USER_LOGIN_PAGE = 102;
	var REGISTER_USER_PAGE = 103;
	var USER_HOME_PAGE = 104;
	var ROBOT_ASSOCIATION_PAGE = 105;
	var ROBOT_SCHEDULE_PAGE = 106;
	var CURRENT_PAGE = USER_HOME_PAGE;
	return {

		// Click listeners with success and error callbacks.
		loginWithDemo: function() {
			var email = "demo1@demo.com";
			var password = "demo123";


			PluginManager.loginUser(email, password , neatoSmartApp.successLoginDemo , neatoSmartApp.errorLoginDemo);

		},

		successLoginDemo: function(result) {	 
			document.querySelector('#displayResult').innerHTML = result;
			localStorage.setItem('loggedIn', 1);
			neatoSmartApp.hideWelcomeShowHomePage();
		},

		errorLoginDemo: function(error) {
			alert("ERROR: \r\n"+error ); 
		},

		login: function() {

			var email = document.querySelector('#mailid').value;
			var password = document.querySelector('#passkey').value;			
			PluginManager.loginUser(email, password , neatoSmartApp.successLogin , neatoSmartApp.errorLogin);
		},

		successLogin: function(result) {	 
			document.querySelector('#displayResult').innerHTML = result;
			neatoSmartApp.hideLoginShowHomePage();
		},

		errorLogin: function(error) {
			alert("ERROR: \r\n"+error ); 
		},

		register: function() {
			var email = document.querySelector('#regmailid').value;
			var password = document.querySelector('#regpasskey').value;
			var name = document.querySelector('#regname').value;

			//TODO: Put validation of 2 passwords and email id
			var passwordconfirm = document.querySelector('#regpasskeyconfirm').value;


			PluginManager.registerUser (name ,email, password, neatoSmartApp.successRegister, neatoSmartApp.errorRegister);
		},

		successRegister: function(result) {
			document.querySelector('#displayResult').innerHTML = result;
			neatoSmartApp.hideRegisterShowHomePage();
		},


		errorRegister: function(error) {   
			alert("ERROR: \r\n"+error ); 
		},

		discoverRobots: function() {
			PluginManager.discoverRobot(neatoSmartApp.discoverRobotSuccess, neatoSmartApp.discoverRobotError);
		},

		
		// For now we are getting ip adsdress. Later will replaced to serial id. SHORTLY.
		discoverRobotSuccess: function(result) {
			//TODO : add these all keys in helper class.
			var jsonData = $.parseJSON(result);
			if (jsonData.notificationType == NOTIFICATION_DISCOVERY_STARTED) {
				//TODO.
			}
			else if (jsonData.notificationType == NOTIFICATION_DISCOVERY_RESULT) {
				
				var showList = false;
				for (var i in jsonData.robots) {
						showList = true;
						var robot = jsonData.robots[i];
						neatoSmartApp.addRobotToList(robot.robot_name, robot.robot_ipaddress);
					}
				if(showList) {
					neatoSmartApp.displayRobotList();
				} else {
					alert("No Neato Robots found! Try again");
				}
			}
		}, 

		addRobotToList: function(name, ipAddress) {
			 $('#robotsFound').append('<input type="button" id= "'+ipAddress +'" value="'+ name+'" class="robotItemButton"></input>');
		//	 document.querySelector('#'+serialId).addEventListener('click', neatoSmartApp.clickRobot, true);
		},
		
		clickRobot: function( ipAddress) {
			//alert(ipAddress);
			PluginManager.connectPeer(ipAddress,  neatoSmartApp.successPeerConnect, neatoSmartApp.errorPeerConnect);
			 var myNode = document.getElementById("robotsFound");
			 while (myNode.firstChild) {
			     myNode.removeChild(myNode.firstChild);
			 }
			neatoSmartApp.hideDisplayList();
		},
		successPeerConnect: function() {
			//alert("Success");
		},
		
		errorPeerConnect: function() {
			alert("Error");
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
			alert("ERROR: \r\n"+error ); 
		},

		associateRobot: function() {

			var robotSerialId = document.querySelector('#robotSerialId').value;
			PluginManager.associateRobot(robotSerialId, neatoSmartApp.associateRobotSuccess, neatoSmartApp.associateRobotError);

		},

		associateRobotSuccess: function(result) {
		//	alert(result);
		//	document.querySelector('#displayResult').innerHTML = result;
			neatoSmartApp.hideAssociateShowHome();
		},

		associateRobotError: function(error) {
			alert("ERROR: \r\n" ); 
		},

		sendToBase: function() {
			PluginManager.sendToBase(neatoSmartApp.successSendToBase, neatoSmartApp.errorSendToBase);
		},
		successSendToBase: function(result) {
			
		},
		errorSendToBase: function(error) {
			alert(error);
		},
		startStopCleaningServer: function() {
			
			//TODO: Sending junk serial id for now. Later this serial id should be the serial id of the robot 
			
			var robotSerialId = 0;
			// TODO: Temp variable. Later the robot status will be sent by the robot. Store in local storage as of now.
			var robotStarted = localStorage.getItem('isRobotStarted');
			
			if (robotStarted == null) {
				robotStarted = "false";
			}
			
			var isRobotStarted =  ((robotStarted == "true") ? true : false);
			
			if(isRobotStarted) {
				PluginManager.stopRobot(robotSerialId, true, neatoSmartApp.startStopRobotSuccess, neatoSmartApp.startStopRobotError);
				localStorage.setItem('isRobotStarted', "false");
				// set the text as Stop
				document.querySelector('#btnStartStopCleaningServer').value = "Start Cleaning";
				document.querySelector('#btnStartStopCleaningPeer').value = "Start Cleaning";

			} else if(!isRobotStarted) {
				PluginManager.startRobot(robotSerialId, true, neatoSmartApp.startStopRobotSuccess, neatoSmartApp.startStopRobotError);
				localStorage.setItem('isRobotStarted', "true");
				// set the text as Start
				document.querySelector('#btnStartStopCleaningPeer').value = "Stop Cleaning";
				document.querySelector('#btnStartStopCleaningServer').value = "Stop Cleaning";

			}
		},
		
		startStopCleaningPeer: function() {
			//TODO: Sending junk serial id for now. Later this serial id should be the serial id of the robot 	
			
			var robotSerialId = 0;
			// TODO: Temp variable. Later the robot status will be sent by the robot. Store in local storage as of now.
			var robotStarted = localStorage.getItem('isRobotStarted');
			
			if (robotStarted == null) {
				robotStarted = "false";
			}
			
			var isRobotStarted =  ((robotStarted == "true") ? true : false);
			
			if(isRobotStarted) {
				PluginManager.stopRobot(robotSerialId, false, neatoSmartApp.startStopRobotSuccess, neatoSmartApp.startStopRobotError);
				localStorage.setItem('isRobotStarted', "false");
				// set the text as Stop
				document.querySelector('#btnStartStopCleaningServer').value = "Start Cleaning";
				document.querySelector('#btnStartStopCleaningPeer').value = "Start Cleaning";

			} else if(!isRobotStarted) {
				PluginManager.startRobot(robotSerialId, false, neatoSmartApp.startStopRobotSuccess, neatoSmartApp.startStopRobotError);
				localStorage.setItem('isRobotStarted', "true");
				// set the text as Start
				document.querySelector('#btnStartStopCleaningServer').value = "Stop Cleaning";
				document.querySelector('#btnStartStopCleaningPeer').value = "Stop Cleaning";

			}
		},

		startStopRobotSuccess: function(result) {
			//alert("Success: "+result)
		},
		
		startStopRobotError: function(error) {
			alert("Error");
		},
		
		logoutUser: function() {
			// 0 indicates logout.
			localStorage.setItem('loggedIn' , 0);
			PluginManager.logoutUser(neatoSmartApp.successLogout, neatoSmartApp.errorLogout);
			
		},
		successLogout: function(result) {	 
			document.querySelector('#displayResult').innerHTML = result;
			neatoSmartApp.hideUserShowWelcome();
		},

		errorLogout: function(error) {
			alert("ERROR: \r\n"+error ); 
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
			document.querySelector('#btnSendToBase').addEventListener('click', neatoSmartApp.sendToBase, true);
			document.querySelector('#btnGoToSchedule').addEventListener('click', neatoSmartApp.goToSchedule, true);
			document.querySelector('#btnLogout').addEventListener('click', neatoSmartApp.logoutUser , true);
			
		},


		hideUserHomePage: function() {
			document.querySelector('#userHomePage').setAttribute('aria-hidden', 'true');
			document.querySelector('#btnDiscoverRobot').removeEventListener('click', neatoSmartApp.discoverRobots, true);
			document.querySelector('#btnGoAssociatePage').removeEventListener('click', neatoSmartApp.goAssociateRobotPage , true);
			document.querySelector('#btnStartStopCleaningServer').removeEventListener('click', neatoSmartApp.startStopCleaningServer , true);
			document.querySelector('#btnStartStopCleaningPeer').removeEventListener('click', neatoSmartApp.startStopCleaningPeer , true);
			document.querySelector('#btnSendToBase').removeEventListener('click', neatoSmartApp.sendToBase, true);
			document.querySelector('#btnGoToSchedule').removeEventListener('click', neatoSmartApp.goToSchedule, true);
			document.querySelector('#btnLogout').removeEventListener('click', neatoSmartApp.logoutUser , true);
			
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
		},
		
		hideScheduleShowHome: function() {
			neatoSmartApp.hideSchedulePage();
			neatoSmartApp.showUserHomepage();
		},
		
		hideSchedulePage: function() {
			document.querySelector('#scheduleRobot').setAttribute('aria-hidden', 'true');
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
			CURRENT_PAGE = ROBOT_ASSOCIATION_PAGE;
			document.querySelector('#associateRobot').setAttribute('aria-hidden', 'false');
			document.querySelector('#btnAssociateRobot').addEventListener('click', neatoSmartApp.associateRobot , true);
		},

		hideAssociateRobotPage: function() {
			document.querySelector('#associateRobot').setAttribute('aria-hidden', 'true');
			document.querySelector('#btnAssociateRobot').removeEventListener('click', neatoSmartApp.associateRobot , true);
		},

		hideAssociateShowHome: function() {
			neatoSmartApp.hideAssociateRobotPage();
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
		 	} else {
		 		 navigator.app.exitApp();
		 	}
		 	
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
				document.querySelector('#btnStartStopCleaningPeer').value = "Stop Cleaning";

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
