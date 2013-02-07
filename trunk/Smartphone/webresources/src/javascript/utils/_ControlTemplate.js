function RemoteControl($root) {

	that = this;

	/**
	 * Builds the control and all of its functionality
	 */
	this.init = function() {


		// receive resize and orientation change events
		$(window).on("resize.remote", function() {
		});

		// listener after all divs got a size
		$(document).one("pageshow.remote", function(e) {
		});

	}

	/**
	 * remove event handler and destroy objects
	 */
	this.destroy = function() {
		$(document).off(".remote");
	}

	that.init();
}
