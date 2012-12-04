

/*
 * Initialisation of the plugins  
 */

var neatoSmartApp = (function() {

	
	return {



		// CLick listeners with success and error callbacks.
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

		discoverRobotSuccess: function(result) {
		//	alert(result);
			document.querySelector('#displayResult').innerHTML = result;
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
			} else if(!isRobotStarted) {
				PluginManager.startRobot(robotSerialId, true, neatoSmartApp.startStopRobotSuccess, neatoSmartApp.startStopRobotError);
				localStorage.setItem('isRobotStarted', "true");
				// set the text as Start
				document.querySelector('#btnStartStopCleaningServer').value = "Stop Cleaning";
			}
		},
		
		startStopCleaningPeer: function() {
			//TODO: Sending junk serial id for now. Later this serial id should be the serial id of the robot 	
			var robotSerialId = 0;
			if(isRobotStarted) {
				PluginManager.stopRobot(robotSerialId, false, neatoSmartApp.startStopRobotSuccess, neatoSmartApp.startStopRobotError);
				isRobotStarted = false;
			} else if(!isRobotStarted) {
				PluginManager.startRobot(robotSerialId, false, neatoSmartApp.startStopRobotSuccess, neatoSmartApp.startStopRobotError);
				isRobotStarted = true;
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
			document.querySelector('#registerUserPage').setAttribute('aria-hidden', 'false');
			document.querySelector('#registerUserButton').addEventListener('click', neatoSmartApp.register , true);
		},

		hideRegisterPage: function() {
			document.querySelector('#registerUserPage').setAttribute('aria-hidden', 'true');
			document.querySelector('#registerUserButton').removeEventListener('click', neatoSmartApp.register, true);
		},


		showUserHomepage: function() {
			
			document.querySelector('#userHomePage').setAttribute('aria-hidden', 'false');
			document.querySelector('#btnDiscoverRobot').addEventListener('click', neatoSmartApp.discoverRobots , true);
			document.querySelector('#btnGoAssociatePage').addEventListener('click', neatoSmartApp.goAssociateRobotPage , true);
			document.querySelector('#btnStartStopCleaningServer').addEventListener('click', neatoSmartApp.startStopCleaningServer , true);
			document.querySelector('#btnStartStopCleaningPeer').addEventListener('click', neatoSmartApp.startStopCleaningPeer , true);
			document.querySelector('#btnLogout').addEventListener('click', neatoSmartApp.logoutUser , true);
		},


		hideUserHomePage: function() {
			document.querySelector('#userHomePage').setAttribute('aria-hidden', 'true');
			document.querySelector('#btnDiscoverRobot').removeEventListener('click', neatoSmartApp.discoverRobots, true);
			document.querySelector('#btnGoAssociatePage').removeEventListener('click', neatoSmartApp.goAssociateRobotPage , true);
			document.querySelector('#btnStartStopCleaningServer').removeEventListener('click', neatoSmartApp.startStopCleaningServer , true);
			document.querySelector('#btnStartStopCleaningPeer').removeEventListener('click', neatoSmartApp.startStopCleaningPeer , true);
			document.querySelector('#btnLogout').removeEventListener('click', neatoSmartApp.logoutUser , true);
			
		},
		
		hideUserShowWelcome: function() {
			neatoSmartApp.hideUserHomePage();
			neatoSmartApp.showWelcomePage();
		},
		goAssociateRobotPage: function() {
			neatoSmartApp.hideUserHomePage();
			neatoSmartApp.showAssociateRobotPage();
		},

		hideRegisterShowHomePage: function() {
			neatoSmartApp.hideRegisterPage();
			neatoSmartApp.showUserHomepage();

		}, 
		showAssociateRobotPage: function() {
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
			e.preventDefault();
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
			} else if(!isRobotStarted) {
				// set the text as Start
				document.querySelector('#btnStartStopCleaningServer').value = "Start Cleaning";
			}
			
		},

		isLoggedIn: function() {
			return localStorage.getItem('loggedIn');
		}

	}
}());

document.addEventListener("deviceready", neatoSmartApp.loaded, false);
document.addEventListener("backbutton", neatoSmartApp.backButtonPressed, false);
document.addEventListener("menubutton", neatoSmartApp.menuButtonPressed, false);