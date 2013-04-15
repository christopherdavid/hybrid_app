/**
 * scheduler control
 * creates a basic or advanced scheduler debepdening on scheduleType
 * @(Object) $root the jquery object of the root node
 * @(Integer) scheduleType the type describes the layout and behavior of
 * the scheduler, 0: basic, 1: advanced
 */
function Scheduler($root, scheduleType) {

    that = this;

    var STANDARD_EVENT_DURATION = 60;
    var HOUR = 60;
    var HOUR_IN_PX = 154;
    var MIN_IN_PX = HOUR_IN_PX / 60;
    var scroller;
    var $content;
    var $scrollWrapper;
    var $timeColumn;
    var $dayRow;
    // need to parse each entry from string to an integer
    var weekIndex = $.map($.i18n.t("pattern.week").split(","), function(value){
        return +value;
        });

    var columns = new Array(7);

    this.selectedEvents = ko.observableArray([]);

    /**
     * updates the width of layout to take resize/orientation changes
     */
    this.updateLayout = function() {
        scroller.refresh();
        var width = $scrollWrapper.width();

        // delta == margin left + right
        var delta = $content.outerWidth(true) - $content.innerWidth();

        $content.width((parseInt(width) - delta) + "px");

        // $('.dayRow').width((parseInt(width) - delta) + "px");

        // android Bugfix
        var contentWidth = (parseInt($content.css('width')) - delta);
        var colwidth = Math.floor(contentWidth / 7) + "px";
        $('.day').width(colwidth);
        $('.dayLabel').width(Math.floor(contentWidth / 7) + "px");

    }
    /**
     * selects the current clicked element of this event. if the second clicked
     * element has the same starting time both are selected and so on. if a
     * element with an other starting time got clicked it is selected as the
     * only
     */
    this.clickedEvent = function(event) {

        var selector = (event.eventType == 'quiet') ? '.quiet' : '.task';

        var index = that.selectedEvents.indexOf(event);
        // event is not in array
        if (index == -1) {
            /*
            if (that.selectedEvents().length > 0) {
                var firstSelected = that.selectedEvents()[0];
                if (firstSelected.startTime != event.startTime || firstSelected.rooms != event.rooms || firstSelected.endTime != event.endTime) {
                    that.selectedEvents.removeAll();
                    $('.task, .quiet').removeClass('selected');
                }
            }
            */
            that.selectedEvents.push(event);
            $(selector).each(function(index, ele) {
                if ($(ele).data('reference') === event) {
                    $(ele).addClass('selected');
                }
            });
        } else {
            that.selectedEvents.remove(event);
            $(selector).each(function(index, ele) {
                if ($(ele).data('reference') === event) {
                    $(ele).removeClass('selected');
                }
            });
        }

        console.log(that.selectedEvents());

    }
    /**
     * Builds the control and all of its functionality
     */
    this.init = function() {

        buildDomTree();

        fillTimeColumn();
        addDayColumns();

        // prevent the default behavior of standard touch events
        document.addEventListener('touchmove.scheduler', function(e) {
            e.preventDefault();
        }, false);

        // create iScroller
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

        // receive resize and orientation change events
        $(window).on("resize.scheduler", function() {
            that.updateLayout();
        });

        // listener after all divs got a size
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

    this.addEvents = function(eventArray) {
        for (var i = 0; i < eventArray.length; i++) {
            that.addEvent(eventArray[i]);
        }
    }

    this.addEvent = function(event) {
        var parsedStartTime = $.scroller.parseDate('HH:ii', event.scheduleEventData.startTime);
        var startTimeInMin = parsedStartTime.getHours() * 60 + parsedStartTime.getMinutes();
        //console.log("addEvent:\n" + JSON.stringify(event) + "\nweekIndex:\n" + weekIndex);

        if (scheduleType == 0) {
            var endTime = startTimeInMin + HOUR / 2;
            var clazz = 'task';
            // create View, need to convert the integer to an string for comparison
            var $column = columns[jQuery.inArray((event.scheduleEventData.day), weekIndex)];
            var $task = $('<div/>', {
                'class' : clazz + " event"
            });
            $task.data('reference', event);
            
            var sMode =$.i18n.t("common.cleaningMode." + event.scheduleEventData.cleaningMode)

            var $taskInner = $('<div/>', {
                'class' : clazz + 'Inner state-' + event.state
            }).html(sMode);

            $task.css('margin-top', MIN_IN_PX * startTimeInMin);
            duration = endTime - startTimeInMin;
            $task.height(duration * MIN_IN_PX);
        } else {            //default endtime for a cleaing event
            var endTime = startTimeInMin + HOUR;

            if (event.eventType == 'quiet') {
                var parsedEndTime = $.scroller.parseDate('HH:ii', event.endTime);
                endTime = parsedEndTime.getHours() * 60 + parsedEndTime.getMinutes();
            }

            var clazz = (event.eventType == 'quiet') ? 'quiet' : 'task';

            // create View
            var $column = columns[event.scheduleEventData.day];
            var $task = $('<div/>', {
                'class' : clazz + " event"
            });
            $task.data('reference', event);

            var $taskInner = $('<div/>', {
                'class' : clazz + 'Inner state-' + event.state
            });

            $task.css('margin-top', MIN_IN_PX * startTimeInMin);
            duration = endTime - startTimeInMin;
            $task.height(duration * MIN_IN_PX);
        }
        $task.append($taskInner);
        //use mousedown and not click because of the double fire click event in iscroll
        $task.mousedown(function(event) {
            console.log(event.pageX + "/" + event.pageY);
            var event = $(this).data('reference');
            that.clickedEvent(event);
        });
        $task.bind('taphold', function() {
            console.log('longclick');

        });

        $column.append($task);

    }

    this.deleteEvents = function(eventArray) {
        //iterate from behind, because the last item always got deleted
        for (var i = eventArray.length - 1; i >= 0; i--) {
            that.deleteEvent(eventArray[i]);
        }
    }

    this.deleteEvent = function(event) {
        that.selectedEvents.remove(event);

        $('.event').each(function(index, element) {
            var revEvent = $(this).data('reference');
            if (revEvent == event) {
                $(this).remove();
            }
        });
    }
    /**
     * adds the timeline on the left side
     */
    function fillTimeColumn() {
        var timeFormat = $.i18n.t("pattern.time");
        for (var i = 0; i < 24; i++) {
            var amPmMarker = "";
            var hour = i;
            if (timeFormat == "hhiiA") {
                hour = hour % 12;
                if (hour == 0 && i >= 12) {
                    hour = 12;
                }
                amPmMarker = i <= 12 ? ' am' : ' pm';
            }

            // add leading zero
            hour = hour < 10 ? "0" + hour : hour;

            $timeColumn.append($('<div/>', {
                'class' : 'time',
                'text' : hour + ':00' + amPmMarker,
            }))
        }
    }

    /**
     * adds the columns for the days
     */
    function addDayColumns() {
        for (var i = 0; i < weekIndex.length; i++) {
            var $day = $('<div/>', {
                'class' : 'day',
            }).appendTo($content);
            columns[i] = $day;

            var $label = $('<div/>', {
                'class' : 'dayLabel',
                text : $.i18n.t("common.day." + weekIndex[i]).substr(0, 3)
            }).appendTo($dayRow);
        }
    }

    function initLayout() {

        scroller.refresh();
        // content should be touchable everywhere
        $content.height($('.timeColumn').height());

        that.updateLayout();
    }

    function onScrollpositionChanged(x, y) {

        $('.dayRow').css('-webkit-transform', 'translate(' + x + 'px, 0px)');
        $('.timeColumn').css('-webkit-transform', 'translate(0px, ' + y + 'px)');
    }

    /**
     * creates the DOM tree for this Control
     */
    function buildDomTree() {
        /**
         * <div id="$root" class="scheduler"> <div class="timeColumn"></div>
         * <div class="dayRow"></div> <div id="scrollWrapper"> <div
         * class="schedulerContent"></div> </div> </div>
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
