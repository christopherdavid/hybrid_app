
if(window.addEventListener)
{
    window.addEventListener("load", init, false);
}

//attaching click event listeners to button
function init()
{
    var loginButton = document.getElementById("loginButton");
    var backButton = document.getElementById("backButton");
    
    loginButton.addEventListener('click', loginUser , false);
    backButton.addEventListener('click',goToWelcomeScreen  , false);
}

function loginUser ()
{
    document.getElementById('spinnerImg').style.display = "block";
    var email = document.getElementById('mailid').value;
    var password = document.getElementById('passkey').value;
    UserPluginManager.login(email, password, successLogin, errorLogin);
}

function successLogin (result)
{
    document.getElementById('spinnerImg').style.display = "none";
    document.getElementById('responseText').innerHTML = result;
    window.open("UserHomePage.html",'_self');
}

function errorLogin (error)
{
    //setResponseText(error);
    document.getElementById('responseText').innerHTML = error.errorMessage;
    document.getElementById('spinnerImg').style.display = "none";
}

function goToWelcomeScreen()
{
    window.open("WelcomeScreen.html",'_self');
}