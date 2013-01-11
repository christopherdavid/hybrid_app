var neatoSmartApp = {
    
setRobotListPage: function (robots)
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
                                    
                                    neatoSmartApp.clickRobot(this.id);
                                    });
        $('.backButtonClass').click(function() {
                                    
                                    neatoSmartApp.clickedBack();
                                    });
    },
    
    
clickedBack: function()
    {
        var myNode = document.getElementById("robotsFound");
        while (myNode.firstChild) {
            myNode.removeChild(myNode.firstChild);
        }
        document.querySelector('#robotoverlay').setAttribute('aria-hidden', 'true');
        document.querySelector('#robotsFound').setAttribute('aria-hidden', 'true');
    },
    
clickRobot: function(robotId)
    {
        //alert(robotId);
        var myNode = document.getElementById("robotsFound");
        while (myNode.firstChild) {
            myNode.removeChild(myNode.firstChild);
        }
        localStorage.setItem('robotId', robotId);
        document.querySelector('#robotoverlay').setAttribute('aria-hidden', 'true');
        document.querySelector('#robotsFound').setAttribute('aria-hidden', 'true');
        directConnection(robotId,successDirectConnection,errorDirectConnection);
    }
    
};