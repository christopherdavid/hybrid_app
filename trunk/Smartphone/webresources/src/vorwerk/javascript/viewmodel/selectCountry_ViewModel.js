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
    var countriesRendered = false;
    
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
        // get country code of language string e.g. 'de-DE' -> 'DE,'en-GB' -> 'GB'
        var appCountry = parent.language().split("-")[1];
        // check if appCountry is a selectable country otherwise select other
        if($.inArray(appCountry, countryOrder) != -1) {
            that.selectedCountry(appCountry);
        } else {
            // TODO: nee to be defined how 'other' could be stored on server
            console.log("select other country select GB as temporary fallback")
            that.selectedCountry("GB");
        }
    }
    
    this.renderedCountries = function(element, data) {
        console.log("renderedCountries")
        if(!countriesRendered) {
            // check if selected country is complete and control could be initialized
            if($(element).parent().children().length == that.countries().length) {
                $("#countrySelectionList").controlgroup();
                $("#countrySelectionList").attr("data-role", "controlgroup");
                $("input[type='radio']",element.parent).checkboxradio();
                $("#countrySelectionListContainer").data( "init", true );
                countriesRendered = true;
            }
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
