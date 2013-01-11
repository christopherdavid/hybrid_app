
//List of plugins.
var USER_MANAGER_PLUGIN = "UserManagerPlugin";
var ROBOT_MANAGER_PLUGIN = "RobotManagerPlugin";

var TCP_CONNECTION_STATUS_CONNECTED  = 2001;
var TCP_CONNECTION_STATUS_NOT_CONNECTED =  2002;

var XMPP_CONNECTION_STATUS_CONNECTED  = 3001;
var XMPP_CONNECTION_STATUS_NOT_CONNECTED  = 3002;

var COMMAND_SENT_SUCCESS  = 5001;
var COMMAND_SENT_FAILURE  = 5002;

var ROBOT_ASSOCIATION_SUCCESS = 9001;
var ROBOT_ASSOCIATION_FAILED  = 9002;

//List of action types of USER manager
var ACTION_TYPE_LOGIN 							= "login";
var ACTION_TYPE_LOGOUT 							= "logout";
var ACTION_TYPE_ISLOGGEDIN 						= "isLoggedIn";
var ACTION_TYPE_CREATE_USER 					= "createUser";
var ACTION_TYPE_GET_USER_DETAILS 				= "getUserDetails";
var ACTION_TYPE_ASSOCIATE_ROBOT					= "associateRobot";
var ACTION_TYPE_GET_ASSOCIATED_ROBOTS 			= "getAssociatedRobots"
var ACTION_TYPE_DISASSOCIATE_ROBOT 				= "disassociateRobot";
var ACTION_TYPE_DISASSOCAITE_ALL_ROBOTS 		= "disassociateAllRobots";

// List of actions types of Robot Manager
var ACTION_TYPE_DISCOVER_NEARBY_ROBOTS 			= "discoverNearByRobots";
var ACTION_TYPE_TRY_CONNECT_CONNECTION 			= "tryDirectConnection";
var ACTION_TYPE_SEND_COMMAND_TO_ROBOT 			= "sendCommandToRobot";
var ACIION_TYPE_SET_SCHEDULE 					= "robotSetSchedule";
var ACIION_TYPE_GET_ROBOT_SCHEDULE 				= "getSchedule";
var ACTION_TYPE_GET_ROBOT_MAP 					= "getRobotMap";
var ACTION_TYPE_SET_MAP_OVERLAY_DATA  			= "setMapOverlayData";
var ACTION_TYPE_DISCONNECT_DIRECT_CONNETION		= "disconnectDirectConnection";
//functions with their success and error callbacks

function loginUser (email, password, callbackSuccess, callbackError)
{
   // alert("neatosmartapphelper : loginUser called");
	var loginArray = {'email':email, 'password':password};

    PluginManager.callNativeFunction(callbackSuccess, callbackError, USER_MANAGER_PLUGIN, ACTION_TYPE_LOGIN, JSON.stringify(loginArray));
};

function successLogin (result)
{
    //alert("SUCCESS Login: \r\n"+result );
    //window.document.location.href = 'LoginAsDemo.html';
    window.location.assign("UserHomePage.html");
    
}

function errorLogin (error)
{
    //setResponseText(error);
    document.getElementById('spinnerImg').style.display = "none";
}

function associateRobot (callbackSuccess, callbackError)
{
    var tempRobo = document.getElementById('serialNumbertxt').value;
    var associateArray = {'email':'', 'robotId':tempRobo};
    localStorage.setItem('tempRobotId', tempRobo);
    document.getElementById('spinnerImg').style.display = "block";
    PluginManager.callNativeFunction(callbackSuccess, callbackError, USER_MANAGER_PLUGIN, ACTION_TYPE_ASSOCIATE_ROBOT, JSON.stringify(associateArray));
}

