resourceHandler.registerFunction('s4-1-3_ViewModel.js', 's4-1-3_ViewModel', function(parent) {
    console.log('instance created for: s4-1-3_ViewModel');
    var that = this, 
        mcanvas, $editPopup, geoData, gridImage;
    
    this.id = 's4-1-3_ViewModel';
    this.conditions = {};
    
    this.isList = ko.observable(true);
    
    this.floorList = ko.observableArray();
    this.isFloorListEnabled = ko.observable(false);
    this.selectedFloor = ko.observable();
   
    
    this.rooms = ko.computed(function(){
        if(typeof that.selectedFloor() != "undefined") {
            return geoData[that.selectedFloor()].rooms;
        } else {
            return []
        }
    },this);
    this.selectedRooms = ko.observableArray([]);
    
    this.selectedMode = ko.observable();
    this.cleaningModeList = ko.observableArray([{
        "text" : "normal",
        "id" : "0"
    }, {
        "text" : "eco",
        "id" : "1"
    }]);
    this.isModeEnabled = ko.computed(function(){
        return (that.selectedRooms().length > 0);
    }, this);

    /* <enviroment functions> */
    this.init = function() {
        mcanvas = new MapCanvas(this);
        mcanvas.setState(mcanvas.STATES.AREA);
        mcanvas.init("canvasView");
        
        $(mcanvas).on("areaSelected", function(event, rooms) {
            console.log("event triggered " + event + " rooms " + rooms);
            that.selectedRooms(rooms);
        });
        
        // load the geography
        parent.communicationWrapper.exec(RobotPluginManager.getRobotAtlasMetadata, [parent.communicationWrapper.dataValues["activeRobot"].robotId], that.atlasSuccess, that.atlasError);
    }

    this.reload = function() {
        // remove conditions
        that.conditions = {};
    }

    this.deinit = function() {
    }
    /* </enviroment functions> */
   
    /* <map data functions> */
   // register to selection change
    this.selectedFloor.subscribe(function(newValue) {
        console.log("selected floor id: " + newValue);
        if(typeof newValue != "undefined" && newValue) {
            parent.communicationWrapper.storeDataValue("selectedFloor", newValue);
            //mcanvas.setGeoData(jQuery.extend(true, {}, geoData[newValue]));
            mcanvas.setGeoData(geoData[newValue]);
            if(gridImage[geoData[newValue].gridId]) {
                mcanvas.setGridImage(gridImage[geoData[newValue].gridId]);
            }
        }
    });
    
    this.atlasSuccess = function(result) {
        console.log("atlas received: " + JSON.stringify(result));
        geoData = {};
        gridImage= {};
        
        for (var i = 0, max = result[0].atlasMetadata.geographies.length; i < max; i++) {
            that.floorList.push({"text":result[0].atlasMetadata.geographies[i].name, "id":result[i].atlasMetadata.geographies[i].id});
            // TODO: temorary add gridId
            result[i].atlasMetadata.geographies[i].gridId = "86";
            geoData[result[i].atlasMetadata.geographies[i].id] = result[i].atlasMetadata.geographies[i];
            
            var fetchMap = $.i18n.t('communication.fetch_map');
            
            // load grid image
            parent.communicationWrapper.exec(RobotPluginManager.getAtlasGridData, [parent.communicationWrapper.dataValues["activeRobot"].robotId, ""], that.gridSuccess, that.gridError, {
                type : notificationType.OPERATION,
                message : fetchMap,
                callback : null
            });
        }
        //console.log("that.floorList.length " + that.floorList.length)
        // if there is just one floor or there was a previous selected floor, than select it
        if(that.floorList().length == 1) {
            that.selectedFloor(that.floorList()[0].id);
        } else if (parent.communicationWrapper.dataValues["selectedFloor"] && geoData[parent.communicationWrapper.dataValues["selectedFloor"]]) {
            that.selectedFloor(parent.communicationWrapper.dataValues["selectedFloor"]);
        }
        
        // just enable the selection list if there is more than one entry
        if(that.floorList().length > 1) {
            that.isFloorListEnabled(true);
        }
    }
    
    this.atlasError = function(error) {
        console.log(error.message);
    }
    this.gridSuccess = function(result) {
        console.log("grid received: " + JSON.stringify(result));
        //gridImage[result[0].gridId] = result[0].gridData;
        // TODO: temorary set gridId so that it maps to atlas
        gridImage["86"] = result[0].gridData;
        if(parent.communicationWrapper.dataValues["selectedFloor"] && geoData[parent.communicationWrapper.dataValues["selectedFloor"]]) {
            if(geoData[parent.communicationWrapper.dataValues["selectedFloor"]].gridId = result[0].gridId) {
                mcanvas.setGridImage(result[0].gridData);
            }
        }
    }
    this.gridError = function(error) {
        console.log(error.message);
    }
    /* </map data functions> */

    /* <actionbar functions> */
    this.back = function() {
        that.conditions['back'] = true;
        parent.flowNavigator.previous();
    };

    this.next = function() {
        that.conditions['next'] = true;
        parent.flowNavigator.next();
    };
    /* </actionbar functions> */

    this.selectList = function() {
        that.isList(true);
    }

    this.selectMap = function() {
        that.isList(false);
        mcanvas.updateRooms(that.selectedRooms());
    }
    
    this.selectRoom = function(item, event) {
        var tempIndex = that.selectedRooms.indexOf(item);
        if(tempIndex == -1) {
        	that.selectedRooms.push(item);
        } else {
        	that.selectedRooms.remove(item);
        }
        console.log("item " + JSON.stringify(item) + "\nselectedRooms " + that.selectedRooms())
    }
})
console.log('loaded file: s4-1-1_ViewModel.js'); 