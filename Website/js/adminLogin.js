$(function() {
	var $compatibility = checkCompatibility($('form input[type="submit"]')),
	$loginWarning = $('form p.hidden');
	if($compatibility.WebSocket) {
		$webSocket.init('145.24.222.119','8081','/admin');
		$webSocket.receive = function($msg) {
			var $json = JSON.parse($msg.data);
			if(!$json.login) {
				$loginWarning.removeClass('hidden').fadeIn({complete: function() {$loginWarning.fadeOut(2000);}});
			}
		};
	}
	$('form').submit(function($e) {
		$e.preventDefault();
		$webSocket.send({
			login: {
				username: $('input[name="username"]').val(),
				password: $('input[name="password"]').val()
			}
		});
	});
});