/**
 * A jQuery wrapper for the iScroll plugin. 
 * Feel free to add features and fix bugs!
 * 
 * Use:
 * $('#myDiv').makeScrollable({
			hScrollbar : false,
			vScrollbar : false,
			vScroll : false,
			hScroll : true,
		});
 * 
 * @param iScrollOptions the options similar to the options of the iScroll library
 *
 */

function calcSize($content) {
	var width = 0;

	$content.children().each(function() {
		width += $(this).outerWidth(true);
	});

	$content.width(width + 'px');
}

(function($) {
	$.fn.makeScrollable = function(iScrollOptions) {
		return this.each(function() {
			var $content = $(this);
			
			var uniqueNum = Math.floor(Math.random() * 99999);
			var uniqueName = 'scrollerWrapper-' + uniqueNum;
			var $wrapper = $(this).wrap('<div id="' + uniqueName + '" />');

			// prevent magic margins for inline-block elements
			$wrapper.css('font-size', 0);

			var colorScroller = new iScroll(uniqueName, iScrollOptions);

			$(document).one("pageshow", function(e) {
				calcSize($content);
				colorScroller.refresh();
			});

		});

	};

})(jQuery);