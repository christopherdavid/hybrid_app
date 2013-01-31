
if(window.addEventListener)
{
    window.addEventListener("load", init, false);
}

function init()
{
    var registerUserButton = document.getElementById("registerUserButton");
    var backButton = document.getElementById("backButton");
    
    registerUserButton.addEventListener('click', registerUser , false);
    backButton.addEventListener('click', goToWelcomeScreen , false);
}

function IsEmail(email) {
    var regex = /^([a-zA-Z0-9_\.\-\+])+\@(([a-zA-Z0-9\-])+\.)+([a-zA-Z0-9]{2,4})+$/;
    return regex.test(email);
}

function registerUser()
{
    document.getElementById('spinnerImg').style.display = "block";
    var email = document.getElementById('regmailid').value;
    var password = document.getElementById('regpasskey').value;
    var name = document.getElementById('regname').value;
    var passwordconfirm = document.getElementById('regpasskeyconfirm').value;
    if (!IsEmail(email))
    {
        alert('Please enter a valid Email!');
        document.getElementById('regmailid').value = "";
        document.getElementById('spinnerImg').style.display = "none";
    }
    else if(password != passwordconfirm || password.length == 0 || passwordconfirm.length == 0)
    {
        document.getElementById('spinnerImg').style.display = "none";
        alert("Passwords do not match!");
        document.getElementById('regpasskeyconfirm').value = "";
        document.getElementById('regpasskey').value = "";
    }
    else
    {
        UserPluginManager.createUser(email,password,name,registerUserSuccess,registerUserFailure);
    }
    
}
function registerUserSuccess(value)
{
    document.getElementById('spinnerImg').style.display = "none";
    window.open("UserHomePage.html",'_self');
}
function registerUserFailure(value)
{
    //error in registering new user
    document.getElementById('spinnerImg').style.display = "none";
    document.getElementById('responseText').innerHTML = error.errorMessage;
}

function goToWelcomeScreen()
{
    window.open("WelcomeScreen.html",'_self');
}