function successAsssociateRobot (result)
{
    document.getElementById('spinnerImg').style.display = "none";
    if (ROBOT_ASSOCIATION_SUCCESS === result.responseStat)
    {
        localStorage.setItem('robotId', result.robotId);
        document.getElementById('serialNumbertxt').value = "Robot Associated";
        document.getElementById('peerDevice').innerHTML = result.robotId;
        
        var robotId = localStorage.getItem('robotId');
        document.getElementById('tcpstatusText').innerHTML = 'Not connected';
        document.getElementById('disconnectTCPbtnDiv').style.display = "none";
        document.getElementById('connectTCPbtnDiv').style.display = "block";
        if (robotId)
        {
            document.getElementById('btnConnectTCP').value = "Connect with " + robotId;
        }
    }
    else
    {
        document.getElementById('peerDevice').innerHTML = 'None';
    }
}

function errorAsssociateRobot (error)
{
    document.getElementById('spinnerImg').style.display = "none";
}

function loginAsDemo (callbackSuccess, callbackError)
{
    //alert("neatosmartapphelper : loginAsDemo called");
	var loginArray = {'email':'demo1@demo.com', 'password':'demo123'};
    document.getElementById('spinnerImg').style.display = "block";
    PluginManager.callNativeFunction(callbackSuccess, callbackError, USER_MANAGER_PLUGIN, ACTION_TYPE_LOGIN, JSON.stringify(loginArray));
};

function successLoginAsDemo (result)
{
    document.getElementById('spinnerImg').style.display = "none";
    //alert("loginAsDemo");    // why it is hanging in this alert maybe check(in future)
    window.location.assign("UserHomePage.html");
    
}

function errorLoginAsDemo (error)
{
    document.getElementById('spinnerImg').style.display = "none";
    //alert("ERROR loginAsDemo: \r\n"+error );
}


function logoutUser (callbackSuccess, callbackError)
{
    PluginManager.callNativeFunction(callbackSuccess, callbackError, USER_MANAGER_PLUGIN, ACTION_TYPE_LOGOUT);
};

function successLogout (result)
{
    //alert("SUCCESS Logout: \r\n"+result );
    localStorage.setItem('robotId', null);
    localStorage.removeItem('robotId');
    window.location.assign("WelcomeScreen.html");
}

function errorLogout (error)
{
    alert("ERROR Logout: \r\n"+error );
}

function createUser (email, password, name, callbackSuccess, callbackError)
{
	var registerArray = {'email':email, 'password':password, 'username':name};
    
    PluginManager.callNativeFunction(callbackSuccess, callbackError, USER_MANAGER_PLUGIN, ACTION_TYPE_CREATE_USER, JSON.stringify(registerArray));
};

function successCreateUser(result)
{
    alert("SUCCESS CreateUser: \r\n"+result );
}

function errorCreateUser (error)
{
    alert("ERROR CreateUser: \r\n"+error );
}

function isUserLoggedIn (email, callbackSuccess, callbackError)
{
	var isUserLoggedInArray = {'email':email};
	PluginManager(callbackSuccess, callbackError, USER_MANAGER_PLUGIN,
                 ACTION_TYPE_ISLOGGEDIN, [isUserLoggedInArray]);
};

function successIsUserLoggedIn(result)
{
    alert("SUCCESS isUserLoggedIn: \r\n"+result );
}

function errorIsUserLoggedIn (error)
{
    alert("ERROR isUserLoggedIn: \r\n"+error );
}

