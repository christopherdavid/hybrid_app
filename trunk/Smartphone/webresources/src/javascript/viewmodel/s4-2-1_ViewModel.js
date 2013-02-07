resourceHandler.registerFunction('s4-2-1_ViewModel.js', 's4-2-1_ViewModel',
		function(parent) {
			console.log('instance created for: s4-2-1_ViewModel');
			var that = this;
			this.id = 's4-2-1_ViewModel';
			this.conditions = {};
			
		    this.labelTitle = ko.observable($.i18n.t("s4-2-1.navi.title"));
		    this.days = ko.observableArray([]);
		    
		    
		    

		    /* <enviroment functions> */
			this.init = function() {
				
				parent.communicationWrapper.exec(RobotPluginManager.getSchedule, ['robotId','basic'], that.loadScheduleSuccess, that.loadScheduleError);

				//receive resize and orientation change events
				$(window).on("resize.schedule", function() {
				});
				$(document).one("pageshow.schedule", function(e) {
				});
			}

			this.reload = function() {
				// remove conditions
				that.conditions = {};
			}

			this.deinit = function() {+
				$(document).off(".schedule");
			}
			/* </enviroment functions> */
		    
			
			/* <actionbar functions> */
			this.back = function() {
				that.conditions['back'] = true;
				parent.flowNavigator.previous(that.bundle);
			};
			/* </actionbar functions> */
			
			this.loadScheduleSuccess = function(data){
				$.each(data,function(index,day){
					that.days.push(day);
				});
			}
			
			this.loadScheduleError = function(error){
				console.log(error);
			}
			
			
		})
console.log('loaded file: s4-2-1_ViewModel.js');