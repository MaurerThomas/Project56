(function($) {
	$(function() {
		$('#start a.btn').click(function($e) {
			$e.preventDefault();
			$('#pcbuilder').removeClass();
			$('#start').hide();
		});
	});
	$(document).tooltip({position: {my: 'left top', at: 'right top'}});
})(jQuery);