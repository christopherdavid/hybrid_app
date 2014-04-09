function StartAreaControl(startArea, startContainer,eventArea, startBtn, remote, remoteCells) {
    var posX = null;
    var startBtnDown = false;
    this.remoteButtonDown = null;
    var posY = null;
    var radius = deviceSize.getResolution() == "high" ?  109 : 73;
    var centerX = null;
    var centerY = null;
    
    this.isRemoteEnabled = ko.observable(true);
    this.eventMouseDown = false;
    that = this;
    
    // trigger pressed event every x ms
    this.pressedIntervall = 500;
    this.pressedTimer;
    

    
    // Checks if a specific dom element contains a point x,y
    var rectContainsPoint = function(x, y, element) {
        var leftEdge = element.offset().left;
        var rightEdge = leftEdge + element.outerWidth();
        var topEdge = element.offset().top;
        var bottomEdge = topEdge + element.outerHeight();
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
        var dx = x - cx, dy = y - cy;
        return dx * dx + dy * dy <= r * r;
    };

    this.updatePosition = function() {
        //console.log("update position")
        
        centerX = startArea.width() / 2;
        centerY = startArea.height()/ 2;
        
        posX = startArea.offset().left;
        posY = startArea.offset().top;
        
        // console.log("x: " + (centerX + posX));
        // console.log("y: " + (centerY + posY)) ;
    };


    this.updateLayout = function() {
        that.updatePosition();
    };

    this.onStateChanged = function(state) {
        console.log("state " + state);
        startBtn.attr("data-state", state);
        that.updateBtnState(startBtn, false);
    };
    
    this.pressing = function() {
        // added check if remote is enabled to avoid an event loop on error 
        if(that.remoteButtonDown && that.isRemoteEnabled()) {
            remote.triggerHandler("remotePressed", that.remoteButtonDown);
            this.pressedTimer = window.setTimeout(function() {that.pressing();}, this.pressedIntervall);
        } else {
            remote.triggerHandler("remoteReleased", that.remoteButtonDown);
        }
    };

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
                
                // FIX to get more than two move events:
                // Google Chrome will fire a touchcancel event about 200 milliseconds after touchstart if it thinks the user is panning/scrolling and you do not call event.preventDefault().
                event.preventDefault();
               
                // console.log("vmousedown px " + event.pageX + " py " + event.pageY);                
                if (containsPoint((event.pageX - posX), (event.pageY - posY), centerX, centerY, radius)) {
                    event.preventDefault();
                    event.stopPropagation();
                    event.stopImmediatePropagation();

                    that.updateBtnState(startBtn, true);
                } else if(that.isRemoteEnabled()) {
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
        });
        
        
        eventArea.on("vmouseout", function(event) {
            if (!event.isDefaultPrevented()) {
                // console.log("vmouseout px " + event.pageX + " py " + event.pageY);                
                // Reset start Btn state
                that.updateBtnState(startBtn, false);

                that.remoteButtonDown = null;
                window.clearTimeout(this.pressedTimer);
                
                that.eventMouseDown = false;

                // Check which remote button has been pressed and update the state
                if(that.isRemoteEnabled()) {
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
                if(that.isRemoteEnabled()) {
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
            element.addClass("ui-down");
            element.removeClass("ui-up");
            if(that.remotButtonDown != element) {
                that.remoteButtonDown = element;
                that.pressing();
            }
        } else {
            element.addClass("ui-up");
            element.removeClass("ui-down");
        }
    };

    this.updateBtnState = function(divElement, isMouseDown) {
        // First remove all styling classes
        divElement.removeClass();
        var className = "";
        var state = divElement.attr("data-state");
        
        if (state != "disabled" && state != "waiting") {
            className = isMouseDown ? "startBtn-down" : "startBtn-up";
            that.startBtnDown = isMouseDown;
        }
        divElement[0].className = className;
    };
}

