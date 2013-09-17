resourceHandler.registerFunction('selectCountry_ViewModel.js', function(parent) {
    console.log('instance created for: selectCountry_ViewModel');
    var that = this, myScroll;
    this.conditions = {};
    
    this.back = function() {
        that.conditions['back'] = true;
        parent.flowNavigator.previous();
    };
    this.selectedCountry = ko.observable();
    var countryOrder = $.map($.i18n.t("pattern.countryOrder").split(","), function(value){
        return value;
        });
    this.countries = ko.observableArray([]);
    
    this.init = function() {
        // init scroll container
        myScroll = new iScroll('countryWrapper',{
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
        // get app language
        if(parent.language().indexOf("de") != -1) {
            that.selectedCountry("germany");
        } else if (parent.language().indexOf("fr") != -1) {
            that.selectedCountry("france");
        } else if (parent.language().indexOf("it") != -1) {
            that.selectedCountry("italy");
        } else {
            that.selectedCountry("greatbritain");
        }
    } 

    this.next = function() {
        that.conditions['valid'] = true;
        parent.flowNavigator.next({"country":that.selectedCountry()});
    };
    
    this.deinit = function() {
        myScroll.destroy();
    }

})
console.log('loaded file: selectCountry_ViewModel.js');
