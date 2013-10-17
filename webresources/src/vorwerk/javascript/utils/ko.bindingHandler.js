/**
 * translation handler
 * will be triggered from binding mechanism when an DOM element has the attribute data-bind:'translate: '
 * and binding has been applied from the framework
 */
ko.bindingHandlers.translate = {
    update : function(element, valueAccessor, allBindingsAccessor, viewModel, bindingContext) {
        // First get the latest data that we're bound to
        var value = valueAccessor(), allBindings = allBindingsAccessor();
        var options = {
            target : value.target || textTarget.INNERHTML
        }
        // Next, whether or not the supplied model property is observable, get its current value
        var valueUnwrapped = ko.utils.unwrapObservable(value);
        var jQElement = $(element);
        var translationKey = jQElement.attr('data-i18n');
        var translatedValue = "";
        if ( typeof viewModel.getTranslationVars == "function" && viewModel.getTranslationVars(translationKey).length > 0) {
            console.log("translation variables " + viewModel.getTranslationVars(translationKey))
            translatedValue = $.i18n.t(translationKey, {
                postProcess : 'sprintf',
                sprintf : viewModel.getTranslationVars(translationKey)
            });
        } else {
            translatedValue = $.i18n.t(translationKey);
        }
        //console.log("node " + jQElement.prop("nodeName"));
        //console.log('key ' + jQElement.attr('data-i18n') + ' translated value ' + $.i18n.t(translatedValue));
        // add translation
        switch(options.target) {
            case textTarget.INNERHTML:
                element.innerHTML = translatedValue;
                break;
            case textTarget.FIRSTCHILD:
                element.replaceChild(document.createTextNode(translatedValue), element.firstChild);
                break;
            case textTarget.VALUE:
                jQElement.val(translatedValue);
                break;
            case textTarget.PLACEHOLDER:
                jQElement.attr('placeholder', translatedValue);
                break;
            case textTarget.jqLinkButton:
                jQElement.find("span.ui-btn-text").text(translatedValue);
                break;
            case textTarget.SLIDER_LABEL_A:
                jQElement.parent().next().find("span.ui-slider-label-a").text(translatedValue);
                break;
            case textTarget.SLIDER_LABEL_B:
                jQElement.parent().next().find("span.ui-slider-label-b").text(translatedValue);
                break;
        }
    }
}
ko.bindingHandlers.jqText =  {
    update : function(element, valueAccessor, allBindingsAccessor, viewModel, bindingContext) {
        //var value = ko.utils.unwrapObservable(valueAccessor());
        var value = valueAccessor(), allBindings = allBindingsAccessor();
        if(value.node && value.node == "last") {
            element.replaceChild(document.createTextNode(value.text), element.lastChild);
        } else {
            element.replaceChild(document.createTextNode(value.text), element.firstChild);
        }
        // $('label').contents()
        // $(element).text(value);
    }
}

ko.bindingHandlers.jqLinkButtonEnable = {
    update : function(element, valueAccessor) {
        ko.bindingHandlers.enable.update(element, valueAccessor);
        var value = ko.utils.unwrapObservable(valueAccessor());
        //console.log("jqLinkButtonEnable " + value);
        value ? $(element).removeClass("ui-disabled") : $(element).addClass("ui-disabled")
    }
}

ko.bindingHandlers.jqLinkButtonText = {
    update : function(element, valueAccessor) {
        ko.bindingHandlers.enable.update(element, valueAccessor);
        var value = ko.utils.unwrapObservable(valueAccessor());
        var jQElement = $(element);
        //console.log("jqLinkButtonEnable " + value);
        jQElement.find("span.ui-btn-text").text(value);
    }
}

ko.bindingHandlers.jqButtonEnable = {
    update : function(element, valueAccessor) {
        ko.bindingHandlers.enable.update(element, valueAccessor);
        var value = ko.utils.unwrapObservable(valueAccessor());
        //console.log("jqButtonEnable " + value);
        $(element).button( value ? "enable" : "disable");
    }
}

/**
 * 
 * example: <select data-bind="jqmOptions: [property bind to]"> 
 */
