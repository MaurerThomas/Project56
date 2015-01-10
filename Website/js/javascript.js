(function($) {
	$(function() {
		var $button = $('#start a.btn'),
		$compatibility = checkCompatibility($button);
		$button.click(function($e) {
			$e.preventDefault();
			initFilters();
			$('#pcbuilder').removeClass();
			$('#start').hide();
		});
		if($compatibility.WebSocket) {
			$webSocket.init(window.location.host,'8080','/search');
		}
		$('#pcbuilder-selection-header h2 a.btn').click(function($e) {
			var $btn = $(this);
			$e.preventDefault();
			if($btn.hasClass('glyphicon-chevron-up')) {
				$btn.removeClass('glyphicon-chevron-up');
				$btn.addClass('glyphicon-chevron-down');
				$btn.attr('title','Geef uw selectie weer');
			} else {
				$btn.removeClass('glyphicon-chevron-down');
				$btn.addClass('glyphicon-chevron-up');
				$btn.attr('title','Verberg uw selectie');
			}
			$('#pcbuilder-selection').animate({height: 'toggle'});
		});
	});
	$(document).tooltip({position: {my: 'left top', at: 'right top'}});
})(jQuery);