resourceHandler.registerFunction('s4-1-1_ViewModel.js', 's4-1-1_ViewModel',
		function(parent) {
			console.log('instance created for: s4-1-1_ViewModel');
			var that = this;
			this.id = 's4-1-1_ViewModel';
			this.conditions = {};
			this.backConditions = {};

			this.scheduler = 'undefined';
			this.isBackVisible = ko.observable(true);

			this.labelTitle = ko.observable($.i18n.t("s4-1-1.navi.title"));

			/* <enviroment functions> */
			this.init = function() {
				that.scheduler = new Scheduler($('#schedulerTarget'));
				
				parent.communicationWrapper.exec(RobotPluginManager.getSchedule, ['robotId','advanced'], that.loadScheduleSuccess, that.loadScheduleError);


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
			
			this.loadScheduleSuccess = function(data){
				that.scheduler.addEvents(data);
			}
			
			this.loadScheduleError = function(error){
				console.log(error);
			}

			/* <actionbar functions> */
			this.back = function() {
				that.backConditions['home'] = true;
				parent.flowNavigator.previous();
			};

			this.edit = function() {
				that.conditions['editEvent'] = true;
				bundle = {};
				bundle.events = that.scheduler.selectedEvents();
				parent.flowNavigator.next(bundle);
			};

			this.add = function() {
				that.conditions['addEvent'] = true;
				parent.flowNavigator.next();
			}
			
			this.del = function(){
				var events = that.scheduler.selectedEvents();
					parent.communicationWrapper.exec(RobotPluginManager.removeEvent, ['robotId', 'advanced', events], function(success) {
					that.scheduler.deleteEvents(events);
				}, function(error) {
					parent.showError(error);
				});
				
			}
			
			/* </actionbar functions> */
		})
console.log('loaded file: s4-1-1_ViewModel.js');