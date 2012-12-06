resourceHandler.registerFunction('s3-2-1_ViewModel.js', 's3-2-1_ViewModel', function(parent) {
    console.log('instance created for: s3-2-1_ViewModel');
    var that = this,
        mcanvas;
        
    this.id = 's3-2-1_ViewModel';
    this.conditions = {};
    
    this.init = function() {
        mcanvas = new MapCanvas(this);
        mcanvas.init();
    }
    
    this.deinit = function() {
        mcanvas.deinit();
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
            {name:"enable", from:"none", to:"single"},
            {name:"enable", from:"area", to:"single"},
            {name:"enable", from:"multiple", to:"single"},
            {name:"cut", from:"single", to:"area"},
            {name:"merge", from:"single", to:"multiple"}
        ],
        
        callbacks: {
            onnone: function(event, from, to) {
                console.log("NONE")
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
                
                that.isCutMergeVisible(false);
                that.isBackVisible(false);
                that.isCancelVisible(true);
            },
            
            onleavearea: function(event, from, to) {
                that.isCancelVisible(false);
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
        mcanvas.enableAreaSelection(false);
    }
    
    this.cut = function() {
        if(that.fsm.is("single")) {
            that.fsm.cut();
            mcanvas.enableAreaSelection(true);
        } else {
            console.log("merge rooms");
        };
        return true;
    }
    
    this.edit = function() {
        if(that.fsm.is("area")) {
            mcanvas.confirmSelectionArea();
            that.fsm.enable();
        }
    }
    
    this.zoom = function() {
        mcanvas.zoom();
    }
    this.rotate = function() {
        mcanvas.rotate();
    }
    
})
console.log('loaded file: s3-2-1_ViewModel.js');
