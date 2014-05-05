resourceHandler.registerFunction('basicScheduler_ViewModel.js', function(parent) {
    console.log('instance created for: basicScheduler_ViewModel');
    var that = this, schedulerModified = false, subscribeOnline;
    var weekIndex = $.map($.i18n.t("pattern.week").split(","), function(value){
        return +value;
        });
    
    this.conditions = {};
    this.backConditions = {};
    this.robot = parent.communicationWrapper.getDataValue("selectedRobot");
    this.scheduler = 'undefined';
    this.blockedDays = [];
    this.useSchedule = ko.observable();
    this.isScheduleEnabled = ko.observable("true");
    var initScheduleCheckDone = false;
    this.eventsAddable = ko.observable("false");
    this.isScheduleOnline = ko.observable("true");
    isTimeSet = true;
    
    this.addEnabled = ko.computed(function() {
        return that.isScheduleEnabled() && that.eventsAddable() && that.isScheduleOnline();
    }, this);
    
    // listener when robot online status changed    
    subscribeOnline = this.robot().visualOnline.subscribe(function(newValue) {
            if(isTimeSet) {
                if(!newValue) {
                    // disable scheduler
                    that.scheduler.offline();
                    that.isScheduleOnline(false);
                } else {
                    // enable scheduler
                    that.scheduler.online();
                    that.isScheduleOnline(true);
            }
            }
        }, this);

    this.init = function() {
        // check if country is italy. if so change product logo
        var user = parent.communicationWrapper.getDataValue("user");
        var uCountryCode = (user.extra_param && user.extra_param.countryCode) ? user.extra_param.countryCode : null;
        if(uCountryCode != null && uCountryCode == "IT") {
            $(document).one("pageshow.menuPopup", function(e) {
                $("#menuPopupLogo").addClass("folletto");
            });
        }
        
        // added visual online state as isEnabled parameter for scheduler 
        that.scheduler = new Scheduler($('#schedulerTarget'), 0, that.robot().visualOnline());
        $('#schedulerTarget').on('updatedEvent', that.updateEvent);
        $('#schedulerTarget').on('newEvent', that.newEvent);
        $('#schedulerTarget').on('selectEvent', that.selectEvent);
        that.isScheduleOnline(that.robot().visualOnline());
        
        if(that.robot().clockIsSet() == 1) {
            // check if scheduler is enabled
            var tDeffer = parent.communicationWrapper.exec(RobotPluginManager.isScheduleEnabled, [that.robot().robotId(), SCHEDULE_TYPE_BASIC]);
            tDeffer.done(that.isScheduleEnabledSuccess);
            
             // register to schedule changed event
            parent.notification.registerStatus(ROBOT_SCHEDULE_STATE_CHANGED, function(scheduleState) {
                initScheduleCheckDone = false;
                that.isScheduleEnabledSuccess({isScheduleEnabled:scheduleState});
            });
            
            //register to schedule updated event
            parent.notification.registerStatus(ROBOT_SCHEDULE_UPDATED, function(scheduleState) {
                if(that.isScheduleEnabled()) {
                    that.loadScheduler();
                }
            });
        } else {
            // set scheduler offline
            isTimeSet = false;
            that.scheduler.offline();
            that.isScheduleOnline(false);
            
            parent.notification.showDialog(dialogType.WARNING, $.i18n.t('messages.no_clock.title'), $.i18n.t('messages.no_clock.message'));
        }
        
    };

    this.reload = function() {
        // remove conditions
        that.conditions = {};
        that.blockedDays.length = 0;
    };

    // viewmodel deinit, destroy objects and remove event listener
    this.deinit = function() {
        $('#schedulerTarget').off('updatedEvent');
        that.scheduler.destroy();
        subscribeOnline.dispose();
        that.addEnabled.dispose();
    };
    
    this.isScheduleEnabledSuccess = function(result) {
        console.log("isScheduleEnabledSuccess\n" +JSON.stringify(result));
        that.useSchedule(result.isScheduleEnabled == true ? 'on' : 'off');
        that.isScheduleEnabled(result.isScheduleEnabled);
        initScheduleCheckDone = true;
        // enabled load data
        if(result.isScheduleEnabled) {
            that.loadScheduler();
        }
    };
    
    this.useSchedule.subscribe(function(newValue) {
        var onoff = newValue == 'on' ? true : false;
        console.log("useSchedule: " + onoff + " that.isScheduleEnabled() " + that.isScheduleEnabled());
        if(initScheduleCheckDone && that.isScheduleEnabled() != newValue) {
            //console.log("useSchedule=" + onoff);
            that.isScheduleEnabled(onoff);
            var tDeffer = parent.communicationWrapper.exec(RobotPluginManager.enableSchedule, [that.robot().robotId(), SCHEDULE_TYPE_BASIC, onoff]);
            // enabled load data
            if(onoff) {
                that.loadScheduler();
            }
        }
    });
    
   
    this.loadScheduler = function() {
        // clear all event data
        that.blockedDays.length = 0;
        that.scheduler.clear();
        console.log("getScheduleEvents for robot with id: " + that.robot().robotId());
        //RobotPluginManager.getScheduleEvents(robotId, scheduleType, callbackSuccess, callbackError)
        var tDeffer = parent.communicationWrapper.exec(RobotPluginManager.getScheduleEvents, [that.robot().robotId(), 0]);
        tDeffer.done(that.getScheduleEventsSuccess);
        tDeffer.fail(that.getScheduleEventsError);
    };
    
    // callbacks
    this.getScheduleEventsSuccess = function(result) {
        console.log("getScheduleEventsSuccess:\n" +JSON.stringify(result));
        parent.communicationWrapper.setDataValue("scheduleId", result.scheduleId);
        // catch result with an empty scheduleId
        if(result.scheduleId == "") {
            var tDeffer = parent.communicationWrapper.exec(RobotPluginManager.createSchedule, [that.robot().robotId(), 0]);
            tDeffer.done(that.createScheduleEventsSuccess);
            tDeffer.fail(that.createScheduleEventsError);
        } else {
            if (result.scheduleEventLists.length >= 0 && result.scheduleEventLists.length < weekIndex.length) {
                that.eventsAddable(true);
            }
            
            if(result.scheduleEventLists.length > 0) {
                var aDeffer = [];
                // load event data
                for (var i = 0, max = result.scheduleEventLists.length; i < max; i++) {
                    console.log("loop through schedule events");
                    //RobotPluginManager.getScheduleEventData(scheduleId, scheduleEventData, callbackSuccess, callbackError)
                    var tempDeferred = parent.communicationWrapper.exec(RobotPluginManager.getScheduleEventData, [result.scheduleId, result.scheduleEventLists[i]], 
                        { type: notificationType.SPINNER, message: "" , bHide: false });
                    
                    tempDeferred.done(that.loadScheduleDataSuccess);
                    tempDeferred.fail(that.loadScheduleEventsError);
                    
                    aDeffer.push(tempDeferred);
                }
                $.when.apply(window, aDeffer).then(function(result, notificationOptions) {
                    parent.notification.showLoadingArea(false, notificationOptions.type);
                });
            }
        }
    };
    
    this.getScheduleEventsError = function(error, notificationOptions, errorHandled) {
        // Server Error 
        if(error && error.errorCode) {
            switch (error.errorCode) {
                case ERROR_NO_SCHEDULE_FOR_GIVEN_ROBOT:
                    // create an empty scheduler
                    errorHandled.resolve();
                    var tDeffer = parent.communicationWrapper.exec(RobotPluginManager.createSchedule, [that.robot().robotId(), 0]);
                    tDeffer.done(that.createScheduleEventsSuccess);
                    tDeffer.fail(that.createScheduleEventsError);
                    break;
            }
            
        }
        console.log(error);
    };
    
    this.createScheduleEventsSuccess = function(result) {
        console.log("createScheduleEventsSuccess\n" +JSON.stringify(result));
        parent.communicationWrapper.setDataValue("scheduleId", result.scheduleId);
        $('#addButton').removeClass("ui-disabled");
    };
    
    this.createScheduleEventsError = function(error) {
         console.log(error);
    };
    
    this.loadScheduleDataSuccess = function(result) {
        console.log("loadScheduleDataSuccess\n" + JSON.stringify(result));
        // {
        // 'scheduleId': '955fe88b-061f-4cc0-9f2b-c4baa73b156a',
        // 'scheduleEventId':'76d784e0-78a2-45e0-a67a-3f404eecafc8',
        // 'scheduleEventData': {'startTime':'10:30','day':1, 'cleaningMode':'1'}
        // }
        
        // check if mode is eco or all and set eco as fallback
        if(result.scheduleEventData.cleaningMode != CLEANING_MODE_ECO && result.scheduleEventData.cleaningMode != CLEANING_MODE_NORMAL) {
            result.scheduleEventData.cleaningMode = CLEANING_MODE_ECO;
        }

        // add the day to the day blocked list
        that.blockedDays.push(result.scheduleEventData.day);
        that.scheduler.addEvent(result);
    };
    
    this.loadScheduleDataError = function(error) {
        console.log(error);
    };
    
    this.newEvent = function(event, newEvent) {
        // make sure day doesn't contain already an event
        if(that.blockedDays.indexOf(newEvent.day) == -1) {
            that.conditions['addEvent'] = true;
            var bundle = {};
            bundle.type = "add";
            bundle.blockedDays = that.blockedDays;
            bundle.newEvent = newEvent;
            parent.flowNavigator.next(bundle);
        }
    };
    
    this.selectEvent = function(event, element) {
        that.conditions['editEvent'] = true;
        var bundle = {};
        bundle.type = "edit";
        bundle.blockedDays = that.blockedDays;
        bundle.events = [element];
        parent.flowNavigator.next(bundle);
    };

    this.add = function() {
        that.conditions['addEvent'] = true;
        var bundle = {};
        bundle.type = "add";
        bundle.blockedDays = that.blockedDays;
        parent.flowNavigator.next(bundle);
    };

    this.del = function() {
        var events = that.scheduler.selectedEvents();
        var contextBuffer = {};
        var contextBufferAsString = "";
        
        // create text for each event and store it in buffer localization dependent (pattern.week)
        $.each(events, function(index, item) {
            var sContext = "";
            var localTime = localizeTime(item.scheduleEventData.startTime);
            sContext += $.i18n.t("common.day." + item.scheduleEventData.day);
            sContext += " " + localTime.time + " " + localTime.marker + ",";
            sContext += $.i18n.t("common.cleaningMode." + keyString[item.scheduleEventData.cleaningMode]);
            contextBuffer[jQuery.inArray((item.scheduleEventData.day), weekIndex)] = sContext;
        });
        
        // loop through buffer an create a single string 
        $.each(contextBuffer, function(index, item) {
            if(index > 0) {
                contextBufferAsString += "<br>";
            }
            contextBufferAsString += item;
        });
        // show delete warning message 
        parent.notification.showDialog(dialogType.WARNING,$.i18n.t('dialogs.EVENT_DELETE.title'), $.i18n.t('dialogs.EVENT_DELETE.message') +"</br>"+ contextBufferAsString, 
            [{label:$.i18n.t('dialogs.EVENT_DELETE.button_1'), callback:that.commitDel}, {label:$.i18n.t('dialogs.EVENT_DELETE.button_2')}]); 
    };
    
    this.commitDel = function() {
        var events = that.scheduler.selectedEvents();
        var aDeffer = [];
        parent.notification.closeDialog();

        $.each(events, function(index, item) {
            //RobotPluginManager.deleteScheduleEvent(scheduleId, scheduleEventId, callbackSuccess, callbackError)
            var tempDeferred = parent.communicationWrapper.exec(RobotPluginManager.deleteScheduleEvent, [parent.communicationWrapper.dataValues["scheduleId"], item.scheduleEventId], 
                { type: notificationType.SPINNER, message: "" , bHide: false });
            console.log("deleteScheduleEvent: " + item.scheduleEventId);
            
            tempDeferred.done(function() {
                that.scheduler.deleteEvent(item);
                
                if(that.blockedDays.indexOf(item.scheduleEventData.day) != -1) {
                    that.blockedDays = jQuery.grep(that.blockedDays, function(value) {
                      return value != item.scheduleEventData.day;
                    });
                }
            });
            
            aDeffer.push(tempDeferred);
        });
        $.when.apply(window, aDeffer).then(function(result, notificationOptions) {
            console.log("done deleting all selected events.");
            parent.notification.showLoadingArea(false, notificationType.SPINNER);
            that.updateSchedule(null);
        });
    };
    
    this.updateSchedule = function(element) {
        var sUpdate = $.i18n.t('communication.update_scheduler');
        
        //RobotPluginManager.updateSchedule(scheduleId, callbackSuccess, callbackError)
        var tDeffer = parent.communicationWrapper.exec(RobotPluginManager.updateSchedule, [parent.communicationWrapper.dataValues["scheduleId"]],
            { type: notificationType.OPERATION, message: sUpdate}
        );
        tDeffer.done(function(result) {that.updateScheduleSuccess(result, element); });
        tDeffer.fail(that.updateScheduleError);
    };
    
    this.updateScheduleSuccess = function(result, element) {
        console.log("updateScheduleSuccess\n" + JSON.stringify(result));
        if(element != null) {
            that.scheduler.updatedEvent(element);
        }
    };
    
    this.updateScheduleError = function(error) {
        console.log("updateScheduleError\n" + JSON.stringify(error));
    };
    
    this.updateEvent = function(event, element) {
        //console.log($(element));
        var item = $(element).data('reference');
        console.log(JSON.stringify(item));
        var tempDeferred = parent.communicationWrapper.exec(RobotPluginManager.updateScheduleEvent, [parent.communicationWrapper.dataValues["scheduleId"],item.scheduleEventId, { startTime:item.scheduleEventData.startTime,day:item.scheduleEventData.day, cleaningMode:item.scheduleEventData.cleaningMode}], 
            { type: notificationType.NONE, message: ""});
        
        tempDeferred.done(function(result) {that.updateScheduleEventSuccess(result, element); });
        tempDeferred.fail(that.updateScheduleEventError);
    };
    
    this.updateScheduleEventSuccess = function(result, element) {
        console.log("updateScheduleEventSuccess\n" + JSON.stringify(result));
        this.updateSchedule(element);
    };
    
    this.updateScheduleEventError = function(error) {
        console.log("updateScheduleEventError\n" + JSON.stringify(error));
    };
    
    /* </actionbar functions> */
   
    // navigation menu and menu actions
    this.showMenu = function() {
        parent.notification.showDomDialog("#menuPopup", true);
    };
    
    this.cleaning = function() {
        that.conditions['cleaning'] = true;
        parent.flowNavigator.next();
    };
    
    this.schedule = function() {
        $("#menuPopup").popup("close");
    };
    
    this.settings = function() {
        // switch to settings workflow
        that.conditions['settings'] = true;
        parent.flowNavigator.next();
    };
});
console.log('loaded file: basicScheduler_ViewModel.js');
