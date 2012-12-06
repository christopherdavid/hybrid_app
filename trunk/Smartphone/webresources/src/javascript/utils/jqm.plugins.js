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
            var self = this, o = self.options, $el = self.element, header = $el.closest(":jqmData(role='header')");
            // wrap html elements of notificationbar to content div (needed to toggle visibility)
            self.notificationbarContent = $el.wrapInner("<div class='ui-notificationbar-content'></div>").children(".ui-notificationbar-content"),
            // remove borders of the outer header element
            header.addClass("ui-header-noborder");
            // add style
            $el.addClass("ui-notificationbar");
            
            /**
             * bind to event handler for expand/collapse events
             */
            $el.bind("expand collapse", function(event) {
                if (!event.isDefaultPrevented()) {
                    var $this = $(this), isCollapse = (event.type === "collapse" );
                    event.preventDefault();

                    $this.toggleClass("ui-notificationbar-collapsed", isCollapse);
                    self.notificationbarContent.toggleClass("ui-notificationbar-content-collapsed", isCollapse).attr("aria-hidden", isCollapse);
                    // trigger update event because page layout has changed
                    self.notificationbarContent.trigger("updatelayout");
                }
            })
            // set initial state of notificationbar
            $el.trigger(o.collapsed ? "collapse" : "expand");
        }
    });
    //auto self-init widgets
    $(document).bind("pagecreate create", function(e) {
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


/**
 * polygon button
 * first version only supports circle 
 * hard coded class names and button center point, radius
 */
(function($, undefined) {
    $.widget("mobile.polybutton", $.mobile.widget, {
        options : {
            initSelector : ":jqmData(role='polybutton')"
        },
        posX:null,
        posY:null,
        width:0,
        height:0,
        radius:73,
        centerX:82,
        centerY:81,

        _create : function() {
            var self = this, o = self.options, $el = self.element;
            self.width = $el.width();
            self.height = $el.height();
            /**
             * bind to event handler
             */
            $el.bind("vclick", function(event) {
                if (!event.isDefaultPrevented()) {
                    if(self.posX == null || self.posY == null ) {
                        self.updatePosition();
                    }
                    
                    // console.log("click position in element X:" + (event.pageX - self.posX) + " Y: " + (event.pageY - self.posY));
                    if(self.containsPoint((event.pageX - self.posX), (event.pageY - self.posY),
                        self.centerX, self.centerY, self.radius )) {
                            event.preventDefault();
                            event.stopPropagation();
                            event.stopImmediatePropagation();
                    } else {
                       console.log("contains ? false");
                    }
                }
            })
            
            
            $el.bind("vmousedown", function(event) {
                if (!event.isDefaultPrevented()) {
                    if(self.posX == null || self.posY == null ) {
                        self.updatePosition();
                    }
                    
                    if(self.containsPoint((event.pageX - self.posX), (event.pageY - self.posY),
                        self.centerX, self.centerY, self.radius )) {
                            event.preventDefault();
                            event.stopPropagation();
                            event.stopImmediatePropagation();
                            $el.removeClass("img-startbtn-up").addClass("img-startbtn-down");
                    }
                }
            })
            
            $el.bind("vmouseup", function(event) {
                if (!event.isDefaultPrevented()) {
                    if(self.posX == null || self.posY == null ) {
                        self.updatePosition();
                    }
                    
                    if(self.containsPoint((event.pageX - self.posX), (event.pageY - self.posY),
                        self.centerX, self.centerY, self.radius )) {
                            event.preventDefault();
                            event.stopPropagation();
                            event.stopImmediatePropagation();
                            $el.removeClass("img-startbtn-down").addClass("img-startbtn-up");
                    }
                }
            })
            
            
            $el.bind("orientationchange", function(event){
                // console.log("orientation changed to " + event.orientation)
                self.updatePosition();
            });
        },
        updatePosition:function() {
            this.posX = this.element.offset().left;
            this.posY = this.element.offset().top;
        },
        // checks if a circle contains the point x,y
        containsPoint:function(x, y, cx, cy, r) {
            var dx = x-cx
            var dy = y-cy
            return dx*dx+dy*dy <= r*r
        }
    
    });
    
    //auto self-init widgets
    $(document).bind("pagecreate create", function(e) {
        $.mobile.polybutton.prototype.enhanceWithin(e.target);
    });

})(jQuery);