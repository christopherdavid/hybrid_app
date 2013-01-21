resourceHandler.registerFunction('s4-1-1_ViewModel.js', 's4-1-1_ViewModel',
		function(parent) {
			console.log('instance created for: s4-1-1_ViewModel');
			var that = this;
			this.id = 's4-1-1_ViewModel';
			this.conditions = {};
			
			
			this.isBackVisible = ko.observable(true);
		    this.isCancelVisible = ko.observable(false);
		    
		    this.labelTitle = ko.observable($.i18n.t("s4-1-1.navi.title"));
		    
		    var scheduler;
		    

			this.back = function() {
				that.conditions['back'] = true;
				parent.flowNavigator.previous();
			};

			this.init = function() {
				scheduler = new Scheduler($('#schedulerTarget'));
			}

			this.reload = function() {
				// remove conditions
				that.conditions = {};
			}
			
			this.updateLayout = function(){
				scheduler.updateLayout();
				console.log('fix layout');
			}

			this.deinit = function() {
				scheduler.destroy();
			}
			
			this.add = function(){
				
			}

		})
console.log('loaded file: s4-1-1_ViewModel.js');