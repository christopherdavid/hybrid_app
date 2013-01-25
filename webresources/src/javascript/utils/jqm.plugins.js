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

        _create : function() {
            var self = this, o = self.options, $el = self.element, isShown = false, currentType;
            
            var $contentWrapper = $('<div/>', {
				'class' : 'contentWrapper',
			}).appendTo($el);
                      
            // Append the loading spinner
            self.loadingSpinner = $("<div id='loadingSpinnerSmall'><div class='f_circleG' id='frotateG_01'></div><div class='f_circleG' id='frotateG_02'></div><div class='f_circleG' id='frotateG_03'></div><div class='f_circleG' id='frotateG_04'></div><div class='f_circleG' id='frotateG_05'></div><div class='f_circleG' id='frotateG_06'></div><div class='f_circleG' id='frotateG_07'></div><div class='f_circleG' id='frotateG_08'></div></div>").appendTo($contentWrapper);
            
            // Append the message area when spinner is invisible
            self.messageText = $("<span class='ui-notificationbar_text' style='left:0px;'>MessageText</span>").appendTo($contentWrapper);
            
            // Append the message area when spinner is visible
            self.messageTextSpinner = $("<span class='ui-notificationbar_text' style='margin-left: -90px;'>Message</span>").appendTo($contentWrapper);            
                        
            // add style
            $el.addClass("ui-notificationbar");                        
        },
        
        show : function(message, type) {
            var self = this;
            
            // Get the current header in range
            // If no header is available use the border on top for displaying the notifcationbar
            var offsetTop = 0;          
            
            switch(type){
                case notificationType.OPERATION:
                    self.loadingSpinner.show();
                    self.messageTextSpinner.show();
                    self.messageTextSpinner.text(message);
                    self.messageText.hide();
                    break;
                    
                case notificationType.HINT:
                    self.loadingSpinner.hide();
                    self.messageTextSpinner.hide();
                    self.messageText.show();
                    self.messageText.text(message);
                    setTimeout(function() {self.hide(type, false);}, 3000);
                    break;
            }          
            self.element.slideDown(400);
                        
            self.currentType = type;
            self.isShown = true;            
        },
        
        hide:function(type, force){
            var self = this;
            
            if (self.currentType == type || (force && self.currentType != notificationType.HINT)){
                self.element.slideUp(400);
            }                     
            self.currentType = null;
            self.isShown = false;
        }
        
    });
    //auto self-init widgets
    $(document).bind("ready", function(e) {
        $.mobile.notificationbar.prototype.enhanceWithin(e.target);
    });

})(jQuery);


/**
 * carousel
 *  
 */
(function($, undefined) {
    $.widget("mobile.carousel", $.mobile.widget, {
        options : {
            initSelector : ":jqmData(role='carousel')",
            collapsed : true
        },
        carouselContent : null,
        items:[],
        isUpdating:false,
        width:0,
        direction:0,
        itemIndex:1,
        messages:["0","1","2","3","4","5","6","7"],
        msgIndex:0,

        _create : function() {
            var self = this, 
                o = self.options,
                $el = self.element,
                frag = document.createDocumentFragment(),
                maxDomItems = 3;
            self.width = $el.width(); 
            
            self.carouselContent = $el.find(".ui-carousel-content");
            //TODO: add check what happens if content was there
            // check if we need to create new elements
            if(self.carouselContent.length == 0) {
                console.log("ui-carousel-content not found, create one")
                var content = document.createElement("div");
                content.className = "ui-carousel-content";
                // add items
                for(var i = 0; i < maxDomItems; i++) {
                    self.items.push(document.createElement("div"));
                    self.items[i].className = "ui-carousel-item";
                    self.items[i].style.left = ( self.width * i ) + "px";
                    self.items[i].appendChild(document.createTextNode(i));
                    content.appendChild(self.items[i]);
                }
                frag.appendChild(content);
                self.carouselContent = $el.append(frag).children(".ui-carousel-content");
            // check if all items were set and set correct position
            } else {
                self.items = $el.find(".ui-carousel-item").map(function(index, domEle) {
                    domEle.style.left = ( self.width * index ) + "px";
                    if(index == maxDomItems) {
                        return false
                    };
                    return $(domEle);
                })
            }
            self.carouselContent.on( "transitionend webkitTransitionEnd oTransitionEnd MSTransitionEnd", function() {  
                console.log("transitionend")
                self.updateItems();
            });
        },
        
        updateItems:function() {
            var self = this, remItem;
            if(!self.isUpdating) {
                self.isUpdating = true;
                // direction could be negative depending on move direction
                remItem = self.itemIndex + self.direction;
               
                // create new position order according to itemIndex
                $.each(self.items, function(index, domEle) {
                    // 1 is the indicator which means index equals self.itemIndex (visible item)
                    var posIndex = 1 + index - self.itemIndex;
                    if(posIndex < 0) {
                        posIndex = posIndex + self.items.length;
                    }  else if(posIndex >= self.items.length) {
                        posIndex = posIndex - self.items.length;
                    }
                    domEle.style.left = ( self.width * posIndex ) + "px";
                    
                    
                    // TODO: update content of item
                    if(self.direction > 0) {
                        //self.messages[self.msgIndex - 1]
                    } else if (self.direction < 0) {
                        //self.messages[self.msgIndex + 1]
                    // update all items
                    } else {
                        //self.messages[self.msgIndex - 1]
                    }
                    
                });
                self.carouselContent.css({"transform":"",
                            "transition-duration":"",
                            });
                
                // reset direction
                self.direction = 0;
                self.isUpdating = false;
            }
        },
        
        next:function() {
            this.itemIndex = this.itemIndex-- > 0 ? this.itemIndex-- : (this.items.length - 1);
            this.direction = -1;
            console.log("width " + this.width + " new itemIndex " + this.itemIndex);
            this.carouselContent.css({"transform":"translate(" + this.width + "px,0px)",
                        "transition-duration":"250ms",
                        });
        },
        
        previous:function() {
            this.itemIndex = this.itemIndex++ < (this.items.length - 1) ? this.itemIndex++ : 0;
            this.direction = 1;
            console.log("width " + this.width + " new itemIndex " + this.itemIndex);
            this.carouselContent.css({"transform":"translate(-" + this.width + "px,0px)",
                        "transition-duration":"250ms",
                        });
        }
        
        
    });
    //auto self-init widgets
    $(document).bind("pagecreate create", function(e) {
        $.mobile.carousel.prototype.enhanceWithin(e.target);
    });

})(jQuery);