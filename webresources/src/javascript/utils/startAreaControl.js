function StartAreaControl(startArea, categoryArea, startBtn, categoryTable, categoryCells) {
    var posX = null;
    var startBtnDown = false;
    var categoryCellDown = null;
    var posY = null;
    var radius = 32;
    var centerX = null;
    var centerY = null;
    var scaleFactor = 1.0;
    that = this;

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
        var rightEdge = leftEdge + element.outerWidth();
        var topEdge = element.offset().top;
        var bottomEdge = topEdge + element.outerWidth();

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
        radius = 0.95 * ((startBtn.width() * scaleFactor) / 2);
        posX = startArea.offset().left;
        posY = startArea.offset().top;
    };

    this.scaleStartArea = function() {
        var content = $("#startContent");

        // Scale the container
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

        // Scale the surrounding category area to prevent scrolling
        categoryArea.css({
            'height' : (categoryTable.height() * scaleFactor)
        });
    }

    this.updateLayout = function() {
        that.scaleStartArea();
        centerInParentContainer(startBtn);
        that.updatePosition();
    };

    this.onStateChanged = function(state) {
        console.log("state " + state);
        startBtn.attr("data-state", state);
        that.updateBtnState(startBtn, false);
    };

    this.init = function() {
        /**
         * bind to event handlers
         */
        startArea.on("vclick", function(event) {
            if (!event.isDefaultPrevented()) {
                if (posX == null || posY == null) {
                    that.updatePosition();
                }

                // Within the start button area
                if (containsPoint((event.pageX - posX), (event.pageY - posY), centerX, centerY, radius)) {
                    event.preventDefault();
                    event.stopPropagation();
                    event.stopImmediatePropagation();
                }
            }
        })

        startArea.on("vmousedown", function(event) {
            if (!event.isDefaultPrevented()) {
                if (posX == null || posY == null) {
                    that.updatePosition();
                }

                if (containsPoint((event.pageX - posX), (event.pageY - posY), centerX, centerY, radius)) {
                    event.preventDefault();
                    event.stopPropagation();
                    event.stopImmediatePropagation();

                    that.updateBtnState(startBtn, true);
                } else {
                    // Check which category button has been pressed and update the state
                    for (var i = categoryCells.length - 1; i >= 0; i--) {

                        if (rectContainsPoint(event.pageX, event.pageY, categoryCells[i])) {
                            that.updateCategoryBtnState(categoryCells[i], true);
                            break;
                        }
                    }
                }
            }
        })

        startArea.on("vmouseout", function(event) {
            if (!event.isDefaultPrevented()) {

                // Reset start Btn state
                that.updateBtnState(startBtn, false);

                that.categoryCellDown = null;

                // Check which category button has been pressed and update the state
                for (var i = categoryCells.length - 1; i >= 0; i--) {
                    that.updateCategoryBtnState(categoryCells[i], false);
                }
            }
        });

        startArea.on("vmouseup", function(event) {
            if (!event.isDefaultPrevented()) {
                if (posX == null || posY == null) {
                    that.updatePosition();
                }

                if (containsPoint((event.pageX - posX), (event.pageY - posY), centerX, centerY, radius)) {
                    event.preventDefault();
                    event.stopPropagation();
                    event.stopImmediatePropagation();

                    // Trigger start Button
                    if (that.startBtnDown) {
                        startBtn.triggerHandler("click");
                    }
                } else if (that.categoryCellDown) {
                    that.categoryCellDown.triggerHandler("click");
                }

                // Reset the button states
                that.updateBtnState(startBtn, false);

                // Reset category cell
                that.categoryCellDown = null;

                for (var i = categoryCells.length - 1; i >= 0; i--) {
                    that.updateCategoryBtnState(categoryCells[i], false);
                }
            }
        })

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
        for (var i = categoryCells.length - 1; i >= 0; i--) {
            that.updateCategoryBtnState(categoryCells[i], false);
        }
    };

    this.deinit = function() {
        startArea.off();
        $(document).off(".startArea");
        $(window).off(".startArea");
    };

    this.updateCategoryBtnState = function(element, isMouseDown) {

        // Get the specific elements for the category
        var categoryBorder = element.children();
        var categoryGradient = categoryBorder.children();
        var categoryBtn = categoryGradient.children();
        var categoryBtnTxt = categoryBtn.children();

        var categoryName = element.attr('id');

        // Set the correct state when disabled
        if (element.attr('aria-disabled')) {
            categoryBtnTxt.addClass("disabled");
            categoryGradient.addClass("disabled");
            categoryBorder.addClass("disabled");

            categoryBtn.removeClass(categoryName + "img-up");
            categoryBtn.addClass(categoryName + "img-disabled");
        } else {
            categoryBtn.removeClass(categoryName + "img-disabled");
            categoryBtn.addClass(categoryName + "img-up");

            categoryBorder.removeClass("disabled");
            categoryGradient.removeClass("disabled");
            categoryBtnTxt.removeClass("disabled");

            // Flip the gradient and the border
            if (isMouseDown) {
                that.categoryCellDown = categoryBtn;
                categoryBorder.addClass("flipped");
                categoryGradient.addClass("flipped");
            } else {
                categoryBorder.removeClass("flipped");
                categoryGradient.removeClass("flipped");
            }
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
                case "stop":
                    className += "stopbtn";
                    break;
            }

            if (state != "disabled") {
                className += isMouseDown ? "-up" : "-down";
                that.startBtnDown = isMouseDown;
            }

            divElement[0].className = className;
        }

    }
}

