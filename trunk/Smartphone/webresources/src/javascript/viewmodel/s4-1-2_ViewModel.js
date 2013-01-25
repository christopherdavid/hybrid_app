resourceHandler.registerFunction('s4-1-2_ViewModel.js', 's4-1-2_ViewModel',
		function(parent) {
			console.log('instance created for: s4-1-2_ViewModel');
			var that = this;
			this.id = 's4-1-2_ViewModel';
			this.conditions = {};
			
			this.cleaningDays = ko.observableArray([]);
			this.selectedCleaningDays = ko.observableArray([]);
			
			this.notDisturbDays = ko.observableArray([]);
			this.selectedNotDisturbDays = ko.observableArray([]);
			
			
			this.isQuietTime = ko.observable(false);
			this.isCleaningEvent = ko.observable(true);
			
			
		    this.labelTitle = ko.observable($.i18n.t("s4-1-2.navi.title"));
		    

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
			        mode: 'scroller'
			    });
				
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
				parent.flowNavigator.previous();
			};
			
			this.next = function() {
				that.conditions['next'] = true;
				parent.flowNavigator.next();
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
			
		})
console.log('loaded file: s4-1-1_ViewModel.js');