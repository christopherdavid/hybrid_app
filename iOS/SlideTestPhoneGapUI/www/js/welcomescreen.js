var DEMO_USER_EMAIL = "demo1@demo.com";
var DEMO_USER_PASSWORD = "demo123";

if(window.addEventListener)
{
    window.addEventListener("load", init, false);
}

//attaching click event listeners to button
function init()
{
    var loginButton = document.getElementById("btnGoToLogin");
    var createNewAccountButton = document.getElementById("btnGoToRegister");
    var loginAsDemoButton = document.getElementById("btnDemoLogin");
    
    loginButton.addEventListener('click', loadLoginScreen , false);
    createNewAccountButton.addEventListener('click',loadCreateUserScreen  , false);
    loginAsDemoButton.addEventListener('click', loginAsDemo , false);
}

function loadLoginScreen()
{
    window.open("LoginScreen.html",'_self');
}
function loadCreateUserScreen()
{
    window.open("CreateUserScreen.html",'_self');
}

function loginAsDemo()
{
    document.getElementById('spinnerImg').style.display = "block";
    UserPluginManager.login(DEMO_USER_EMAIL, DEMO_USER_PASSWORD, successLoginAsDemo, errorLoginAsDemo);
	
}

function successLoginAsDemo (result)
{
    document.getElementById('spinnerImg').style.display = "none";
    //alert("loginAsDemo");    // why it is hanging in this alert maybe check(in future)
    window.open("UserHomePage.html",'_self');
    
}

function errorLoginAsDemo (error)
{
    document.getElementById('spinnerImg').style.display = "none";
    //alert("ERROR loginAsDemo: \r\n"+error );
}
