resourceHandler.registerFunction('daypicker_ViewModel.js', function(parent) {
    console.log('instance created for: daypicker_ViewModel');
    var that = this;
    var weekIndex = $.map($.i18n.t("pattern.week").split(","), function(value){
        return +value;
        });
        
    this.conditions = {};
    this.cleaningDays = ko.observableArray([]);
    this.selectedCleaningDays = ko.observableArray([]);
    this.blockedDays = ko.observableArray([]);
    
    this.init = function() {
        if (that.cleaningDays().length == 0) {
            for (var i = 0; i < weekIndex.length; i++) {
                that.cleaningDays.push({
                    "dayName" : $.i18n.t("common.day." + weekIndex[i]).substr(0, 3),
                    "id" : weekIndex[i]
                });
            }
        }
        
        var langFormat = $.i18n.t("pattern.time");
        $('.timePicker').mobiscroll().time({
            theme : 'jqm',
            display : 'inline',
            showLabel: false,
            timeWheels : langFormat,
            mode : 'mixed',
            stepMinute : 15
        });
        
        $cleaningTimePicker = $('#cleaningTime');
        
    }
    
    this.cleaningDaySelected = function(item) {
        // check if day is blocked
        if(that.blockedDays.indexOf(item.id) == -1) {
            toggleItem(item, that.selectedCleaningDays);
        }
    }
    
    function toggleItem(item, list) {
        var removedItemId = list.indexOf(item);
        // item was not in array
        if (removedItemId == -1) {
            list.push(item);
        } else {
            list.remove(item);
        }
    }
    
    this.reload = function() {
        // remove conditions
        that.conditions = {};
    };
    
    this.back = function() {
        that.conditions['back'] = true;
        parent.flowNavigator.previous();
    };
    
})
console.log('loaded file: daypicker_ViewModel.js');
