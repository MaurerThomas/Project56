(function($) {
	$(function() {
		var $button = $('#start a.btn'),
		$compatibility = compatibilityCheck($button);
		$button.click(function($e) {
			$e.preventDefault();
			$('#pcbuilder').removeClass();
			$('#start').hide();
		});
		if($compatibility.WebSocket) {
			$webSocket.init('145.24.222.119','8080','/search');
		}
	});
	$(document).tooltip({position: {my: 'left top', at: 'right top'}});
})(jQuery);