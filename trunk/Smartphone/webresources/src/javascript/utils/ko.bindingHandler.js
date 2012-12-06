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
        var translatedValue ="";
        if(typeof viewModel.getTranslationVars == "function" && viewModel.getTranslationVars(translationKey).length > 0) {
            console.log("translation variables " + viewModel.getTranslationVars(translationKey))
            translatedValue = $.i18n.t(translationKey,{postProcess:'sprintf', sprintf: viewModel.getTranslationVars(translationKey)});
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
        }
    }
}

ko.bindingHandlers.jqLinkButtonEnable = {
    update: function(element, valueAccessor) {
        ko.bindingHandlers.enable.update(element, valueAccessor);
        var value = ko.utils.unwrapObservable(valueAccessor());
        //console.log("jqLinkButtonEnable " + value);
        value ? $(element).removeClass("ui-disabled") : $(element).addClass("ui-disabled")
    }
}

ko.bindingHandlers.jqLinkButtonText = {
    update: function(element, valueAccessor) {
        ko.bindingHandlers.enable.update(element, valueAccessor);
        var value = ko.utils.unwrapObservable(valueAccessor());
        var jQElement = $(element);
        //console.log("jqLinkButtonEnable " + value);
        jQElement.find("span.ui-btn-text").text(value);
    }
}

ko.bindingHandlers.jqButtonEnable = {
    update: function(element, valueAccessor) {
        ko.bindingHandlers.enable.update(element, valueAccessor);
        var value = ko.utils.unwrapObservable(valueAccessor());
        //console.log("jqButtonEnable " + value);
        $(element).button(value ? "enable" : "disable");
    }
}