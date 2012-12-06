resourceHandler.registerFunction('s0_ViewModel.js', 's0_ViewModel', function(parent) {
	console.log('instance created for: s0_ViewModel');
	var that = this;
	this.id = 's0_ViewModel';
	this.conditions = {};
	this.startAreaControl = null;
	that = this;

	this.logout = function() {
		that.conditions['logout'] = true;
		parent.flowNavigator.next();
	};

	this.selectRobot = function() {
		// TODO: implement switch to robot selection dialog
		that.conditions['changeRobot'] = true;
		parent.flowNavigator.next();
	};
	
	this.startCleaning = function(){
		//TODO: react on start states of the robot and react accordingly
	}
	
	this.cleaning = function(){
		// TODO: switch to cleaning workflow
	}
	
	this.remote = function(){
		// TODO: switch to remote view
	}
	
	this.schedule = function(){
		// TODO: switch to schedule workflow
	}
	
	this.settings = function(){
		// TODO: switch to settings workflow	
	}

	this.sendToBase = function() {
		// TODO: send command that the robot should return to base
	};

	/**
	 * Called when the viewmodel is initialized (after the view has been loaded)
	 */
	this.init = function() {
		this.startAreaControl = new StartAreaControl($('#startArea'), $(".categoryArea"), $('#startBtn'), $(".categoryTable"), [$('#cleaning'), $('#remote'), $('#schedule'), $('#settings')]);
		this.startAreaControl.init();
	};
	
	this.deinit = function() {
		that.startAreaControl.deinit();
	}
})
console.log('loaded file: s0_ViewModel.js');
