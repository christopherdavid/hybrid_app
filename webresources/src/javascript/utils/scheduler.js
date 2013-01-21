function Scheduler($root) {
	

	that = this;
	
	var STANDARD_EVENT_DURATION = 60;
	var HOUR = 60;
	var HOUR_IN_PX = 154;
	var MIN_IN_PX = HOUR_IN_PX/60;
	var scroller;
	var $content;
	var $scrollWrapper;
	var $timeColumn;
	var $dayRow;
	
	var columns = new Array(7);

	/**
	 * updates the width of layout to take resize/orientation changes
	 */
	this.updateLayout = function() {
		scroller.refresh();
		var width = $scrollWrapper.width();

		var delta = $content.outerWidth(true) - $content.innerWidth();

		$content.width((parseInt(width) - delta) + "px");

		//$('.dayRow').width((parseInt(width) - delta) + "px");

		// android Bugfix, doesn't work :'(
		var contentWidth = (parseInt($content.css('width')) - delta);
		var colwidth = Math.floor(contentWidth / 7) + "px";
		$('.day').width(colwidth);
		$('.dayLabel').width(Math.floor(contentWidth / 7) + "px");
		
	}

	/**
	 * Builds the control and all of its functionality
	 */
	this.init = function() {
		
		buildDomTree();
		
		fillTimeColumn();
		addDayColumns();
		addEvents();
		
		
		// prevent the default behavior of standard touch events
		document.addEventListener('touchmove.scheduler', function(e) {
			e.preventDefault();
		}, false);
		
		//create iScroller
		scroller = new iScroll('scrollWrapper', {
			hScrollbar : false,
			vScrollbar : false,
			bounce : true,
			vScroll : true,
			hScroll : true,
			momentum : true,
			onPosChanged : function(x, y) {

				onScrollpositionChanged(x, y);
			}
		});
		
		//receive resize and orientation change events
		$(window).on("resize.scheduler", function() {
			that.updateLayout();
		});

		//listener after all divs got a size
		$(document).one("pageshow.scheduler", function(e) {
			initLayout();
		});
		
	}
	
	/**
     * remove event handler and destroy objects
     */
    this.destroy = function() {
        // destroy iScroll object
        scroller.destroy();
        columns = null;
        // remove all event handler for scheduler
        $(document).off(".scheduler");
    }

	/**
	 * adds the timeline on the left side
	 */
	function fillTimeColumn() {
		for (i = 0; i < 24; i++) {
			amPmMarker = i<=12?'am':'pm';
			
			
			// add leading zero
			hour = i < 10 ? "0" + i : i;
			
			hour = hour % 12;
			if(hour == 0 && i >= 12){
				hour = 12;
			}
		

			//$timeColumn.append('<div class="time">' + hour + ':00 am</div>');
			
			$timeColumn.append($('<div/>', {
				'class' : 'time',
				'text' : hour + ':00 '+ amPmMarker,
			}))
		}
	}

	/**
	 * adds the columns for the days
	 */
	function addDayColumns() {
		for (i = 1; i <= 7; i++) {
			var $day = $('<div/>', {
				'class' : 'day',
			}).appendTo($content);
			columns[i - 1] = $day;

			var $label = $('<div/>', {
				'class' : 'dayLabel',
				text : $.i18n.t("s4-1-1.day." + i).substr(0, 3)
			}).appendTo($dayRow);

			// $content.append('<div class="day"><div
			// class="dayLabel">Mon</div></div></div>');
		}
	}

	/**
	 * adds the Events into the schedule table
	 */
	function addEvents() {


		addEvent(0,HOUR*3);
		addEvent(0,HOUR*1);
		
		addEvent(3, HOUR*2, HOUR*5);
		
		addEvent(4,HOUR*12, HOUR*16);
		
		addEvent(3,HOUR*18, HOUR*19);
		
		addEvent(3,HOUR*15, HOUR*17);
		
		addEvent(5,HOUR*0, HOUR*3);
		
		addEvent(6,HOUR*2, HOUR*5);
		
	}
	
	/**
	 * 
	 * @param day 0-6
	 * @param startTime 0-1440
	 */
	function addEvent(day, startTime){
		addEvent(day, startTime, startTime + STANDARD_EVENT_DURATION);
	}
	
	/**
	 * 
	 * @param day 0-6
	 * @param startTime 0-1440
	 * @param endTime 0-1440
	 */
	function addEvent(day, startTime, endTime){
		var $column = columns[day];
		var $task = $('<div/>', {
			'class' : 'task'
		});
		
		var $taskInner = $('<div/>', {
			'class' : 'taskInner'
		});
		
		$task.css('margin-top',MIN_IN_PX*startTime);
		duration = endTime-startTime;
		$task.height(duration * MIN_IN_PX);
		$task.append($taskInner);
		$column.append($task);
	}

	function initLayout() {
		
		scroller.refresh();
		// content should be touchable everywhere
		$content.height($('.timeColumn').height());
		
		
		that.updateLayout();
	}

	function onScrollpositionChanged(x, y) {

		$('.dayRow').css('-webkit-transform', 'translate(' + x + 'px, 0px)');
		$('.timeColumn')
				.css('-webkit-transform', 'translate(0px, ' + y + 'px)');
	}

	/**
	 * creates the DOM tree for this Control
	 */
	function buildDomTree(){
		/**
		 * <div id="$root" class="scheduler">
              <div class="timeColumn"></div>
              <div class="dayRow"></div>
              <div id="scrollWrapper">
                  <div class="schedulerContent"></div>
              </div>
			</div>
		 */
		
		$root.addClass('scheduler');
		$timeColumn = $('<div/>', {
			'class' : 'timeColumn',
		})
		
		$dayRow = $('<div/>', {
			'class' : 'dayRow',
		})
		
		$scrollWrapper = $('<div/>', {
			'id' : 'scrollWrapper',
		});
		$content = $('<div/>', {
			'class' : 'schedulerContent',
		});
		$scrollWrapper.append($content);
		
		$root.append($timeColumn);
		$root.append($dayRow);
		$root.append($scrollWrapper);
	}

	that.init();
}
