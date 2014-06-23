resourceHandler.registerFunction('selectCountry_ViewModel.js', function(parent) {
    console.log('instance created for: selectCountry_ViewModel');
    var that = this, myScroll,
        user = parent.communicationWrapper.getDataValue("user"),
        countriesRendered = false,
        countryOrder = $.map($.i18n.t("pattern.countryOrder").split(","), function(value){
            return value;
        });
    
    this.conditions = {};
    this.countryScreenTitle = ko.observable($.i18n.t("createAccount.page.country"));
    this.robot = parent.communicationWrapper.getDataValue("selectedRobot");
    this.selectedCountry = ko.observable();
    this.countries = ko.observableArray([]);
    this.leftIcon = ko.observable(dataImage.BACK);
    this.isRobotVisible = null;
    
    this.initComputed = function() {
        that.isRobotVisible = ko.computed(function(){
            return (typeof(that.robot().robotName) != 'undefined');
        },this);
    };
    
    this.init = function() {
        console.log("init selectCountry_ViewModel");
        // init scroll container
        myScroll = new iScroll("countryWrapper",{
            hScrollbar : false,
            vScrollbar : false,
            bounce : true,
            vScroll : true,
            hScroll : true,
            momentum : true
        });
        
        $(document).one("pageshow.country", function(e) {
            myScroll.refresh();
        });
        
        // fill country list
        for (var i = 0; i < countryOrder.length; i++) {
            that.countries.push({
                "label" : $.i18n.t("common.countries." + countryOrder[i]),
                "value" : countryOrder[i]
            });
        }
        
        if(isDefined(that.bundle, "state") && that.bundle.state == pageState.CHANGE) {
            that.leftIcon(dataImage.CANCEL);
            that.countryScreenTitle($.i18n.t("userSettings.page.country"));
        }
        
        // get country code of language string e.g. 'de-DE' -> 'DE,'en-GB' -> 'GB'
        var appCountry = null;
        if(isDefined(user, "extra_param.countryCode")) {
            appCountry = user.extra_param.countryCode;
        } else {
            appCountry = parent.language().split("-")[1];
        }
        
        // check if country has already been selected (back button in workflow was pressed)
        if(typeof that.selectedCountry() == 'undefined') {
            // check if appCountry is a selectable country otherwise select other
            if($.inArray(appCountry, countryOrder) != -1) {
                that.selectedCountry(appCountry);
            } else {
                that.selectedCountry("0");
            }
        }
    };
    
    this.deinit = function() {
        myScroll.destroy();
        that.countries([]);
        that.isRobotVisible.dispose();
        countriesRendered = false;
    };
    
    this.reload = function() {
        that.initComputed();
    };
    
    this.renderedCountries = function(element, data) {
        if(!countriesRendered) {
            // check if selected country is complete and control could be initialized
            if($(element).parent().children().length == that.countries().length) {
                $("#countrySelectionList").controlgroup();
                $("#countrySelectionList").attr("data-role", "controlgroup");
                $("input[type='radio']",element.parent).checkboxradio();
                $("#countrySelectionListContainer").data("init", true );
                countriesRendered = true;
            }
        }
    };

    this.next = function() {
        event.stopPropagation();
        that.conditions["valid"] = true;
        parent.flowNavigator.next({
            "country": that.selectedCountry(),
            "state": that.bundle.state
        });
    };
    
    this.back = function() {
        parent.flowNavigator.previous();
    };
    
    that.initComputed();
});
console.log('loaded file: selectCountry_ViewModel.js');
