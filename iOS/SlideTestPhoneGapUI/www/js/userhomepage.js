var ROBOT_ASSOCIATION_SUCCESS = 9001;
var ROBOT_ASSOCIATION_FAILED  = 9002;

var COMMAND_ROBOT_START = 101;
var COMMAND_ROBOT_STOP = 102;
var COMMAND_ROBOT_JABBER_DETAILS = 103;
var COMMAND_SEND_BASE = 104;

var COMMAND_SENT_SUCCESS  = 5001;
var COMMAND_SENT_FAILURE  = 5002;

var TCP_CONNECTION_STATUS_CONNECTED  = 2001;
var TCP_CONNECTION_STATUS_NOT_CONNECTED =  2002;

var XMPP_CONNECTION_STATUS_CONNECTED  = 3001;
var XMPP_CONNECTION_STATUS_NOT_CONNECTED  = 3002;


if(window.addEventListener)
{
    window.addEventListener("load", init, false);
}

function init()
{
    onUserHomeLoaded();
    
    var discoverButton = document.getElementById("btnDiscoverRobot");
    var associateButton = document.getElementById("associatebtn");
    var startCleaningButton = document.getElementById("btnStartCleaningPeer");
    var stopCleaningButton = document.getElementById("btnStopCleaningPeer");
    var connectTCPButton = document.getElementById("btnConnectTCP");
    var disconnectTCPButton = document.getElementById("btnDisconnect");
    var logoutButton = document.getElementById("btnLogout")
    var robotAtlasButton = document.getElementById("btnRobotAtlas");
    var aboutButton = document.getElementById("btnAboutScreen");
    
    discoverButton.addEventListener('click', findNearbyRobots , false);
    associateButton.addEventListener('click', associateRobot , false);
    startCleaningButton.addEventListener('click', startCleaning , false);
    stopCleaningButton.addEventListener('click', stopCleaning , false);
    connectTCPButton.addEventListener('click', connectTCP , false);
    disconnectTCPButton.addEventListener('click', disconnectTCP , false);
    logoutButton.addEventListener('click', logout , false);
    robotAtlasButton.addEventListener('click', showRobotAtlas, false);
    aboutButton.addEventListener('click', showAbout, false);
}

function showAbout()
{
    window.open("AboutScreen.html",'_self');
}

function onUserHomeLoaded()
{
    var robotId = localStorage.getItem('robotId');
    var connected  = localStorage.getItem('connectionStat');
    document.getElementById('tcpstatusText').innerHTML = connected;
    if (connected == 1) // connected
    {
        document.getElementById('spinnerImg').style.display = "none";
        document.getElementById('tcpstatusText').innerHTML = 'Connected';
        document.getElementById('connectTCPbtnDiv').style.display = "none";
        document.getElementById('disconnectTCPbtnDiv').style.display = "block";
        document.getElementById('peerDevice').innerHTML = robotId;
    }
    else
    {
        document.getElementById('disconnectTCPbtnDiv').style.display = "none";
        document.getElementById('tcpstatusText').innerHTML = 'Not connected';
        if (robotId && robotId.length != 0 && robotId != 'null')
        {
            document.getElementById('connectTCPbtnDiv').style.display = "block";
            document.getElementById('peerDevice').innerHTML = robotId;
            document.getElementById('btnConnectTCP').value = "Connect with " + robotId;
        }
        else
        {
            document.getElementById('connectTCPbtnDiv').style.display = "none";
            document.getElementById('peerDevice').innerHTML = "None";
        }
    }

}

function findNearbyRobots()
{
    document.getElementById('spinnerImg').style.display = "block";
    RobotPluginManager.discoverNearbyRobots(successfindNearbyRobots,errorfindnearbyRobots);
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
        setRobotListPage(result);
    }
}
function errorfindnearbyRobots(result)
{
    document.getElementById('spinnerImg').style.display = "none";
}

function setRobotListPage(robots)
{
    // alert("got Robots");
    $('#robotsFound').append('<p class="robotsFoundClass">Robots Found</p>');
    for (var i in robots) {
        var robot = robots[i];
        // alert(robot.robot_name+" "+robot.robotId);
        $('#robotsFound').append('<input type="button" id= "'+robot.robotId +'" value="'+ robot.robot_name+'" class="robotItemButton"></input>');
    }
    $('#robotsFound').append('<hr>');
    $('#robotsFound').append('<input type="button" id="btnGoBackToWelcome" value="Go Back" class="backButtonClass" "></input>');
    document.querySelector('#robotoverlay').setAttribute('aria-hidden', 'false');
    document.querySelector('#robotsFound').setAttribute('aria-hidden', 'false');
    $('.robotItemButton').click(function() {
                                
                                clickRobot(this.id);
                                });
    $('.backButtonClass').click(function() {
                                
                                clickedBack();
                                });
}

