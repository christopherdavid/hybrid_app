
if(window.addEventListener)
{
    window.addEventListener("load", init, false);
}

function init()
{
    document.getElementById('spinnerImg').style.display = "block";
    var appVersion = localStorage.getItem('appBuild');
    var libVersion = localStorage.getItem('binaryBuild');
    var serverUsed = localStorage.getItem('serverUsed');
    
    document.getElementById('appBuild').innerHTML = appVersion;
    document.getElementById('binaryBuild').innerHTML = libVersion;
    document.getElementById('serverUsed').innerHTML = serverUsed;
    
    document.addEventListener("deviceready", onDeviceReady, false);
    var backButton = document.getElementById("btnBackButton");
    backButton.addEventListener('click', goToHome , false);

}
// Cordova is loaded and it is now safe to make calls Cordova methods
//
function onDeviceReady() {
    getAppInfo();
    //document.removeEventListener("deviceready",L,false);
}

function getAppInfo()
{
    UserPluginManager.debugGetConfigurationDetails(getDetailSuccess, getDetailFailure);
}

function getDetailSuccess(value)
{
    var appVersion  = value.appVersion;
    var libVersion  = value.libVersion;
    var serverUsed  = value.serverUsed;
    
    document.getElementById('appBuild').innerHTML = appVersion;
    document.getElementById('binaryBuild').innerHTML = libVersion;
    document.getElementById('serverUsed').innerHTML = serverUsed;
    document.getElementById('spinnerImg').style.display = "none";
    
    localStorage.setItem('appBuild', appVersion);
    localStorage.setItem('binaryBuild', libVersion);
    localStorage.setItem('serverUsed', serverUsed);
}
function getDetailFailure(value)
{
}

function goToHome()
{
    window.open("UserHomePage.html",'_self');
}