/**
 * notificationbar is just an container which could toggle visibility of it's
 * content.
 * listens to the events: 'expand' and 'collapse' to toggle content
 */
(function($, undefined) {
    $.widget("mobile.notificationbar", $.mobile.widget, {
        options : {
            initSelector : ":jqmData(role='notificationbar')",
            collapsed : true
        },
        notificationbarContent : null,
        timer : null,
        animationTimer : null,
        isShown : false,

        _create : function() {
            var self = this, o = self.options, $el = self.element, currentType;

            self.contentWrapper = $('<div/>', {
                'class' : 'contentWrapper',
            }).appendTo($el);
            
            // Append the message icon area
            self.messageIcon = $("<div id='ui-notificationbar_icon'></div>").appendTo(self.contentWrapper);
            
            // Append the loading spinner
            self.loadingSpinner = $("<div id='loadingSpinnerSmall' class='display:block'><div class='f_circleG' id='frotateG_01'></div><div class='f_circleG' id='frotateG_02'></div><div class='f_circleG' id='frotateG_03'></div><div class='f_circleG' id='frotateG_04'></div><div class='f_circleG' id='frotateG_05'></div><div class='f_circleG' id='frotateG_06'></div><div class='f_circleG' id='frotateG_07'></div><div class='f_circleG' id='frotateG_08'></div></div>").appendTo(self.messageIcon);

            // Append the message area when spinner is invisible
            self.messageText = $("<span class='ui-notificationbar_text'>MessageText</span>").appendTo(self.contentWrapper);

            // add style
            $el.addClass("ui-notificationbar");
        },

        show : function(message, type) {
            var self = this;

            // Get the current header in range
            // If no header is available use the border on top for displaying the notifcationbar
            var offsetTop = 0;

            if (self.isShown) {
                window.clearTimeout(self.animationTimer);
            }
            // remove all old classes
            self.contentWrapper[0].className = "contentWrapper";
            
            console.log(self.contentWrapper);
            switch(type) {
                case notificationType.OPERATION:
                    self.contentWrapper.addClass("icon spinner");
                    self.messageText.text(message);
                    break;
                case notificationType.GETREADY:
                    self.contentWrapper.addClass("icon getready");
                    self.messageText.text(message);
                    break;
                case notificationType.WAKEUP:
                    self.contentWrapper.addClass("icon wakeup");
                    self.messageText.text(message);
                    break;
                case notificationType.HINT:
                    window.clearTimeout(self.timer);
                    self.messageText.text(message);
                    break;
            }
            if (!self.isShown) {
                self.element.slideDown(400);
            }
            self.currentType = type;
            self.isShown = true;
        },

        hide : function(force) {
            var self = this;
            // stay at least 200ms visible before closing animation is triggered cause of following messages
            if(force == true) {
                self.hideAnimation();
            } else {
                self.animationTimer = window.setTimeout(function() {
                    self.hideAnimation();
                }, 200);
            }
        },
        hideAnimation : function() {
            var self = this;
            self.element.slideUp(400);
            self.currentType = null;
            self.isShown = false;
        }
    });
    //auto self-init widgets
    $(document).one("frameworksReady", function(e) {
        $.mobile.notificationbar.prototype.enhanceWithin(e.target);
    });

})(jQuery);
