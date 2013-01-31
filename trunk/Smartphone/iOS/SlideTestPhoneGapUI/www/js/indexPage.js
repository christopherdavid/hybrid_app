if(window.addEventListener)
{
    window.addEventListener("load", onLoad, false);
}

function onLoad() {
    document.addEventListener("deviceready", onDeviceReady, false);
}

// Cordova is loaded and it is now safe to make calls Cordova methods
//
function onDeviceReady() {
    isUserLoggedIn();
}

function isUserLoggedIn() {
    UserPluginManager.isUserLoggedIn('userEmail',successIsUserLoggedIn,errorIsUserLoggedIn);
}

function successIsUserLoggedIn(value) {
    
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

function errorIsUserLoggedIn(value) {
    document.getElementById('spinnerImg').style.display = "none";
}