//robotManagerPlugin methods with their success and error returns
function findNearbyRobots(callbackSuccess,callbackError)
{
    //alert("findNearbyRobots(javascript) called");
    var findRobotsArray = {};
    document.getElementById('spinnerImg').style.display = "block";
    PluginManager.callNativeFunction(callbackSuccess, callbackError, ROBOT_MANAGER_PLUGIN,ACTION_TYPE_DISCOVER_NEARBY_ROBOTS, findRobotsArray);
    
}
function successfindNearbyRobots(result)
{
    document.getElementById('spinnerImg').style.display = "none";
    if(result.length==0)
    {
         alert("No robots found. Check if the device is connected over wifi and make sure that both device and robot are on the same local network.");
    }
    else
    {
        neatoSmartApp.setRobotListPage(result);
    }
}
function errorfindnearbyRobots()
{
    document.getElementById('spinnerImg').style.display = "none";
}
function directConnection(robotId,callbackSuccess,callbackError)
{
    var connectPeerCommandArray = {'robotId':robotId};
    PluginManager.callNativeFunction(callbackSuccess, callbackError, ROBOT_MANAGER_PLUGIN,ACTION_TYPE_TRY_CONNECT_CONNECTION,JSON.stringify(connectPeerCommandArray));
    
}
function successDirectConnection(result)
{
    if (result === TCP_CONNECTION_STATUS_CONNECTED)
    {
        var robotId = localStorage.getItem('robotId');
        document.getElementById('spinnerImg').style.display = "none";
        document.getElementById('tcpstatusText').innerHTML = 'Connected';
        document.getElementById('connectTCPbtnDiv').style.display = "none";
        document.getElementById('disconnectTCPbtnDiv').style.display = "block";
        document.getElementById('peerDevice').innerHTML = robotId;
    }
    else
    {
        var robotId = localStorage.getItem('robotId');
        document.getElementById('tcpstatusText').innerHTML = 'Not connected';
        document.getElementById('disconnectTCPbtnDiv').style.display = "none";
        document.getElementById('connectTCPbtnDiv').style.display = "block";
        document.getElementById('peerDevice').innerHTML = "None";
        if (robotId)
        {
            document.getElementById('btnConnectTCP').value = "Connect with " + robotId;
        }
    }
    
}
function errorDirectConnection()
{
}


function startCleaning(callbackSuccess,callbackError)
{
    var robotId = localStorage.getItem('robotId');
    document.getElementById('spinnerImg').style.display = "block";
    var commandArray = {'robotId':robotId, 'commandId':101};
    PluginManager.callNativeFunction(callbackSuccess, callbackError, ROBOT_MANAGER_PLUGIN,ACTION_TYPE_SEND_COMMAND_TO_ROBOT,JSON.stringify(commandArray));
    
}

function successStartCleaning(value)
{
    document.getElementById('spinnerImg').style.display = "none";
    if (value == COMMAND_SENT_SUCCESS)
    {
    
    }
    else
    {
        
    }
}

function errorStartCleaning(value)
{
}

function disconnectTCP(callbackSuccess,callbackError)
{
    var robotId = localStorage.getItem('robotId');
    var commandArray = {'robotId':robotId};
    document.getElementById('spinnerImg').style.display = "block";
    PluginManager.callNativeFunction(callbackSuccess, callbackError, ROBOT_MANAGER_PLUGIN,ACTION_TYPE_DISCONNECT_DIRECT_CONNETION,JSON.stringify(commandArray));
}

function successDisconnectTCP(value)
{
    if (value === TCP_CONNECTION_STATUS_NOT_CONNECTED)
    {
        var robotId = localStorage.getItem('robotId');
        document.getElementById('tcpstatusText').innerHTML = 'Not connected';
        document.getElementById('disconnectTCPbtnDiv').style.display = "none";
        document.getElementById('connectTCPbtnDiv').style.display = "block";
        if (robotId)
        {
            document.getElementById('btnConnectTCP').value = "Connect with " + robotId;
        }
    }
    else
    {
        alert('still connected over TCP');
    }
    document.getElementById('spinnerImg').style.display = "none";
}


function errorDisconnectTCP(value)
{
}

function connectTCP(callbackSuccess,callbackError)
{
    var robotId = localStorage.getItem('robotId');
    document.getElementById('spinnerImg').style.display = "block";
    directConnection(robotId,callbackSuccess,callbackError);
}

function successConnectTCP(value)
{
    if (value === TCP_CONNECTION_STATUS_CONNECTED)
    {
        document.getElementById('spinnerImg').style.display = "none";
        document.getElementById('tcpstatusText').innerHTML = 'Connected';
        document.getElementById('connectTCPbtnDiv').style.display = "none";
        document.getElementById('disconnectTCPbtnDiv').style.display = "block";
    }
    else
    {
        var robotId = localStorage.getItem('robotId');
        document.getElementById('spinnerImg').style.display = "none";
        document.getElementById('tcpstatusText').innerHTML = 'Not connected';
        document.getElementById('disconnectTCPbtnDiv').style.display = "none";
        document.getElementById('connectTCPbtnDiv').style.display = "block";
        if (robotId)
        {
            document.getElementById('btnConnectTCP').value = "Connect with " + robotId;
        }
    }
    document.getElementById('spinnerImg').style.display = "none";
}


