$(function() {
	var $websocket = check('WebSocket'), $JSON = check('JSON'), $localStorage = check('localStorage'), $button = $('#start a.btn');

	if(!$localStorage) {
		addWarning('Uw browser ondersteund geen LocalStorage of uw cookies staan uit. Als u de pagina verlaat gaan uw geselecteerde onderdelen verloren.');
	}
	if(!$websocket) {
		addError('Uw browser ondersteund geen WebSockets.');
	}
	if(!$JSON) {
		addError('Uw browser understeund geen JSON.');
	}

	function addError($message) {
		$button.before('<p class="text-danger"><span class="glyphicon glyphicon-warning-sign"></span> '+$message+'</p>');
		$button.attr('disabled',true);
	}

	function addWarning($message) {
		$button.before('<p class="text-warning"><span class="glyphicon glyphicon-warning-sign"></span> '+$message+'</p>');
	}


	function check($property) {
		try {
			return (window[$property] !== undefined && window[$property] !== null);
		} catch($e) {}
		return false;
	}
});