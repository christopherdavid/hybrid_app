resourceHandler.registerFunction('basicSchedulerDate_ViewModel.js', function(parent) {
    console.log('instance created for: basicSchedulerDate_ViewModel');
    var that = this, $cleaningTimePicker;
    // need to parse each entry from string to an integer
    var weekIndex = $.map($.i18n.t("pattern.week").split(","), function(value){
        return +value;
        });
    var dayNameLength = parseInt($.i18n.t("pattern.dayNameLength"),10);
    this.conditions = {};

    this.cleaningDays = ko.observableArray([]);
    this.selectedCleaningDays = ko.observableArray([]);
    this.blockedDays = ko.observableArray([]);
    // set normal as default
    this.cleaningMode = ko.observable(CLEANING_MODE_NORMAL);
    this.ecoMode = ko.computed(function() {
        return that.cleaningMode() == CLEANING_MODE_ECO;
    });
    this.robot = parent.communicationWrapper.getDataValue("selectedRobot");
    this.hasDelete = ko.observable(false);
    
    this.isNextEnabled = ko.computed(function() {
        return (that.selectedCleaningDays().length > 0 && that.robot().visualOnline() && that.editingAllowed());
    }, this);
    
    this.editingAllowed = ko.observable(true);
    
    /* <enviroment functions> */
    this.init = function() {
        if (that.cleaningDays().length == 0) {
            for (var i = 0; i < weekIndex.length; i++) {
                that.cleaningDays.push({
                    "dayName" : $.i18n.t("common.day." + weekIndex[i]).substr(0, dayNameLength),
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
            mode : 'scroller',
            stepMinute : 15
        });
        
        $cleaningTimePicker = $('#cleaningTime');
        console.log(that.bundle);
        if (that.bundle) {
            if (that.bundle.blockedDays && that.bundle.blockedDays.length > 0) {
                that.blockedDays(that.bundle.blockedDays);
            }
            if (that.bundle.type == "add" && that.bundle.newEvent) {
                var startTime = $.scroller.parseDate('HH:ii', that.bundle.newEvent.startTime);
                // set event time to date picker
                $cleaningTimePicker.scroller('setDate', startTime, true);
                
                // clear cleaning days and set the new ones from bundle
                that.selectedCleaningDays.removeAll();
                var dayIndex = jQuery.inArray((that.bundle.newEvent.day), weekIndex);
                that.selectedCleaningDays.push(that.cleaningDays()[dayIndex]);
                
            } else if(that.bundle.type == "edit") {
                //show delete Button
                that.hasDelete(true);
            }
            if (that.bundle.events && that.bundle.events.length > 0) {
                var startTime = $.scroller.parseDate('HH:ii', that.bundle.events[0].scheduleEventData.startTime);
                
                // set event time to date picker
                $cleaningTimePicker.scroller('setDate', startTime, true);
                var eventMode = null;
                var eventDiffers = false;
                
                // clear cleaning days and set the new ones from bundle
                that.selectedCleaningDays.removeAll();
                
                $.each(that.bundle.events, function(index, item) {
                    var i = jQuery.inArray((item.scheduleEventData.day), weekIndex);
                    that.selectedCleaningDays.push(that.cleaningDays()[i]);
                    // check which event cleaningMode should be set:
                    // if 2 events have different modes than leave it blank 
                    // otherwise set the cleaningMode of the event                    
                    if(eventMode != null && (eventMode != item.scheduleEventData.cleaningMode) && !eventDiffers) {
                        eventDiffers = true;
                    }
                    eventMode = item.scheduleEventData.cleaningMode;
                    
                    // check if day is also on blocked list and remove it from there
                    if(that.blockedDays().length > 0) {
                        if(that.blockedDays.indexOf(item.scheduleEventData.day) != -1) {
                            that.blockedDays.remove(item.scheduleEventData.day);
                        }
                    }
                });
                if(!eventDiffers) {
                    that.cleaningMode(eventMode);
                } else {
                    // set normal as default
                    that.cleaningMode(CLEANING_MODE_NORMAL);
                }
            } else {
                // set normal as default
                that.cleaningMode(CLEANING_MODE_NORMAL);
            }
        }
        
        //receive resize and orientation change events
        $(window).on("resize.timeset", function() {
            that.updateLayout();
        });
        $(document).one("pageshow.timeset", function(e) {
            that.updateLayout();
        });
        
         // register to schedule changed event
        parent.notification.registerStatus(ROBOT_SCHEDULE_STATE_CHANGED, function(scheduleState) {
            that.editingAllowed(false);
        });
        
        //register to schedule updated event
        parent.notification.registerStatus(ROBOT_SCHEDULE_UPDATED, function(scheduleState) {
            that.editingAllowed(false);
        });
    };

    this.reload = function() {
        // remove conditions
        that.conditions = {};
    };

    // viewmodel deinit, destroy objects and remove event listener
    this.deinit = function() {
        $(window).off(".timeset");
        that.isNextEnabled.dispose();
        that.ecoMode.dispose();
    };
    /* </enviroment functions> */

    /* <actionbar functions> */
    
    this.back = function() {
        that.conditions['back'] = true;
        parent.flowNavigator.previous();
    };
    
    this.cancel = function() {
        that.conditions['cancel'] = true;
        parent.flowNavigator.previous(that.bundle);
    };

    this.next = function() {
        var startDate = $cleaningTimePicker.scroller('getDate');
        var startTime = $.scroller.formatDate('HH:ii', startDate);
        var aDeffer = [];
        
        // loop trough all selectedCleaningDays and update or create events
        ko.utils.arrayForEach(that.selectedCleaningDays(), function(item) {
            console.log("startTime:" + startTime + "day:" + item.id);
            
            // check if it is a new event or an update
            if(that.bundle && that.bundle.events && that.bundle.events.length > 0) {
                // update the current events and remove them from bundle.events
                var tempEvent = that.bundle.events.pop();
                //RobotPluginManager.updateScheduleEvent(scheduleId, scheduleEventId, scheduleEventData, callbackSuccess, callbackError) 
                
                var tempDeferred = parent.communicationWrapper.exec(RobotPluginManager.updateScheduleEvent, [parent.communicationWrapper.dataValues["scheduleId"],tempEvent.scheduleEventId, { startTime:startTime,day:item.id, cleaningMode:that.cleaningMode()}], 
                    { type: notificationType.SPINNER, message: "" , bHide: false });
                
                tempDeferred.done(that.updateScheduleEventSuccess);
                tempDeferred.fail(that.updateScheduleEventError);
                
                aDeffer.push(tempDeferred);
            } else {
                // create a new event
                var tempDeferredAdd = parent.communicationWrapper.exec(RobotPluginManager.addScheduleEvent, [parent.communicationWrapper.dataValues["scheduleId"], { startTime:startTime,day:item.id, cleaningMode:that.cleaningMode()}], 
                    { type: notificationType.SPINNER, message: "" , bHide: false });
                
                tempDeferredAdd.done(that.addScheduleEventSuccess);
                tempDeferredAdd.fail(that.addScheduleEventError);
                
                aDeffer.push(tempDeferredAdd);
            }
        });
        
        $.when.apply(window, aDeffer).then(function(result, notificationOptions) {
            console.log("all events have been created or updated");
            //parent.notification.showLoadingArea(false, notificationOptions.type);
            
            if(that.bundle && that.bundle.events && that.bundle.events.length > 0) {
                console.log("remove remaining events contained in bundle");
                var aDefferRem = [];    
                $.each(that.bundle.events, function(index, item) {
                    //RobotPluginManager.deleteScheduleEvent(scheduleId, scheduleEventId, callbackSuccess, callbackError)
                    parent.communicationWrapper.exec(RobotPluginManager.deleteScheduleEvent,[
                        parent.communicationWrapper.dataValues["scheduleId"], item.scheduleEventId],
                        that.delScheduleEventSuccess, that.delScheduleEventError);
                    
                    var tempRemDeferred = parent.communicationWrapper.exec(RobotPluginManager.deleteScheduleEvent, [parent.communicationWrapper.dataValues["scheduleId"], item.scheduleEventId], 
                        { type: notificationType.SPINNER, message: "" , bHide: false });
                    aDefferRem.push(tempRemDeferred);
                });
                
                $.when.apply(window, aDefferRem).then(function(result, notificationOptions) {
                    parent.notification.showLoadingArea(false, notificationOptions.type);
                    that.updateSchedule();
                });
            } else {
                that.updateSchedule();
            }
        });
    };
    
    this.updateSchedule = function() {
        //RobotPluginManager.updateSchedule(scheduleId, callbackSuccess, callbackError)
        var tDeffer = parent.communicationWrapper.exec(RobotPluginManager.updateSchedule, [parent.communicationWrapper.dataValues["scheduleId"]]);
        tDeffer.done(that.updateScheduleSuccess);
        tDeffer.fail(that.updateScheduleError);
    };
    
    /* callbacks */
    this.addScheduleEventSuccess = function(result) {
        console.log("addScheduleEventSuccess\n" + JSON.stringify(result));
    };
    
    this.addScheduleEventError = function(error) {
        console.log("addScheduleEventError\n" + JSON.stringify(error));
    };
    
    this.updateScheduleEventSuccess = function(result) {
        console.log("updateScheduleEventSuccess\n" + JSON.stringify(result));
    };
    
    this.updateScheduleEventError = function(error) {
        console.log("updateScheduleEventError\n" + JSON.stringify(error));
    };
    
    this.updateScheduleSuccess = function(result, notificationOptions) {
        console.log("updateScheduleSuccess\n" + JSON.stringify(result));
        parent.notification.showLoadingArea(false, notificationOptions.type);
        that.conditions['next'] = true;
        parent.flowNavigator.next();
    };
    
    this.updateScheduleError = function(error) {
        console.log("updateScheduleError\n" + JSON.stringify(error));
    };
    
    
    /* </actionbar functions> */

    this.cleaningDaySelected = function(item) {
        // check if day is blocked
        if(that.blockedDays.indexOf(item.id) == -1) {
            toggleItem(item, that.selectedCleaningDays);
        }
    };
    
    function toggleItem(item, list) {
        var removedItemId = list.indexOf(item);
        // item was not in array
        if (removedItemId == -1) {
            list.push(item);
        } else {
            list.remove(item);
        }
    }
    
    this.toggleEcoMode = function() {
        that.cleaningMode(that.cleaningMode() == CLEANING_MODE_ECO ? CLEANING_MODE_NORMAL : CLEANING_MODE_ECO);
    };
    
    this.del = function() {
        if (that.bundle.events && that.bundle.events.length > 0) {
            var item = that.bundle.events[0];
            var sContext = "";
            var localTime = localizeTime(item.scheduleEventData.startTime);
            sContext += $.i18n.t("common.day." + item.scheduleEventData.day);
            sContext += " " + localTime.time + " " + localTime.marker; 
            // To fix the issue # - 454 : remove the cleaning mode
            // + ",";
            //
            //sContext += $.i18n.t("common.cleaningMode." + keyString[(item.scheduleEventData.cleaningMode == CLEANING_MODE_ECO ? CLEANING_MODE_ECO : CLEANING_MODE_NORMAL)]);
            
            // show delete warning message 
            parent.notification.showDialog(dialogType.WARNING,$.i18n.t('dialogs.EVENT_DELETE.title'), $.i18n.t('dialogs.EVENT_DELETE.message') +"</br>"+ sContext, 
                [{label:$.i18n.t('dialogs.EVENT_DELETE.button_1'), callback:that.commitDel}, {label:$.i18n.t('dialogs.EVENT_DELETE.button_2')}]);
        } 
    };
    
    this.commitDel = function() {
        if (that.bundle.events && that.bundle.events.length > 0) {
            var item = that.bundle.events[0];
            parent.notification.closeDialog();
            
            var tDeffer = parent.communicationWrapper.exec(RobotPluginManager.deleteScheduleEvent, [parent.communicationWrapper.dataValues["scheduleId"], item.scheduleEventId], 
                { type: notificationType.SPINNER, message: "" , bHide: false });
            console.log("deleteScheduleEvent: " + item.scheduleEventId);
            
            tDeffer.done(function(){
                parent.notification.showLoadingArea(false, notificationType.SPINNER);
                that.updateSchedule();
            });
        }
    };


    this.updateLayout = function() {
        var width = $('.dayPicker').innerWidth();
        $('.dayPicker li').width(Math.floor(width / 7) - 2 + "px");
    };

});
console.log('loaded file: basicSchedulerDate_ViewModel.js'); 