if(window.addEventListener)
{
    window.addEventListener("load", init, false);
}

function init()
{
    onLoad();
    
     var getRobotAtlasMetadataButton = document.getElementById("btnGetRobotAtlasMetadata");
     var updateRobotAtlasMetadataButton = document.getElementById("btnUpdateRobotAtlasMetadata");
     var getGridDataButton = document.getElementById("btnGetGridData");
     var backToHomeButton = document.getElementById("btnBackToHome");
    
     getRobotAtlasMetadataButton.addEventListener('click', getAtlasMetadata , false);
     updateRobotAtlasMetadataButton.addEventListener('click', updateGridMetadata , false);
     getGridDataButton.addEventListener('click', getGridMetadata , false);
     backToHomeButton.addEventListener('click', showUserHome , false);
    
}

function onLoad() {
 
    var robotId = localStorage.getItem('robotId');
    if (robotId && robotId != null && robotId != 'null')
    {
        document.getElementById('robotAssociateStatus').innerHTML = 'Robot : ' + robotId;
    }
    else
    {
        document.getElementById('robotAssociateStatus').innerHTML = 'Not associated with any robot.';
    }
}

function getAtlasMetadata()
{
    document.getElementById('spinnerImg').style.display = "block";
    var robotId = localStorage.getItem('robotId');
    if(robotId == null)
    {
        robotId = "Robot_1001";
    }
    RobotPluginManager.getRobotAtlasMetadata(robotId, getAtlasMetadataSuccess, getAtlasMetadataError);
    
}

function getAtlasMetadataSuccess(result)
{
    document.getElementById('spinnerImg').style.display = "none";
    document.getElementById('commandStatatus').innerHTML = 'Got Atlas metadata';
    
}

function getAtlasMetadataError(result)
{
    document.getElementById('spinnerImg').style.display = "none";
    document.getElementById('commandStatatus').innerHTML = 'Failed to get Atlas metadata';
}


function updateGridMetadata(callbackSuccess,callbackError)
{
    document.getElementById('spinnerImg').style.display = "block";
    var atlasMetadata = "{\"geographies\":[{\"id\":\"floor1\",\"noGo\":[[120,30,150,45],[65,110,85,140]],\"base\":[[20,5,25,10]]}]}";
    var robotId = localStorage.getItem('robotId');
    if(robotId == null)
    {
        robotId = "Robot_1001";
    }
    RobotPluginManager.updateAtlasMetaData(robotId ,atlasMetadata, updateGridMetadataSuccess, updateGridMetadataError);
    
}

function updateGridMetadataSuccess(result)
{
    document.getElementById('spinnerImg').style.display = "none";
    document.getElementById('commandStatatus').innerHTML = 'Grid metadata updated';
}

function updateGridMetadataError(result)
{
    document.getElementById('spinnerImg').style.display = "none";
    document.getElementById('commandStatatus').innerHTML = 'Failed to update Grid metadata';
}
function getGridMetadata()
{
    document.getElementById('spinnerImg').style.display = "block";
    var robotId = localStorage.getItem('robotId');
    if(robotId == null)
    {
        robotId = "Robot_1001";
    }
    var gridId = "";
    RobotPluginManager.getAtlasGridData(robotId,gridId,getGridMetadataSuccess,getGridMetadataError);
    
}

function getGridMetadataSuccess(result)
{
    document.getElementById('spinnerImg').style.display = "none";
    document.getElementById('robotAtlasGridImage').src=result.gridData;
}

function getGridMetadataError(result)
{
    document.getElementById('spinnerImg').style.display = "none";
    document.getElementById('commandStatatus').innerHTML = 'Could not fetch grid';
}

function showUserHome()
{
    window.open("UserHomePage.html",'_self');
}
