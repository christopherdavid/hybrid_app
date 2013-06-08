resourceHandler.registerFunction('basicScheduler_ViewModel.js', function(parent) {
    console.log('instance created for: basicScheduler_ViewModel');
    var that = this, schedulerModified = false;
    this.conditions = {};
    this.backConditions = {};
    this.robot = parent.communicationWrapper.getDataValue("selectedRobot");
    
    this.scheduler = 'undefined';
    this.blockedDays = [];

    /* <enviroment functions> */
    this.init = function() {
        that.scheduler = new Scheduler($('#schedulerTarget'), 0);
        $('#schedulerTarget').on('updatedEvent', that.updateEvent);
        
        console.log("getScheduleEvents for robot with id: " + that.robot().robotId());
        that.loadScheduler();

        if (that.bundle) {
            if (that.bundle.events) {
                that.scheduler.addEvents(that.bundle.events);
            }
        }
        that.scheduler.selectedEvents.subscribe(function(array) {
            if (that.scheduler.selectedEvents().length > 0) {
                $('#editButton').removeClass("ui-disabled");
                $('#deleteButton').show();
                $('#addButton').hide();
            } else {
                $('#editButton').addClass("ui-disabled");
                $('#deleteButton').hide();
                $('#addButton').show();
            }
        });

    }

    this.reload = function() {
        // remove conditions
        that.conditions = {};
    }

    this.deinit = function() {
        that.scheduler.destroy();
    }
    /* </enviroment functions> */
   
   this.changeRobot = function() {
        // Switch to robot selection dialog
        that.conditions['changeRobot'] = true;
        parent.flowNavigator.next(robotScreenCaller.CHANGE);
    };

    this.loadScheduler = function() {
        //RobotPluginManager.getScheduleEvents(robotId, scheduleType, callbackSuccess, callbackError)
        var tDeffer = parent.communicationWrapper.exec(RobotPluginManager.getScheduleEvents, [that.robot().robotId(), 0]);
        tDeffer.done(that.getScheduleEventsSuccess);
        tDeffer.fail(that.getScheduleEventsError);
    }
    
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
            $('#addButton').removeClass("ui-disabled");
            var aDeffer = [];
    
            // load event data
            for (var i = 0, max = result.scheduleEventLists.length; i < max; i++) {
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
    this.getScheduleEventsError = function(error) {
        // Server Error create an empty scheduler
        if(error && error.errorCode == 1003) {
            var tDeffer = parent.communicationWrapper.exec(RobotPluginManager.createSchedule, [that.robot().robotId(), 0]);
            tDeffer.done(that.createScheduleEventsSuccess);
            tDeffer.fail(that.createScheduleEventsError);
        }
        console.log(error);
    }
    this.createScheduleEventsSuccess = function(result) {
        console.log("createScheduleEventsSuccess\n" +JSON.stringify(result));
        parent.communicationWrapper.setDataValue("scheduleId", result.scheduleId);
        $('#addButton').removeClass("ui-disabled");
    }
    this.createScheduleEventsError = function(error) {
         console.log(error);
    }
    this.loadScheduleDataSuccess = function(result) {
        console.log("loadScheduleDataSuccess\n" + JSON.stringify(result));
        // {
        // 'scheduleId': '955fe88b-061f-4cc0-9f2b-c4baa73b156a',
        // 'scheduleEventId':'76d784e0-78a2-45e0-a67a-3f404eecafc8',
        // 'scheduleEventData': {'startTime':'10:30','day':1, 'cleaningMode':'1'}
        // }

        // add the day to the day blocked list
        that.blockedDays.push(result.scheduleEventData.day);
        that.scheduler.addEvent(result);
    }
    this.loadScheduleDataError = function(error) {
        console.log(error);
    }
    /* <actionbar functions> */
    this.back = function() {
        parent.flowNavigator.previous();
    };

    this.edit = function() {
        that.conditions['editEvent'] = true;
        var bundle = {};
        bundle.blockedDays = that.blockedDays;
        bundle.events = that.scheduler.selectedEvents();
        parent.flowNavigator.next(bundle);
    };

    this.add = function() {
        that.conditions['addEvent'] = true;
        var bundle = {};
        bundle.blockedDays = that.blockedDays;
        parent.flowNavigator.next(bundle);
    }

    this.del = function() {
        var events = that.scheduler.selectedEvents();
        var weekIndex = $.map($.i18n.t("pattern.week").split(","), function(value){
            return +value;
            });
            jQuery.inArray((2), weekIndex);
        var contextBuffer = {};
        var contextBufferAsString = "";
        
        // create text for each event and store it in buffer localization dependent (pattern.week)
        $.each(events, function(index, item) {
            var sContext = "";
            sContext += $.i18n.t("common.day." + item.scheduleEventData.day);
            sContext += " " + localizeTime(item.scheduleEventData.startTime)+",";
            sContext += $.i18n.t("common.cleaningMode." + item.scheduleEventData.cleaningMode);
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
        parent.notification.showDialog(dialogType.WARNING,'Delete event', $.i18n.t('dialogs.EVENT_DELETE.title',{count:events.length}) +"</br>"+ contextBufferAsString, 
            [{label:$.i18n.t('dialogs.EVENT_DELETE.button_1'), callback:that.commitDel}, {label:$.i18n.t('dialogs.EVENT_DELETE.button_2')}]); 
    }
    this.commitDel = function() {
        var events = that.scheduler.selectedEvents();
        var aDeffer = [];
        parent.notification.closeDialog();

        $.each(events, function(index, item) {
            //RobotPluginManager.deleteScheduleEvent(scheduleId, scheduleEventId, callbackSuccess, callbackError)
            var tempDeferred = parent.communicationWrapper.exec(RobotPluginManager.deleteScheduleEvent, [parent.communicationWrapper.dataValues["scheduleId"], item.scheduleEventId], 
                { type: notificationType.SPINNER, message: "" , bHide: false });
            
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
            if(notificationOptions && notificationOptions.type) {
                parent.notification.showLoadingArea(false, notificationOptions.type);
            } else {
                parent.notification.reset();
            }
            that.updateSchedule(null);
        });
    }
    
    this.updateSchedule = function(element) {
        var sUpdate = $.i18n.t('communication.update_scheduler');
        
        //RobotPluginManager.updateSchedule(scheduleId, callbackSuccess, callbackError)
        var tDeffer = parent.communicationWrapper.exec(RobotPluginManager.updateSchedule, [parent.communicationWrapper.dataValues["scheduleId"]],
            { type: notificationType.OPERATION, message: sUpdate}
        );
        tDeffer.done(function(result) {that.updateScheduleSuccess(result, element)});
        tDeffer.fail(that.updateScheduleError);
    }
    this.updateScheduleSuccess = function(result, element) {
        console.log("updateScheduleSuccess\n" + JSON.stringify(result));
        if(element != null) {
            that.scheduler.updatedEvent(element);
        }
    }
    this.updateScheduleError = function(error) {
        console.log("updateScheduleError\n" + JSON.stringify(error));
    }
    
    this.updateEvent = function(event, element) {
        //console.log($(element));
        var item = $(element).data('reference');
        console.log(JSON.stringify(item))
        var tempDeferred = parent.communicationWrapper.exec(RobotPluginManager.updateScheduleEvent, [parent.communicationWrapper.dataValues["scheduleId"],item.scheduleEventId, { startTime:item.scheduleEventData.startTime,day:item.scheduleEventData.day, cleaningMode:item.scheduleEventData.cleaningMode}], 
            { type: notificationType.NONE, message: ""});
        
        tempDeferred.done(function(result) {that.updateScheduleEventSuccess(result, element)});
        tempDeferred.fail(that.updateScheduleEventError);
    }
    
    this.updateScheduleEventSuccess = function(result, element) {
        console.log("updateScheduleEventSuccess\n" + JSON.stringify(result));
        this.updateSchedule(element);
    }
    this.updateScheduleEventError = function(error) {
        console.log("updateScheduleEventError\n" + JSON.stringify(error));
    }
    
    /* </actionbar functions> */
   
   // popup links
    this.cleaning = function() {
        that.conditions['cleaning'] = true;
        parent.flowNavigator.next();
    }
    this.schedule = function() {
        $("#menuPopup").popup("close");
    }
    this.settings = function() {
        // switch to settings workflow
        that.conditions['settings'] = true;
        parent.flowNavigator.next();
    }
})
console.log('loaded file: basicScheduler_ViewModel.js');
