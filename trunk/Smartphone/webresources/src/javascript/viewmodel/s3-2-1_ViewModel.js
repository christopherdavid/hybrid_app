resourceHandler.registerFunction('s3-2-1_ViewModel.js', 's3-2-1_ViewModel', function(parent) {
    console.log('instance created for: s3-2-1_ViewModel');
    var that = this,
        mcanvas, $editPopup, geoData;
        
    this.id = 's3-2-1_ViewModel';
    this.conditions = {};
    this.room = {
        name:ko.observable(),
        color:ko.observable(),
        icon:ko.observable(),
        nogo:ko.observable()
    };
    this.floorList = ko.observableArray();
    this.isFloorListEnabled = ko.observable(false);
    this.selectedFloor = ko.observable();
    this.isArea = ko.observable(true);
    this.colors = ko.observableArray(COLORTABLE);
    this.icons = ko.observableArray(ICON);
    
    this.init = function() {
        // load the geography
        parent.communicationWrapper.exec(RobotPluginManager.getMaps, [parent.communicationWrapper.dataValues["activeRobot"].robotId], that.geographySuccess, that.geographyError);
        
        mcanvas = new MapCanvas(this);
        mcanvas.setState(mcanvas.STATES.AREA);
        mcanvas.init();
        $editPopup = $( "#editPopup" );
    }
    
    this.deinit = function() {
        mcanvas.deinit();
    }
    // register to selection change
    this.selectedFloor.subscribe(function(newValue) {
        console.log("selected floor id: " + newValue);
        if(typeof newValue != "undefined" && newValue) {
            parent.communicationWrapper.storeDataValue("selectedFloor", newValue);
            mcanvas.setGeoData(jQuery.extend(true, {}, geoData[newValue]));
        }
    });
    
    this.geographySuccess = function(result) {
        console.log("geography received: " + JSON.stringify(result));
        parent.showLoadingArea(false);
        
        geoData = {};
        
        for (var i = 0, max = result[0].mapOverlayInfo.geographies.length; i < max; i++) {
            that.floorList.push(result[0].mapOverlayInfo.geographies[i].id);
            geoData[result[0].mapOverlayInfo.geographies[i].id] = result[0].mapOverlayInfo.geographies[i];
        }
        that.isFloorListEnabled(true);
        console.log("selectedFloor " + parent.communicationWrapper["selectedFloor"])
        if(parent.communicationWrapper.dataValues["selectedFloor"] && geoData[parent.communicationWrapper.dataValues["selectedFloor"]]) {
            that.selectedFloor(parent.communicationWrapper.dataValues["selectedFloor"]);
        }
    }
    
    this.geographyError = function(error) {
        parent.showLoadingArea(false);
        alert(error.message);
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
        initial: "none",
        
        events: [
            {name:"disable", from:"single", to:"none"},
            {name:"enable", from:["none","area","multiple"], to:"single"},
            {name:"cut", from:"single", to:"area"},
            {name:"merge", from:"single", to:"multiple"}
        ],
        
        callbacks: {
            onnone: function(event, from, to) {
                that.isCutMergeVisible(true);
                that.isCutMergeEnabled(false);
                that.isEditOkEnabled(false);
            },
            
            onsingle: function(event, from, to) {
                that.labelCutMerge($.i18n.t("s3-2-1.navi.cut", { context: to }));
                that.labelEditOk($.i18n.t("s3-2-1.navi.edit", { context: to }));
                that.labelTitle($.i18n.t("s3-2-1.navi.title", { context: to }));
                that.isCutMergeVisible(true);
                that.isCutMergeEnabled(true);
                that.isEditOkEnabled(true);
            },
            
            onarea: function(event, from, to) {
                that.labelCutMerge($.i18n.t("s3-2-1.navi.cut", { context: to }));
                that.labelEditOk($.i18n.t("s3-2-1.navi.edit", { context: to }));
                that.labelTitle($.i18n.t("s3-2-1.navi.title", { context: to }));
                that.isFloorListEnabled(false);
                that.isCutMergeVisible(false);
                that.isBackVisible(false);
                that.isCancelVisible(true);
            },
            
            onleavearea: function(event, from, to) {
                that.isCancelVisible(false);
                that.isFloorListEnabled(true);
                that.isBackVisible(true);
                that.isCutMergeVisible(true);
            },
            
            onmultiple: function(event, from, to) {
                that.labelCutMerge($.i18n.t("s3-2-1.navi.cut", { context: to }));
                that.labelEditOk($.i18n.t("s3-2-1.navi.edit", { context: to }));
                that.labelTitle($.i18n.t("s3-2-1.navi.title", { context: to }));
                that.isEditOkEnabled(false);
            },
            onleavemulitple: function(event, from, to) {
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
        if(that.fsm.is("single")) {
            that.fsm.cut();
            mcanvas.setState(mcanvas.STATES.CUT);
        } else {
            console.log("merge rooms");
        };
        return true;
    }
    
    this.edit = function() {
        if(that.fsm.is("area")) {
            mcanvas.confirmSelectionArea();
            mcanvas.setState(mcanvas.STATES.AREA);
            that.fsm.enable();
        } else if(that.fsm.is("single")) {
            that.room.name(mcanvas.getSelectedArea().name);
            that.room.color(COLORTABLE[mcanvas.getSelectedArea().color]);
            that.room.icon(ICON[mcanvas.getSelectedArea().icon]);
            that.room.nogo(mcanvas.getSelectedArea().nogo);
            $editPopup.popup( "open" );
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
        $editPopup.popup( "close" );
    }
    this.popupOk = function() {
        $editPopup.popup( "close" );
        if(that.isArea()) {
            mcanvas.updateRoom(that.room);
        } else {
            mcanvas.setNoGo(that.room);
        }
    }
})
console.log('loaded file: s3-2-1_ViewModel.js');