ko.bindingHandlers.jqmOptions = {
    update : function(element, valueAccessor, allBindingsAccessor, context) {
        ko.bindingHandlers.options.update(element, valueAccessor, allBindingsAccessor, context);
        $(element).selectmenu("refresh");
    }
};

ko.bindingHandlers.jqRadioGroup = {
    update: function(element, valueAccessor, allBindingsAccessor, viewModel, bindingContext) {
        var value = valueAccessor();
        var currValue = ko.utils.unwrapObservable(value);
        var initialized = $(element).data( "init");
        if(initialized){
            $("input[type='radio']",element).prop( "checked", false )
                    .checkboxradio( "refresh" );
            $("input[type='radio'][value='"+currValue+"']",element)
                    .prop( "checked", true ).checkboxradio( "refresh" );
        }
    }
};

/**
 * binding to checked property for jquery mobile radio button 
 * example: <input type="radio" data-bind="checked: [property bind to], jqRadioChecked: [property bind to]" /> 
 */
ko.bindingHandlers.jqRadioChecked = {
    update : function(element, valueAccessor, allBindingsAccessor, context) {
        var value = valueAccessor();
        var valueUnwrapped = ko.utils.unwrapObservable(value);
        if (valueUnwrapped == $(element).val()) {
            $(element).prop("checked", "true").checkboxradio("refresh");
        } else {
            $(element).removeProp("checked").checkboxradio("refresh");
        }
    }
};

ko.bindingHandlers.jqRadioEnable = {
    update : function(element, valueAccessor, allBindingsAccessor, context) {
        var value = valueAccessor();
        var valueUnwrapped = ko.utils.unwrapObservable(value);
        ko.bindingHandlers.enable.update(element, valueAccessor);
        //console.log("jqRadioEnable " + valueUnwrapped);
        $(element).checkboxradio( valueUnwrapped ? "enable" : "disable");
    }
}

/**
 * binding to class attribute of rendered jquery mobile button of a select element
 * example: <select id="[!IMPORTANT!]" data-bind="jqmButtonClass:'first-button'">
 * !IMPORTANT! the select needs to have an id otherwise the rendered button couldn't be find 
 */
ko.bindingHandlers.jqmButtonClass = {
    update : function(element, valueAccessor, allBindingsAccessor, viewModel, bindingContext) {
        // First get the latest data that we're bound to
        var value = valueAccessor(), allBindings = allBindingsAccessor();
        
        var valueUnwrapped = ko.utils.unwrapObservable(valueAccessor());
        //console.log($("#"+element.id+"-button"));
        $("#"+element.id+"-button").addClass(value);
    }
};

/**
 * binding to enabaled property for jquery mobile select
 * <select data-bind="jqmOptions: cleaningType"> 
 */
ko.bindingHandlers.jqOptionsEnable = {
    update : function(element, valueAccessor) {
        ko.bindingHandlers.enable.update(element, valueAccessor);
        var value = ko.utils.unwrapObservable(valueAccessor());
        //console.log("jqOptionsEnable " + value);
        $(element).selectmenu( value ? "enable" : "disable");
    }
}

ko.bindingHandlers.jqmFlipValue = {
    update : function(element, valueAccessor, allBindingsAccessor, context) {
        ko.bindingHandlers.value.update(element, valueAccessor, allBindingsAccessor, context);
        $(element).slider("refresh");
    }
};

ko.bindingHandlers.jqmInputVisible = {
    update : function(element, valueAccessor, allBindingsAccessor, viewModel, bindingContext) {
        var value = valueAccessor(), allBindings = allBindingsAccessor();
        var valueUnwrapped = ko.utils.unwrapObservable(valueAccessor());
        $(element).parent().toggle(valueUnwrapped);
    }
}

ko.bindingHandlers.jqmInputCSS = {
    update : function(element, valueAccessor, allBindingsAccessor, viewModel, bindingContext) {
        var value = valueAccessor(), allBindings = allBindingsAccessor();
        var valueUnwrapped = ko.utils.unwrapObservable(valueAccessor());
        $.each( value, function( className, obs ) {
            var addClass = typeof obs == "function" ? obs() : obs;
            //console.log( className + ": " + addClass);
            if(addClass) {
                $(element).parent().addClass(className);
            } else {
                $(element).parent().removeClass(className);
            }
        });
        
    }
}