function errorConnectTCP(value)
{
    /*document.getElementById('spinnerImg').style.display = "none";
    var robotId = localStorage.getItem('robotId');
    document.getElementById('tcpstatusText').innerHTML = 'Not connected';
    document.getElementById('disconnectTCPbtnDiv').style.display = "none";
    document.getElementById('connectTCPbtnDiv').style.display = "block";
    if (robotId)
    {
        document.getElementById('btnConnectTCP').value = "Connect with " + robotId;
    }*/
}

function onUserHomeLoaded()
{
    var robotId = localStorage.getItem('robotId');
    if (robotId && robotId != null && robotId != 'null') {
        //alert(robotId);
        document.getElementById('peerDevice').innerHTML = robotId;
        document.getElementById('btnConnectTCP').value = "Connect with " + robotId;
        document.getElementById('connectTCPbtnDiv').style.display = "block";
        document.getElementById('disconnectTCPbtnDiv').style.display = "none";
    }
    else
    {
        document.getElementById('disconnectTCPbtnDiv').style.display = "none";
    }
}


function stopCleaning(callbackSuccess,callbackError)
{
    var robotId = localStorage.getItem('robotId');
    document.getElementById('spinnerImg').style.display = "block";
    var commandArray = {'robotId':robotId, 'commandId':102};
    PluginManager.callNativeFunction(callbackSuccess, callbackError, ROBOT_MANAGER_PLUGIN,ACTION_TYPE_SEND_COMMAND_TO_ROBOT,JSON.stringify(commandArray));
    
}

function successStopCleaning(value)
{
    document.getElementById('spinnerImg').style.display = "none";
    if (value == COMMAND_SENT_SUCCESS)
    {
        
    }
    else
    {
        
    }
}

function errorStopCleaning(value)
{

}

var neatoSmartApp = {
    
deviceReady: function ()
    {
        isUserLoggedIn();
    }
};

function isUserLoggedIn()
{
    /*var isUserLoggedInArray = {'email':'userEmail'};
    PluginManager.callNativeFunction(isLoginSuccess, isLoginError, USER_MANAGER_PLUGIN, ACTION_TYPE_ISLOGGEDIN, JSON.stringify(isUserLoggedInArray));*/
    var isUserLoggedInArray = {'email':'userEmail'};
    PluginManager.callNativeFunction(isLoginSuccess, isLoginError, USER_MANAGER_PLUGIN, ACTION_TYPE_ISLOGGEDIN, [isUserLoggedInArray]);
}

function isLoginSuccess(value)
{
    if (value === 1)
    {
        //window.location.href = "UserHomePage.html";
        window.open("UserHomePage.html",'_self');
    }
    else
    {
        //window.location.href = "WelcomeScreen.html";
        localStorage.setItem('robotId', null);
        window.open("WelcomeScreen.html",'_self');
    }
}
function isLoginError(error)
{
    document.getElementById('spinnerImg').style.display = "none";
}

function logout(callbackSuccess,callbackError)
{
    var isUserLoggedInArray;
    document.getElementById('spinnerImg').style.display = "block";
    PluginManager.callNativeFunction(isLoginSuccess, isLoginError, USER_MANAGER_PLUGIN, ACTION_TYPE_LOGOUT, [isUserLoggedInArray]);
}

function successLogout(value)
{
    document.getElementById('spinnerImg').style.display = "none";
    localStorage.setItem('robotId', null);
    localStorage.removeItem('robotId');
    window.open("WelcomeScreen.html",'_self');
}

function errorLogout(value)
{
    document.getElementById('spinnerImg').style.display = "none";
}


function setResponseText(result) {
    if (result == null) {
        document.querySelector('#responseText').innerHTML = '';
    }
    document.querySelector('#responseText').innerHTML ="Response: "+result;
}
