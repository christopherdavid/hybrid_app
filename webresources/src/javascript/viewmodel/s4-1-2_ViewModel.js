resourceHandler.registerFunction('s4-1-2_ViewModel.js', 
		function(parent) {
			console.log('instance created for: s4-1-2_ViewModel');
			var that = this;
			this.conditions = {};
			
			this.cleaningDays = ko.observableArray([]);
			this.selectedCleaningDays = ko.observableArray([]);
			
			this.notDisturbDays = ko.observableArray([]);
			this.selectedNotDisturbDays = ko.observableArray([]);
			
			
			this.isQuietTime = ko.observable(false);
			this.isCleaningEvent = ko.observable(true);
			this.isNextEnabled = ko.computed(function(){
				var visible = false;
				if(that.isCleaningEvent() && that.selectedCleaningDays().length > 0){
					visible = true;
				} else
				if(that.isQuietTime() && that.selectedNotDisturbDays().length > 0){
					visible = true;
				}
				return visible;
			},this)
			
			
		    this.labelTitle = ko.observable($.i18n.t("s4-1-2.navi.title"));
		    
		    
		    
		    var $cleaningTimePicker;
		    var $quietStartTimePicker;
			var $quietEndTimePicker;

		    /* <enviroment functions> */
			this.init = function() {
				if(that.cleaningDays().length == 0){
					for (i = 1; i <= 7; i++) {
						that.cleaningDays.push({"dayName":$.i18n.t("s4-1-1.day." + i).substr(0, 3), "id":i-1});
						that.notDisturbDays.push({"dayName":$.i18n.t("s4-1-1.day." + i).substr(0, 3), "id":i-1});
					}
				}
				
				var amPmStyle = 'hhiiA';
				var normal = 'HHii';
				
				var langFormat = that.language().indexOf("en") == -1 ? normal : amPmStyle;
				
				$('.timePicker').mobiscroll().time({
			        theme: 'default',
			        display: 'inline',
			        timeWheels: langFormat,
			        mode: 'scroller',
			        stepMinute: 15,
			        onChange: setEndTime
			    });
				
				$cleaningTimePicker = $('#cleaningTime');
				$quietStartTimePicker = $('#quietStartTime');
				$quietEndTimePicker = $('#quietEndTime');
				
				
				if(that.bundle){
					if(that.bundle.events){
						var type = bundle.events[0].eventType;
						var startTime =  $.scroller.parseDate('HH:ii', bundle.events[0].startTime);
						
						$cleaningTimePicker.scroller('setDate', startTime, true);
						$quietStartTimePicker.scroller('setDate', startTime, true);
						
						that.selectedCleaningDays.removeAll();
						that.selectedNotDisturbDays.removeAll();
						
						$.each(bundle.events, function(index, item){
							var i = item.day;
							that.selectedNotDisturbDays.push(that.notDisturbDays()[i]);
							that.selectedCleaningDays.push(that.cleaningDays()[i]);
						});
						
						if(type == 'quiet'){
							var endTime = $.scroller.parseDate('HH:ii', bundle.events[0].endTime);
							$quietEndTimePicker.scroller('setDate', endTime, true);
							
							//select quietTime dirty hack I know -.-
							setTimeout(function(){
								$('.ui-block-b').children().click();
							},50)
						}
						
					}
				}
				
			
				
				
				setEndTime();
				
				//receive resize and orientation change events
				$(window).on("resize.timeset", function() {
					that.updateLayout();
				});
				$(document).one("pageshow.timeset", function(e) {
					that.updateLayout();
				});
			}

			this.reload = function() {
				// remove conditions
				that.conditions = {};
			}

			this.deinit = function() {+
				$(document).off(".timeset");
			}
			/* </enviroment functions> */
		    
			
			/* <actionbar functions> */
			this.cancel = function() {
				that.conditions['cancel'] = true;
				parent.flowNavigator.previous(that.bundle);
			};
			
			this.next = function() {
				
				var events = new Array(that.selectedCleaningDays().length);
				
				
				//generate random UUID
				var uuid = 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
				    var r = Math.random()*16|0, v = c == 'x' ? r : (r&0x3|0x8);
				    return v.toString(16);
				});

				if (that.isCleaningEvent()) {
					var startDate = $cleaningTimePicker.scroller('getDate');
					var startTime = $.scroller.formatDate('HH:ii', startDate);
					
					var i = 0;					
					ko.utils.arrayForEach(that.selectedCleaningDays(), function(item) {
						var cleaningEvent = {
							'eventType' : 'cleaning',
							'day' : item.id,
							'startTime' : startTime,
							'id': uuid
						}
						events[i] = cleaningEvent;
						i++;
					});
					
					that.conditions['cleaning'] = true;
					
					var bundle = {};
					bundle.oldEvents = that.bundle ? that.bundle.events : {};
					
					bundle.events = events;
					
					
					parent.flowNavigator.next(bundle);
					
					
				} else if (that.isQuietTime()){
					
					
					var startDate = $quietStartTimePicker.scroller('getDate');
					var endDate = $quietEndTimePicker.scroller('getDate');
					var startTime = $.scroller.formatDate('HH:ii', startDate);
					var endTime = $.scroller.formatDate('HH:ii', endDate);
					
					
					
					var i = 0;					
					ko.utils.arrayForEach(that.selectedNotDisturbDays(), function(item) {
						var cleaningEvent = {
							'eventType' : 'quiet',
							'day' : item.id,
							'startTime' : startTime,
							'endTime': endTime,
							'id': uuid
						}
						events[i] = cleaningEvent;
						i++;
					});
					
					that.conditions['quiet'] = true;
					
					if(that.bundle && that.bundle.events){
						//remove old events and add the new ones
						parent.communicationWrapper.exec(RobotPluginManager.removeEvent, ['robotId', 'advanced', that.bundle.events], function(success) {
							parent.communicationWrapper.exec(RobotPluginManager.addEvent, ['robotId', 'advanced', events], function(success) {
								parent.flowNavigator.next();
							}, function(error) {
								parent.showError(error);
							});
						}, function(error) {
							parent.showError(error);
						});
						
					}else{
					
						parent.communicationWrapper.exec(RobotPluginManager.addEvent, ['robotId', 'advanced', events], function(success) {
							parent.flowNavigator.next();
						}, function(error) {
							parent.showError(error);
						});
					}
					
					
					
					
					
				}

				
			};
			/* </actionbar functions> */
			
			this.selectCleaningEvent = function(){
				that.isQuietTime(false);
				that.isCleaningEvent(true);
			}

			this.selectQuietTime = function() {
				that.isCleaningEvent(false);
				that.isQuietTime(true);
			}
			
			this.cleaningDaySelected = function(item){
				toggleItem(item, that.selectedCleaningDays);
			}
			
			this.notDisturbDaySelected = function(item){
				toggleItem(item, that.selectedNotDisturbDays);
			}
			
			function toggleItem(item, list){
				var removedItemId = list.indexOf(item);
				// item was not in array
				if(removedItemId == -1){
					list.push(item);
				}else{
					list.remove(item);
				}
			}
			
			this.updateLayout = function() {
				var width = $('.dayPicker').innerWidth();
				$('.dayPicker li').width(Math.floor(width / 7)-2 + "px");
			}
			
			function setEndTime(){
	        	var startDate = $quietStartTimePicker.scroller('getDate');
	        	startDate.setMinutes(startDate.getMinutes() + 15);
				$quietEndTimePicker.scroller('option', 'minDate', startDate);
				
			}
			
		})
console.log('loaded file: s4-1-1_ViewModel.js');