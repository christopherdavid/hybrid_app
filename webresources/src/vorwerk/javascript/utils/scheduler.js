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
    var HOUR_IN_PX = deviceSize.getResolution() == "high" ? 160 : 80;
    var MIN_IN_PX = HOUR_IN_PX / 60;
    var DEFAULT_START_TIME = "8:00";
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
    // better use a variable instead of read and convert webkit-transform value using a matrix 
    var containerScrollY = 0;
    var containerScrollX = 0;
    
    var autoScroll = {
        border:{ // this is the border which activates scrolling
            top:0,
            bottom:0
        },
        element:{
            ui:null,
            synced: false,
            initialValue: {
                x:0,
                y:0
            },
            scrollStart: {
                x:0,
                y:0
            },
            delta: {
                x:0,
                y:0
            },
            position: {
                x:0,
                y:0
            }
        },
        isScrolling: false,
        timer:null,
        interval:50,
        step: HOUR_IN_PX/4,
        direction: 0, //0: up 1: down
        UP:0,
        DOWN:1
    }
    
    this.selectedEvents = ko.observableArray([]);
    this.updatedEvents = ko.observableArray([]);
    
    /**
     * Builds the control and all of its functionality
     */
    this.init = function() {
        $.event.special.tap.tapholdThreshold = 150; 
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
            scrollToTime(DEFAULT_START_TIME);
        });
    }
    /**
     * remove event handler and destroy objects
     */
    this.destroy = function() {
        $.event.special.tap.tapholdThreshold = 750; 
        // destroy iScroll object
        scroller.destroy();
        columns = null;
        // remove all event handler for scheduler
        $('.event div').off(".scheduler");
        $(window).off(".scheduler");
    }

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
        //console.log("updateLayout")
    }
    
    this.addEvents = function(eventArray) {
        for (var i = 0; i < eventArray.length; i++) {
            that.addEvent(eventArray[i]);
        }
    }

    this.addEvent = function(oEvent) {
        var parsedStartTime = $.scroller.parseDate('HH:ii', oEvent.scheduleEventData.startTime);
        var startTimeInMin = parsedStartTime.getHours() * 60 + parsedStartTime.getMinutes();
        //console.log("addEvent:\n" + JSON.stringify(event) + "\nweekIndex:\n" + weekIndex);
        
        // basic scheduler
        if (scheduleType == 0) {
            var endTime = startTimeInMin + HOUR;
            var clazz = 'basicTask';
            // create View, need to convert the integer to an string for comparison
            var $column = columns[jQuery.inArray((oEvent.scheduleEventData.day), weekIndex)];
            var $task = $('<div/>', {
                'class' : clazz + " event"
            });
            $task.data('reference', oEvent);
            var $taskInner = $('<div/>', {
                'class' : clazz + 'Inner mode-'+ oEvent.scheduleEventData.cleaningMode
            });
            
            $task.css('top', MIN_IN_PX * startTimeInMin);
        
        // advanced scheduler
        } else {
            //default endtime for a cleaing event
            var endTime = startTimeInMin + HOUR;

            if (oEvent.eventType == 'quiet') {
                var parsedEndTime = $.scroller.parseDate('HH:ii', oEvent.endTime);
                endTime = parsedEndTime.getHours() * 60 + parsedEndTime.getMinutes();
            }

            var clazz = (oEvent.eventType == 'quiet') ? 'quiet' : 'task';

            // create View
            var $column = columns[oEvent.scheduleEventData.day];
            var $task = $('<div/>', {
                'class' : clazz + " event"
            });
            $task.data('reference', oEvent);

            var $taskInner = $('<div/>', {
                'class' : clazz + 'Inner state-' + oEvent.state
            });

            $task.css('margin-top', MIN_IN_PX * startTimeInMin);
            duration = endTime - startTimeInMin;
            $task.height(duration * MIN_IN_PX);
        }
        $task.append($taskInner);
        
        $taskInner.on("vclick.scheduler", function(event) {
            that.clickedEvent($(event.target.parentElement));
            event.preventDefault();
            event.stopPropagation();
        });
        
        $taskInner.on("taphold.scheduler", function(event) {
            var oEvent = $(event.target.parentElement).data('reference');
            var $column = columns[jQuery.inArray((oEvent.scheduleEventData.day), weekIndex)];
            $column.toggleClass("dragging",true);
        });
        
        $column.append($task);
        // mark dayLabel 
        var day = $dayRow.children(".dayLabel")[jQuery.inArray((oEvent.scheduleEventData.day), weekIndex)];
        $(day).addClass("hasEvent");

        $task.draggable({
            grid : [HOUR_IN_PX/4, HOUR_IN_PX/4],
            containment: "parent",
            scroll:false,
            handle: "div."+clazz+"Inner",
            axis: "y",
            start : function(event, ui) {
                var oEvent = $(event.target).data('reference');
                var $column = columns[jQuery.inArray((oEvent.scheduleEventData.day), weekIndex)];
                $column.toggleClass("dragging",true);
                
                // disable iScroll, to drag the selection area
                autoScroll.border.top = -containerScrollY + autoScroll.step;
                autoScroll.border.bottom = -containerScrollY + $scrollWrapper.height() - HOUR_IN_PX - autoScroll.step;
                scroller.disable();
            },
            drag:function(event, ui) {
                var i = $(this).data("draggable"), o = i.options, scrolled = false;
                /*
                console.log("drag ui.position.top:" + ui.position.top 
                    + " initialValue " + autoScroll.element.initialValue.y 
                    + " position " + autoScroll.element.position.y 
                    + " scrollStart " + autoScroll.element.scrollStart.y
                    + " delta " + autoScroll.element.delta.y);
                */
                // state initial: pressed event and start dragging
                if(autoScroll.element.ui == null) {
                    // check if auto scroll must be activated
                    if(that.checkScrolling(ui.position.top)) {
                        autoScroll.element.ui = ui;
                        autoScroll.element.initialValue.y = ui.position.top;
                        autoScroll.element.scrollStart.y = ui.position.top;
                        autoScroll.element.position.y = ui.position.top;
                        
                        containerScrollY = autoScroll.direction == autoScroll.DOWN ? containerScrollY + autoScroll.step : containerScrollY - autoScroll.step;
                        $content.css('-webkit-transform', 'translate(' + containerScrollX + 'px, ' + containerScrollY + 'px)');
                        $('.timeColumn').css('-webkit-transform', 'translate(0px, ' + containerScrollY + 'px)');
                        autoScroll.timer = window.setTimeout(function(){ that.handleAutoScroll() }, autoScroll.interval);
                    }
                // state update: auto scroll already initialized, update position of event
                } else {
                    if(autoScroll.isScrolling) {
                        // check if drag direction changes
                        if(autoScroll.direction == autoScroll.DOWN && autoScroll.element.scrollStart.y < ui.position.top) {
                            console.log("CHANGED DIRECTION: dragging downwards");
                            autoScroll.border.top = -containerScrollY + autoScroll.step;
                            autoScroll.border.bottom = -containerScrollY + $scrollWrapper.height() - HOUR_IN_PX - autoScroll.step;
                            autoScroll.element.position.y -= autoScroll.step;
                            window.clearTimeout(autoScroll.timer);
                            autoScroll.isScrolling = false;
                        } else if(autoScroll.direction == autoScroll.UP && autoScroll.element.scrollStart.y > ui.position.top) {
                            console.log("CHANGED DIRECTION: dragging upwards");
                            autoScroll.border.top = -containerScrollY + autoScroll.step;
                            autoScroll.border.bottom = -containerScrollY + $scrollWrapper.height() - HOUR_IN_PX - autoScroll.step;
                            autoScroll.element.position.y += autoScroll.step;
                            window.clearTimeout(autoScroll.timer);
                            autoScroll.isScrolling = false;
                        }
                    } else {
                        var tempDelta = ui.position.top - autoScroll.element.initialValue.y;
                        
                        // check if border has been reached and correct delta
                        // because in handleAutoScroll the event was set to top or bottom border and of this is now more in sync with the pressed event
                        if(!autoScroll.element.synced && autoScroll.element.scrollStart.y != ui.position.top) {
                            if(autoScroll.direction == autoScroll.DOWN && autoScroll.element.position.y == 0
                                && ui.position.top >= 0) {
                                    autoScroll.element.delta.y += ui.position.top - autoScroll.element.scrollStart.y;
                                    console.log("synchronized scrollStart and fixed delta: " + autoScroll.element.delta.y)
                                    autoScroll.element.scrollStart.y = ui.position.top;
                                    autoScroll.element.synced = true;
                            } else if(autoScroll.direction == autoScroll.UP && (autoScroll.element.position.y == $('.day').height() - HOUR_IN_PX)
                                && ui.position.top <= $('.day').height() - HOUR_IN_PX) {
                                    autoScroll.element.delta.y += ui.position.top - autoScroll.element.scrollStart.y;
                                    console.log("synchronized scrollStart and fixed delta: " + autoScroll.element.delta.y)
                                    autoScroll.element.scrollStart.y = ui.position.top;
                                    autoScroll.element.synced = true;
                            }
                        }
                        
                        // check if delta has changed
                        if(autoScroll.element.delta.y != tempDelta) {
                            // check if delta is still in range
                            if(autoScroll.element.position.y + tempDelta - autoScroll.element.delta.y >= 0 
                                && autoScroll.element.position.y + tempDelta - autoScroll.element.delta.y <= $('.day').height() - HOUR_IN_PX) {
                                autoScroll.element.position.y += tempDelta - autoScroll.element.delta.y;
                                autoScroll.element.delta.y = tempDelta;
                            }
                        }                        
                        // check if auto scroll must be re-activated
                        if(that.checkScrolling(autoScroll.element.position.y)) {
                            autoScroll.element.scrollStart.y = ui.position.top;
                            autoScroll.element.synced = false;
                            containerScrollY = autoScroll.direction == autoScroll.DOWN ? containerScrollY + autoScroll.step : containerScrollY - autoScroll.step;
                            $content.css('-webkit-transform', 'translate(' + containerScrollX + 'px, ' + containerScrollY + 'px)');
                            $('.timeColumn').css('-webkit-transform', 'translate(0px, ' + containerScrollY + 'px)');
                            autoScroll.timer = window.setTimeout(function(){ that.handleAutoScroll() }, autoScroll.interval);
                        }
                    }
                    
                    // set position otherwise the event would set the position to the old coordinates (pressed event coordinates)
                    ui.position.top = autoScroll.element.position.y;
                }
            },
            stop : function(event, ui) {
                var oEvent = $(event.target).data('reference');
                var $column = columns[jQuery.inArray((oEvent.scheduleEventData.day), weekIndex)];
                $column.toggleClass("dragging",false);
                
                // dragging done: enable iScroll
                scroller.enable();
                if(autoScroll.element.ui != null) {
                    console.log("stop" + autoScroll.element.position.y);
                    that.movedEvent($(event.target), autoScroll.element.position.y);
                    // reset auto scroll
                    window.clearTimeout(autoScroll.timer);
                    autoScroll.element.ui = null;
                    autoScroll.isScrolling = false;
                    autoScroll.element.initialValue.y = 0;
                    autoScroll.element.scrollStart.y = 0;
                    autoScroll.element.position.y = 0;
                    autoScroll.element.delta.y = 0;
                    autoScroll.element.synced = false;
                
                } else {
                    console.log("stop" + ui.position.top);
                    that.movedEvent($(event.target), ui.position.top);
                }
            }
        });
    }
    
    this.checkScrolling = function(posY) {
        console.log("border.top:" + autoScroll.border.top + " border.bottom:" + autoScroll.border.bottom + " posY " + posY);
        
        if(posY < autoScroll.border.top && (containerScrollY + autoScroll.step) < 0 ) {
            console.log("hit top border")
            autoScroll.isScrolling = true;
            autoScroll.direction = autoScroll.DOWN;
            return true;
        } else if(posY > autoScroll.border.bottom && (-containerScrollY + autoScroll.step < $content.height() - $scrollWrapper.height()) ) {
            console.log("hit bottom border")
            autoScroll.isScrolling = true;
            autoScroll.direction = autoScroll.UP;
            return true;
        }
        return false;
    }
    
    this.handleAutoScroll = function() {
        if(autoScroll.direction == autoScroll.DOWN) {
            // check if container needs to be scrolled
            if((containerScrollY + autoScroll.step) < 0 ) {
                containerScrollY += autoScroll.step;
            } else {
                containerScrollY = 0;
            }
            
            // check if event has reached top
            if(autoScroll.element.position.y - autoScroll.step > 0) {
                // move event one step upwards 
                autoScroll.element.position.y -= autoScroll.step;
                autoScroll.element.ui.helper[0].style.top = autoScroll.element.position.y + "px";
                
                // restart timer till top is reached
                autoScroll.timer = window.setTimeout(function(){ that.handleAutoScroll() }, autoScroll.interval);
                            } else {
                autoScroll.element.position.y = 0;
                autoScroll.isScrolling = false;
                autoScroll.border.top = -containerScrollY + autoScroll.step;
                autoScroll.border.bottom = -containerScrollY + $scrollWrapper.height() - HOUR_IN_PX - autoScroll.step;
            }
        } else {
            // check if container needs to be scrolled
            if(-containerScrollY + autoScroll.step < $content.height() - $scrollWrapper.height()) {
                containerScrollY -= autoScroll.step;
            } else {
                containerScrollY = -($content.height() - $scrollWrapper.height());
            }
            
             // check if event has reached bottom
            if(autoScroll.element.position.y + autoScroll.step < $('.day').height() - HOUR_IN_PX) {
                // move event one step upwards 
                autoScroll.element.position.y += autoScroll.step;
                autoScroll.element.ui.helper[0].style.top = autoScroll.element.position.y + "px";
                
                // restart timer till bottom is reached
                autoScroll.timer = window.setTimeout(function(){ that.handleAutoScroll() }, autoScroll.interval);
            } else {
                autoScroll.element.position.y = $('.day').height() - HOUR_IN_PX;
                autoScroll.isScrolling = false;
                autoScroll.border.top = -containerScrollY + autoScroll.step;
                autoScroll.border.bottom = -containerScrollY + $scrollWrapper.height() - HOUR_IN_PX - autoScroll.step;
            }
        }
        // update UI
        $content.css('-webkit-transform', 'translate(' + containerScrollX + 'px, ' + containerScrollY + 'px)');
        $('.timeColumn').css('-webkit-transform', 'translate(0px, ' + containerScrollY + 'px)');
        
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
                // remove eventhandler
                $(this).find("div").off();
                $(this).remove();
            }
        });
    }
    this.movedEvent = function(element, newPos) {
        var oEvent = element.data('reference');
        var newHour = Math.floor(newPos/HOUR_IN_PX);
        var newMin  = newPos%HOUR_IN_PX;
        newMin = newMin/HOUR_IN_PX * 60;
        var newTime = newHour + ":" + (newMin < 10 ? "0" + newMin : newMin);
        console.log("newTime " + newTime);
        //console.log(oEvent);
        //check if time has changed
        if(oEvent.scheduleEventData.startTime != newTime) {
            oEvent.scheduleEventData.startTime = newTime;
            that.updatedEvents.push(oEvent);
            // set state
            element.addClass("state-local");
            $root.triggerHandler("updatedEvent", element);
        }
    }
    this.updatedEvent = function(element) {
        console.log(element)
        var oEvent = $(element).data('reference');
        $(element).removeClass("state-local");
        that.updatedEvents.remove(oEvent);
    }
    /**
     * selects the current clicked element of this event.
     */
    this.clickedEvent = function(element) {
        var oEvent = element.data('reference');
        element.addClass('selected');
        $root.triggerHandler("selectEvent", oEvent);    }
    
    /**
     * adds the timeline on the left side
     */
    function fillTimeColumn() {
        var timeFormat = $.i18n.t("pattern.time");
        for (var i = 0; i < 24; i++) {
            $timeColumn.append($('<div/>', {
                'class' : 'time',
                'text' : localizeTime(i+":00"),
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
            $day.data('dayIndex',weekIndex[i]);
            $day.on("vclick.scheduler", function(event, i) {
                var dayIndex = $(this).data('dayIndex');
                // minus day margin-top and minus background-position-y
                var newY = event.offsetY + parseInt($day.css('margin-top')) - parseInt($content.css('background-position-y'));
                
                var newHour = Math.floor(newY/HOUR_IN_PX);
                var newMin  = newY%HOUR_IN_PX;
                newMin = parseInt(newMin/HOUR_IN_PX * 60);
                
                if(newMin < 8) {
                    // round to 0
                    newMin = 0;
                } else if(newMin > 7 && newMin < 23) {
                    //round to 15
                    newMin = 15;
                } else if(newMin > 22 && newMin < 38) {
                    //round to 30
                    newMin = 30;
                } else if(newMin > 37 && newMin < 53) {
                    //round to 45
                    newMin = 45;
                } else {
                    //round to next hour
                    newHour++;
                    newMin = 0;
                }
                // hand if click would be less than 0 hour or bigger than 24 hour
                if(newHour < 0 || newHour >= 24) {
                    newHour = 0;
                    newMin = 0;
                }
                var newTime = newHour + ":" + (newMin < 10 ? "0" + newMin : newMin);
                
                // visual show a new event on scheduler just for debug reasons
                /*
                var visualY = (newHour*60 + newMin)*MIN_IN_PX;
                
                var $task = $('<div/>', {
                    'class' : 'event',
                    'style' : 'top:' + visualY + 'px'
                });
                
                $('<div/>', {
                    'class' : 'newEnty'
                }).appendTo($task);
                
                $task.appendTo($(this));
                console.log("visualY: " + visualY);
                */ 
                //console.log("Y: " + event.offsetY + " newTime " + newTime + " dayIndex " + dayIndex);
                event.preventDefault();
                event.stopPropagation();
                $root.triggerHandler("newEvent", {
                    startTime:newTime,
                    day: dayIndex
                });
            });

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
    function scrollToTime(time) {
        var parsedStartTime = $.scroller.parseDate('HH:ii', time);
        var startTimeInMin = parsedStartTime.getHours() * 60 + parsedStartTime.getMinutes();
        // scroll to new time but to max of visible area
        containerScrollY = -Math.min( (MIN_IN_PX * startTimeInMin),($content.height() - $scrollWrapper.height()) );
        autoScroll.border.top = -containerScrollY + autoScroll.step;
        autoScroll.border.bottom = -containerScrollY + $scrollWrapper.height() - HOUR_IN_PX - autoScroll.step; 
        $content.css('-webkit-transform', 'translate(0px, ' + containerScrollY + 'px)');
        $('.timeColumn').css('-webkit-transform', 'translate(0px, ' + containerScrollY + 'px)');
    }

    function onScrollpositionChanged(x, y) {
        containerScrollY = y;
        containerScrollX = x;
        autoScroll.border.top = -containerScrollY + autoScroll.step;
        autoScroll.border.bottom = -containerScrollY + $scrollWrapper.height() - HOUR_IN_PX - autoScroll.step;
        $('.dayRow').css('-webkit-transform', 'translate(' + containerScrollX + 'px, 0px)');
        $('.timeColumn').css('-webkit-transform', 'translate(0px, ' + containerScrollY + 'px)');
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
