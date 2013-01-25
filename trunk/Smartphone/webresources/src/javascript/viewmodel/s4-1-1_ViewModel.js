resourceHandler.registerFunction('s4-1-1_ViewModel.js', 's4-1-1_ViewModel',
		function(parent) {
			console.log('instance created for: s4-1-1_ViewModel');
			var that = this;
			this.id = 's4-1-1_ViewModel';
			this.conditions = {};
			this.backConditions = {};
			
			this.isEditEnabled = ko.computed(function(){return !isTaskSelected(); },this);
			this.isBackVisible = ko.observable(true);
			
			
		    this.labelTitle = ko.observable($.i18n.t("s4-1-1.navi.title"));
		    
		    var scheduler;
		    
		    
		    /* <enviroment functions> */
			this.init = function() {
				scheduler = new Scheduler($('#schedulerTarget'));
			}

			this.reload = function() {
				// remove conditions
				that.conditions = {};
			}

			this.deinit = function() {
				scheduler.destroy();
			}
			/* </enviroment functions> */
		    
			
			/* <actionbar functions> */
			this.back = function() {
				that.backConditions['home'] = true;
				parent.flowNavigator.previous();
			};
			
			this.edit = function() {
				console.log("edit Task");
			};
			
			this.add = function(){
				that.conditions['addEvent'] = true;
				parent.flowNavigator.next();
			}
			/* </actionbar functions> */
			
			function isTaskSelected(){
				return (scheduler != 'undefined' || scheduler.getSelectedTask() != null);
			}
			
			
			
			
		})
console.log('loaded file: s4-1-1_ViewModel.js');