resourceHandler.registerFunction('s3-2-1_ViewModel.js', function(parent) {
    console.log('instance created for: s3-2-1_ViewModel');
    var that = this, mcanvas, $editPopup, geoData, gridImage;

    this.conditions = {};
    this.room = {
        name : ko.observable(),
        color : ko.observable(),
        icon : ko.observable(),
        nogo : ko.observable()
    };
    this.floorList = ko.observableArray();
    this.isFloorListEnabled = ko.observable(false);
    this.selectedFloor = ko.observable();
    this.selectedRooms = [];
    this.isArea = ko.observable(true);
    this.colors = ko.observableArray(COLORTABLE);
    this.icons = ko.observableArray(ICON);

    this.init = function() {
        mcanvas = new MapCanvas(this);
        mcanvas.setState(mcanvas.STATES.AREA);
        mcanvas.init("canvasView");
        $editPopup = $("#editPopup");

        //$('#canvasView').on("areaSelected", function(event, rooms) {
        $(mcanvas).on("areaSelected", function(event, rooms) {
            console.log("event triggered " + event + " rooms " + rooms);
            if (rooms.length == 0) {
                that.fsm.disable();
            } else if (rooms.length == 1) {
                that.fsm.enable();
            } else if (rooms.length > 1) {
                //TODO: check if rooms were neighbors
                if (!that.fsm.is("multiple")) {
                    that.fsm.merge();
                }
            }
            that.selectedRooms = rooms;
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
        $('#canvasView').off("areaSelected");
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
    // text for labels: cut|merge, edit|cancel
    this.labelCutMerge = ko.observable($.i18n.t("s3-2-1.navi.cut"));
    this.labelEditOk = ko.observable($.i18n.t("s3-2-1.navi.edit"));
    this.labelTitle = ko.observable($.i18n.t("s3-2-1.navi.title"));

    this.isCutMergeEnabled = ko.observable(false);
    this.isCutMergeVisible = ko.observable(true);
    this.isEditOkEnabled = ko.observable(false);

    this.isBackVisible = ko.observable(true);
    this.isCancelVisible = ko.observable(false);

    /**
     * finiteStateMachine
     * states:
     * - none: no room selected
     * - single: one room selected
     * - multiple: two or more rooms selected
     * - area: selection area visible (cut an area)
     */
    this.fsm = StateMachine.create({
        initial : "none",

        events : [{
            name : "disable",
            from : "single",
            to : "none"
        }, {
            name : "enable",
            from : ["none", "area", "multiple"],
            to : "single"
        }, {
            name : "cut",
            from : "single",
            to : "area"
        }, {
            name : "merge",
            from : "single",
            to : "multiple"
        }],

        callbacks : {
            onnone : function(event, from, to) {
                that.isCutMergeVisible(true);
                that.isCutMergeEnabled(false);
                that.isEditOkEnabled(false);
            },

            onsingle : function(event, from, to) {
                that.labelCutMerge($.i18n.t("s3-2-1.navi.cut", {
                    context : to
                }));
                that.labelEditOk($.i18n.t("s3-2-1.navi.edit", {
                    context : to
                }));
                that.labelTitle($.i18n.t("s3-2-1.navi.title", {
                    context : to
                }));
                that.isCutMergeVisible(true);
                that.isCutMergeEnabled(true);
                that.isEditOkEnabled(true);
            },

            onarea : function(event, from, to) {
                that.labelCutMerge($.i18n.t("s3-2-1.navi.cut", {
                    context : to
                }));
                that.labelEditOk($.i18n.t("s3-2-1.navi.edit", {
                    context : to
                }));
                that.labelTitle($.i18n.t("s3-2-1.navi.title", {
                    context : to
                }));
                that.isFloorListEnabled(false);
                that.isCutMergeVisible(false);
                that.isBackVisible(false);
                that.isCancelVisible(true);
            },

            onleavearea : function(event, from, to) {
                that.isCancelVisible(false);
                that.isFloorListEnabled(true);
                that.isBackVisible(true);
                that.isCutMergeVisible(true);
            },

            onmultiple : function(event, from, to) {
                that.labelCutMerge($.i18n.t("s3-2-1.navi.cut", {
                    context : to
                }));
                that.labelEditOk($.i18n.t("s3-2-1.navi.edit", {
                    context : to
                }));
                that.labelTitle($.i18n.t("s3-2-1.navi.title", {
                    context : to
                }));
                that.isEditOkEnabled(false);
            },
            onleavemulitple : function(event, from, to) {
                that.isEditOkEnabled(true);
            }
        }
    });

    this.back = function() {
        that.conditions['back'] = true;
        parent.flowNavigator.previous();
    };

    this.cancel = function() {
        that.fsm.enable();
        mcanvas.setState(mcanvas.STATES.AREA);
    }

    this.cut = function() {
        if (that.fsm.is("single")) {
            that.fsm.cut();
            mcanvas.setState(mcanvas.STATES.CUT);
        } else {
            console.log("merge rooms");
        };
        return true;
    }

    this.edit = function() {
        if (that.fsm.is("area")) {
            mcanvas.confirmSelectionArea();
            mcanvas.setState(mcanvas.STATES.AREA);
            that.fsm.enable();
        } else if (that.fsm.is("single")) {
            that.room.name(that.selectedRooms[0].name);
            that.room.color(COLORTABLE[that.selectedRooms[0].color]);
            that.room.icon(ICON[that.selectedRooms[0].icon]);
            that.room.nogo("");
            $editPopup.popup("open");
        }
    }

    this.selectColor = function(data, event) {
        that.room.color(data);
    }
    this.selectCategory = function(data, event) {
        console.log("data " + data);
        that.room.icon(data);
    }
    this.selectArea = function() {
        that.isArea(true);
    }
    this.selectNoGo = function() {
        that.isArea(false);
    }
    this.zoom = function() {
        mcanvas.zoom();
    }
    this.rotate = function() {
        mcanvas.rotate();
    }

    this.popupCancel = function() {
        $editPopup.popup("close");
    }
    this.popupOk = function() {
        $editPopup.popup("close");
        if (that.isArea()) {
            // store values
            mcanvas.updateRoom(that.room);
            // update atlas data
            var tempAtlas = {"geographies":[]};
            $.each( geoData, function( key, value ) {
              tempAtlas.geographies.push(value);
            });
            console.log("robotId" + parent.communicationWrapper.dataValues["activeRobot"].robotId +"\n" +JSON.stringify(tempAtlas));
            parent.communicationWrapper.exec(RobotPluginManager.updateAtlasMetaData, [parent.communicationWrapper.dataValues["activeRobot"].robotId, tempAtlas], that.updateSuccess, that.updateError);
        } else {
            mcanvas.setNoGo(that.room);
        }
    }
    this.updateSuccess = function(result) {
        
    }
    this.updateError = function(error) {
        console.log(error.message);
    }
    
})
console.log('loaded file: s3-2-1_ViewModel.js');