function clickRobot(robotId)
{
    //alert(robotId);
    var myNode = document.getElementById("robotsFound");
    while (myNode.firstChild) {
        myNode.removeChild(myNode.firstChild);
    }
    localStorage.setItem('robotId', robotId);
    document.querySelector('#robotoverlay').setAttribute('aria-hidden', 'true');
    document.querySelector('#robotsFound').setAttribute('aria-hidden', 'true');
    directConnection(robotId);
}

function clickedBack()
{
    var myNode = document.getElementById("robotsFound");
    while (myNode.firstChild) {
        myNode.removeChild(myNode.firstChild);
    }
    document.querySelector('#robotoverlay').setAttribute('aria-hidden', 'true');
    document.querySelector('#robotsFound').setAttribute('aria-hidden', 'true');
}

function directConnection(robotId)
{
    RobotPluginManager.tryDirectConnection(robotId,successDirectConnection,errorDirectConnection);
    
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
        localStorage.setItem('connectionStat', 1);
    }
    else
    {
        document.getElementById('spinnerImg').style.display = "none"; //stoping the spinner since tcp isnt connected
        var robotId = localStorage.getItem('robotId');
        document.getElementById('tcpstatusText').innerHTML = 'Not connected';
        document.getElementById('disconnectTCPbtnDiv').style.display = "none";
        document.getElementById('connectTCPbtnDiv').style.display = "block";
        document.getElementById('peerDevice').innerHTML = "None";
        if (robotId)
        {
            document.getElementById('btnConnectTCP').value = "Connect with " + robotId;
        }
        localStorage.setItem('connectionStat', 0);
    }
    
}
function errorDirectConnection(value)
{
    //if there is some error in the direct connection.if there is a error in set robot user the returning the error
    document.getElementById('disconnectTCPbtnDiv').style.display = "none";
}



function associateRobot ()
{
    var tempRobo = document.getElementById('serialNumbertxt').value;
    var associateArray = {'email':'', 'robotId':tempRobo};
    localStorage.setItem('tempRobotId', tempRobo);
    document.getElementById('spinnerImg').style.display = "block";
    UserPluginManager.associateRobot("",tempRobo,successAsssociateRobot,errorAsssociateRobot);
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
        localStorage.setItem('connectionStat', 0);
        window.open("UserHomePage.html",'_self');
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


function startCleaning()
{
    var robotId = localStorage.getItem('robotId');
    document.getElementById('spinnerImg').style.display = "block";
    RobotPluginManager.sendCommandToRobot(robotId,COMMAND_ROBOT_START,[],successStartCleaning,errorStartCleaning);
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


function stopCleaning()
{
    var robotId = localStorage.getItem('robotId');
    document.getElementById('spinnerImg').style.display = "block";
    RobotPluginManager.sendCommandToRobot(robotId,COMMAND_ROBOT_STOP,[],successStopCleaning,errorStopCleaning);
    
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

function connectTCP()
{
    var robotId = localStorage.getItem('robotId');
    document.getElementById('spinnerImg').style.display = "block";
    directConnection(robotId);
}

function successConnectTCP(value)
{
    if (value === TCP_CONNECTION_STATUS_CONNECTED)
    {
        localStorage.setItem('connectionStat', 1);
        document.getElementById('spinnerImg').style.display = "none";
        document.getElementById('tcpstatusText').innerHTML = 'Connected';
        document.getElementById('connectTCPbtnDiv').style.display = "none";
        document.getElementById('disconnectTCPbtnDiv').style.display = "block";
    }
    else
    {
        localStorage.setItem('connectionStat', 1);
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


function disconnectTCP()
{
    var robotId = localStorage.getItem('robotId');
    var commandArray = {'robotId':robotId};
    document.getElementById('spinnerImg').style.display = "block";
    RobotPluginManager.disconnectDirectConnection(robotId,successDisconnectTCP,errorDisconnectTCP);
}

function successDisconnectTCP(value)
{
    if (value === TCP_CONNECTION_STATUS_NOT_CONNECTED)
    {
         localStorage.setItem('connectionStat', 0);
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
        localStorage.setItem('connectionStat', 1);
    }
    document.getElementById('spinnerImg').style.display = "none";
}


function errorDisconnectTCP(value)
{
}

function logout()
{
    document.getElementById('spinnerImg').style.display = "block";
    UserPluginManager.logout(successLogout,errorLogout);
}

function successLogout(value)
{
    document.getElementById('spinnerImg').style.display = "none";
    localStorage.setItem('robotId', null);
    localStorage.removeItem('robotId');
    localStorage.setItem('connectionStat', null);
    localStorage.removeItem('connectionStat');
    window.open("WelcomeScreen.html",'_self');
}

function errorLogout(value)
{
    document.getElementById('spinnerImg').style.display = "none";
}


function showRobotAtlas()
{
    window.open("GetAtlas.html",'_self');
}