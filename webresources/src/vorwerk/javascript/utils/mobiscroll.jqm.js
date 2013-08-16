(function ($) {

    $.mobiscroll.themes.jqm = {
        defaults: {
            jqmBorder: 'a',
            jqmBody: 'c',
            jqmHeader: 'b',
            jqmWheel: 'd',
            jqmClickPick: 'c',
            jqmSet: 'b',
            jqmCancel: 'c'
        },
        init: function (elm, inst) {
            var s = inst.settings;
            $('.dw', elm).removeClass('dwbg');
            /*
            $('.dw', elm).removeClass('dwbg').addClass('ui-overlay-shadow ui-corner-all ui-body-' + s.jqmBorder);
            // ok button in dialog
            $('.dwb-s .dwb', elm).attr('data-role', 'button').attr('data-mini', 'true').attr('data-theme', s.jqmSet);
            
            $('.dwb-n .dwb', elm).attr('data-role', 'button').attr('data-mini', 'true').attr('data-theme', s.jqmCancel);
            // cancel button in dialog
            $('.dwb-c .dwb', elm).attr('data-role', 'button').attr('data-mini', 'true').attr('data-theme', s.jqmCancel);
            // plus and minus buttons
            $('.dwwb', elm).attr('data-role', 'button').attr('data-theme', s.jqmClickPick);
            // headerbar in dialog
            $('.dwv', elm).addClass('ui-header ui-bar-' + s.jqmHeader);
            // container
            $('.dwwr', elm).addClass('ui-body-' + s.jqmBody);
            // wheel plus
            $('.dwpm .dwwl', elm).addClass('ui-body-' + s.jqmWheel);
            // wheel minus
            $('.dwpm .dwl', elm).addClass('ui-body-' + s.jqmBody);
            */
            elm.trigger('create');
            // Hide on overlay click
            $('.dwo', elm).click(function () { inst.cancel(); });
        }
    };

})(jQuery);
