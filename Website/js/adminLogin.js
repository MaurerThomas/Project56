$(function() {
	var $formButton = $('form input[type="submit"]'),
	$compatibility = checkCompatibility($formButton),
	$loginWarning = $('form p.hidden');
	if($compatibility.WebSocket) {
		$webSocket.init('145.24.222.119','8081','/admin');
		$webSocket.receive = function($msg) {
			var $json = JSON.parse($msg.data);
			if(!$json.login) {
				$loginWarning.removeClass('hidden').fadeIn({
					complete: function() {
						$loginWarning.fadeOut(2000);
					}
				});
				$formButton.attr('disabled',false);
			} else {
				$webSocket.receive = htmlHandler;
			}
		};
	}
	$('form').submit(function($e) {
		$e.preventDefault();
		$formButton.attr('disabled',true);
		$webSocket.send({
			login: {
				username: $('input[name="username"]').val(),
				password: $('input[name="password"]').val()
			}
		});
	});
});