function StartAreaControl(startArea, startContainer,eventArea, startBtn, remote, remoteCells) {
    var posX = null;
    var startBtnDown = false;
    this.remoteButtonDown = null;
    var posY = null;
    var radius = 147;
    var centerX = null;
    var centerY = null;
    var scaleFactor = 0.5;
    var BTN_RADIUS = 147;
    this.isRemoteDisabled = ko.observable(true);
    this.eventMouseDown = false;
    that = this;
    this.pressedIntervall = 250;
    this.pressedTimer;
    

    var centerInParentContainer = function(element) {
        element.css({
            position : 'absolute',
            left : (element.parent().width() - element.outerWidth()) / 2,
            top : (element.parent().height() - element.outerHeight()) / 2,
            margin : 0
        });
    };

    // Checks if a specific dom element contains a point x,y
    var rectContainsPoint = function(x, y, element) {
        var leftEdge = element.offset().left;
        var rightEdge = leftEdge + element.outerWidth()*scaleFactor;
        var topEdge = element.offset().top;
        var bottomEdge = topEdge + element.outerHeight()*scaleFactor;
        /*
        console.log("rectContainsPoint x:" + x + " y " + y 
            + "\nleftEdge " + leftEdge + " rightEdge " + rightEdge
            + "\ntopEdge " + topEdge + " bottomEdge " + bottomEdge
        )
        */

        if (leftEdge <= x && rightEdge >= x && topEdge <= y && bottomEdge >= y) {
            return true;
        }
        return false;
    };

    // checks if a circle contains the point x,y
    var containsPoint = function(x, y, cx, cy, r) {
        var dx = x - cx
        var dy = y - cy
        return dx * dx + dy * dy <= r * r
    };

    this.updatePosition = function() {
        centerX = (startArea.width() * scaleFactor) / 2;
        centerY = (startArea.height() * scaleFactor) / 2;
        radius = BTN_RADIUS * scaleFactor;
        posX = startArea.offset().left;
        posY = startArea.offset().top;
    };

    this.scaleStartArea = function() {
        var content = $("#startContent");

        // Scale the container
        /*
        if (categoryTable.width() > content.width()) {

            scaleFactor = content.width() / categoryTable.width();

            var scaling = "scale(" + scaleFactor + ")";

            categoryTable.css({
                'transform-origin' : '0px 0px',
                '-moz-transform' : scaling,
                '-webkit-transform' : scaling,
                '-o-transform' : scaling,
                'transform' : scaling,
            });

        } else if (scaleFactor < 1.0) {

            categoryTable.css({
                'transform-origin' : '0px 0px',
                '-moz-transform' : 'scale(1.0, 1.0)',
                '-webkit-transform' : 'scale(1.0, 1.0)',
                '-o-transform' : 'scale(1.0, 1.0)',
                'transform' : 'scale(1.0, 1.0)'
            });

            scaleFactor = 1.0;
        }
        */

        // Scale the surrounding category area to prevent scrolling
        startContainer.css({
            'height' : (startArea.height() * scaleFactor)
        });
    }

    this.updateLayout = function() {
        //that.scaleStartArea();
        //centerInParentContainer(startBtn);
        that.updatePosition();
    };

    this.onStateChanged = function(state) {
        console.log("state " + state);
        startBtn.attr("data-state", state);
        that.updateBtnState(startBtn, false);
    };
    
    this.pressing = function() {
        if(that.remoteButtonDown) {
            remote.triggerHandler("remotePressed", that.remoteButtonDown);
            this.pressedTimer = window.setTimeout(function() {that.pressing()}, this.pressedIntervall);
        }
    }

    this.init = function() {
        /**
         * bind to event handlers
         */
        eventArea.on("vmousedown", function(event) {
            if (!event.isDefaultPrevented()) {
                /*
                if (posX == null || posY == null) {
                    that.updatePosition();
                }
                */
               
                // console.log("vmousedown px " + event.pageX + " py " + event.pageY);                
                if (containsPoint((event.pageX - posX), (event.pageY - posY), centerX, centerY, radius)) {
                    event.preventDefault();
                    event.stopPropagation();
                    event.stopImmediatePropagation();

                    that.updateBtnState(startBtn, true);
                } else if(!that.isRemoteDisabled()) {
                    // Check which remote button has been pressed and update the state
                    for (var i = remoteCells.length - 1; i >= 0; i--) {

                        if (rectContainsPoint(event.pageX, event.pageY, remoteCells[i])) {
                            that.eventMouseDown = true;
                            that.updateRemoteBtnState(remoteCells[i], true);
                            //remote.triggerHandler("remotePressed", [remoteCells[i]]);
                            break;
                        }
                    }
                }
            }
        })
        
        
        eventArea.on("vmouseout", function(event) {
            if (!event.isDefaultPrevented()) {
                // console.log("vmouseout px " + event.pageX + " py " + event.pageY);                
                // Reset start Btn state
                that.updateBtnState(startBtn, false);

                that.remoteButtonDown = null;
                window.clearTimeout(this.pressedTimer);
                
                that.eventMouseDown = false;

                // Check which remote button has been pressed and update the state
                if(!that.isRemoteDisabled()) {
                    for (var i = remoteCells.length - 1; i >= 0; i--) {
                        that.updateRemoteBtnState(remoteCells[i], false);
                    }
                }
            }
        });

        eventArea.on("vmouseup", function(event) {
            if (!event.isDefaultPrevented()) {
                /*
                if (posX == null || posY == null) {
                    that.updatePosition();
                }
                */
                event.preventDefault();
                event.stopPropagation();
                event.stopImmediatePropagation();

                if (containsPoint((event.pageX - posX), (event.pageY - posY), centerX, centerY, radius)) {

                    // Trigger start Button
                    if (that.startBtnDown) {
                        startBtn.triggerHandler("startClick");
                    }
                }

                // Reset the button states
                that.updateBtnState(startBtn, false);
                
                that.eventMouseDown = false;
                
                if(that.remoteButtonDown) {
                    that.updateRemoteBtnState(that.remoteButtonDown, false);
                    that.remoteButtonDown = null;
                    window.clearTimeout(this.pressedTimer);
                }
            }
        });
        
        eventArea.on("vmousemove", function(event) {
            if (!event.isDefaultPrevented()) {
                // console.log("vmousemove px " + event.pageX + " py " + event.pageY);
                // console.log(that.remoteButtonDown)
                if(!that.isRemoteDisabled()) {
                    if(that.remoteButtonDown) {
                        // check if button is still pressed
                        if (!rectContainsPoint(event.pageX, event.pageY, that.remoteButtonDown)) {
                            that.updateRemoteBtnState(that.remoteButtonDown, false);
                            that.remoteButtonDown = null;
                            window.clearTimeout(this.pressedTimer);
                        }
                    } else if(that.remoteButtonDown == null && that.eventMouseDown) {
                        // Check which remote button has been pressed and update the state
                        for (var i = remoteCells.length - 1; i >= 0; i--) {
                            if (rectContainsPoint(event.pageX, event.pageY, remoteCells[i])) {
                                that.updateRemoteBtnState(remoteCells[i], true);
                                break;
                            }
                        }
                    }
                }
            }
        });
        
        $(window).on("resize.startArea", function() {
            that.updateLayout();
        });

        $(document).one("pageshow", function(event) {
            that.updateLayout();
        });

        $(document).on("orientationchange.startArea", function(event) {
            that.updateLayout();
        });

        // Initially update the layout
        that.updateLayout();
        
        // And the buttons
        for (var i = remoteCells.length - 1; i >= 0; i--) {
            that.updateRemoteBtnState(remoteCells[i], false);
        }
    };

    this.deinit = function() {
        startArea.off();
        $(document).off(".startArea");
        $(window).off(".startArea");
    };

    this.updateRemoteBtnState = function(element, isMouseDown) {
        var buttonName = element.attr('id');
        
        if(isMouseDown) {
            element.addClass(buttonName+"-down");
            element.removeClass(buttonName+"-up");
            if(that.remotButtonDown != element) {
                that.remoteButtonDown = element;
                that.pressing();
            }
        } else {
            element.addClass(buttonName+"-up");
            element.removeClass(buttonName+"-down");
        }
    }

    this.updateBtnState = function(divElement, isMouseDown) {
        // First remove all styling classes
        divElement.removeClass();

        var className = "img-startstopbtn";

        var state = divElement.attr("data-state");

        // If the state is set...
        if (state != undefined) {
            switch(state) {
                case "disabled":
                    that.startBtnDown = false;
                    className += " startbtn_inactive-disabled";
                    break;
                case "inactive":
                    className += " startbtn_inactive";
                    break;
                case "active":
                    className += " startbtn_active";
                    break;
                case "paused":
                    className += " startbtn_paused";
                    break;
                case "waiting":
                    that.startBtnDown = false;
                    className += " startbtn_waiting";
                    break;
            }

            if (state != "disabled" && state != "waiting") {
                className += isMouseDown ? "-down" : "-up";
                that.startBtnDown = isMouseDown;
            }
            divElement[0].className = className;
        }
    }
    
    
     this.updateTxtState = function(state) {
        
        var className = "img-robot_status";
       // var state = $("#stsImg").attr("robot-state");
		console.log("Update Text State "+ state);
		
        // If the state is set...
       // if (state != undefined) {
            switch(state) {
                case 10009:
                case 10005:
                    className = "robotsts_onbase-nn";
                    break;
                case 10002:
                case 10008:
                	className = "robotsts_cleaning-nn";
                    break;
                case 10007:
                    className = "robotsts_stop-nn";
                    break;
                case 10003:
				case 10004:
				case 10006:
					className = "robotsts_error-nn";
                    break;
                default:
                    className = "robotsts_unknown-nn";
                    break;
            }
             $('#stsImg').removeClass("robotsts_stop-nn");
             $('#stsImg').removeClass("robotsts_cleaning-nn");
             $('#stsImg').removeClass("robotsts_unknown-nn");
             $('#stsImg').removeClass("robotsts_onbase-nn");
             $('#stsImg').removeClass("robotsts_error-nn");
			console.log("Update Text State "+ className);
              $('#stsImg').addClass(className);
        //}
    }
}

