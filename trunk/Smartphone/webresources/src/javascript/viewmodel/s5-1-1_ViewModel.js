resourceHandler.registerFunction('s5-1-1_ViewModel.js', 's5-1-1_ViewModel', function(parent) {
    console.log('instance created for: s5-1-1_ViewModel');
    var that = this, mcanvas, $editPopup, geoData, gridImage;

    this.id = 's5-1-1_ViewModel';
    this.conditions = {};

    this.floorList = ko.observableArray();
    this.isFloorListEnabled = ko.observable(false);
    this.selectedFloor = ko.observable();

    this.selectedRooms = ko.observableArray([]);
    this.selectedSpot = ko.observableArray([]);
    this.selectedType = ko.observable();
    this.cleaningType = ko.observableArray([{
        id : "Base",
        items : ["ECO"]
    }, {
        id : "Spot",
        items : ["ECO", "Boost"]
    }, {
        id : "Area",
        items : ["ECO", "Boost", "2x"]
    }, {
        id : "Flat",
        items : ["ECO", "Boost"]
    }]);
    this.selectedMode = ko.observable();
    this.cleaningMode = ko.computed(function() {
        if ( typeof that.selectedType() != "undefined") {
            return that.selectedType().items;
        } else {
            return []
        }
    }, this);
    this.isModeEnabled = ko.computed(function() {
        if (that.cleaningMode().length > 0 && that.isStartVisible()) {
            // each type could have additional dependencies
            if (that.selectedType().id == "Area") {
                return (that.selectedRooms().length > 0);
            }
            return true;
        }
        return false;
    }, this);

    this.isStartEnabled = ko.computed(function() {
        return that.isModeEnabled() && typeof that.selectedMode() != "undefined";
    }, this);
    this.isStartVisible = ko.observable(true);

    // register to selection change
    this.selectedType.subscribe(function(newValue) {
        if ( typeof newValue != "undefined" && newValue) {
            console.log("selected type: " + JSON.stringify(newValue));
            switch(newValue.id) {
                case "Base":
                    mcanvas.setState(mcanvas.STATES.GRID);
                    break;
                case "Area":
                    that.selectedRooms([]);
                    mcanvas.updateRooms([]);
                    mcanvas.setState(mcanvas.STATES.AREA);
                    break;
                case "Flat":
                    mcanvas.setState(mcanvas.STATES.GRID);
                    break;
                case "Spot":
                    mcanvas.setState(mcanvas.STATES.SPOT);
                    break;
            }
        }
    });

    this.init = function() {
        mcanvas = new MapCanvas(this);
        mcanvas.setState(mcanvas.STATES.GRID);
        mcanvas.init("canvasView");

        $(mcanvas).on("areaSelected", function(event, rooms) {
            console.log("event triggered " + event + " rooms " + rooms);
            that.selectedRooms(rooms);
        });

        // prevent the default behavior of standard touch events
        document.addEventListener('touchmove.map', function(e) {
            e.preventDefault();
        }, false);

        $('#colorScroller').makeScrollable({
            hScrollbar : false,
            vScrollbar : false,
            vScroll : false,
            hScroll : true,
        });

        $('#categoryScroller').makeScrollable({
            hScrollbar : false,
            vScrollbar : false,
            vScroll : false,
            hScroll : true,
        });
        
        // load the geography
        parent.communicationWrapper.exec(RobotPluginManager.getRobotAtlasMetadata, [parent.communicationWrapper.dataValues["activeRobot"].robotId], that.atlasSuccess, that.atlasError);
    }

    this.deinit = function() {
        mcanvas.deinit();
    }
    // register to selection change
    this.selectedFloor.subscribe(function(newValue) {
        console.log("selected floor id: " + newValue);
        if ( typeof newValue != "undefined" && newValue) {
            parent.communicationWrapper.storeDataValue("selectedFloor", newValue);
            mcanvas.setGeoData(geoData[newValue]);
            if (gridImage[geoData[newValue].gridId]) {
                mcanvas.setGridImage(gridImage[geoData[newValue].gridId]);
            }
        }
    });

    this.atlasSuccess = function(result) {
        console.log("atlas received: " + JSON.stringify(result));
        geoData = {};
        gridImage = {};

        for (var i = 0, max = result[0].atlasMetadata.geographies.length; i < max; i++) {
            that.floorList.push({
                "text" : result[0].atlasMetadata.geographies[i].name,
                "id" : result[i].atlasMetadata.geographies[i].id
            });
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
        if (that.floorList().length == 1) {
            that.selectedFloor(that.floorList()[0].id);
        } else if (parent.communicationWrapper.dataValues["selectedFloor"] && geoData[parent.communicationWrapper.dataValues["selectedFloor"]]) {
            that.selectedFloor(parent.communicationWrapper.dataValues["selectedFloor"]);
        }

        // just enable the selection list if there is more than one entry
        if (that.floorList().length > 1) {
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
        if (parent.communicationWrapper.dataValues["selectedFloor"] && geoData[parent.communicationWrapper.dataValues["selectedFloor"]]) {
            if (geoData[parent.communicationWrapper.dataValues["selectedFloor"]].gridId = result[0].gridId) {
                mcanvas.setGridImage(result[0].gridData);
            }
        }
    }
    this.gridError = function(error) {
        console.log(error.message);
    }

    this.back = function() {
        that.conditions['back'] = true;
        parent.flowNavigator.previous();
    };

    this.start = function() {
        that.isStartVisible(false);
        if (that.selectedType().id != "Base") {
            var cleaningCommand = {
                type : that.selectedType().id,
                mode : that.selectedMode(),
            };
            if (that.selectedType().id == "Area") {
                mcanvas.setState(mcanvas.STATES.MOVEAREA);
                // get rooms
                cleaningCommand.rooms = that.selectedRooms().slice(0);
            } else if (that.selectedType().id == "Spot") {
                mcanvas.setState(mcanvas.STATES.MOVESPOT);
                // get bounding box for spot
                cleaningCommand.spot = mcanvas.getSelectionArea();
            }
            console.log("send cleaning command: " + JSON.stringify(cleaningCommand));
        } else {
            // Send command that the robot should return to base
            //parent.communicationWrapper.exec(RobotPluginManager.sendCommandToRobot, [that.robot().robotId(), COMMAND_SEND_BASE, ""], that.successSendToBase, that.errorSendToBase);
        }
    }

    this.pause = function() {
        that.isStartVisible(true);
        if (that.selectedType().id == "Area") {
            mcanvas.setState(mcanvas.STATES.AREA);
        } else if (that.selectedType().id == "Spot") {
            mcanvas.setState(mcanvas.STATES.SPOT);
        }
    }

    this.zoom = function() {
        mcanvas.zoom();
    }
    this.rotate = function() {
        mcanvas.rotate();
    }
})
console.log('loaded file: s5-1-1_ViewModel.js');
