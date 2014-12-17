$(function() {
	var $formButton = $('form input[type="submit"]'),
	$compatibility = checkCompatibility($formButton),
	$wrongLogin = $('#wrongLogin'),
	$noLogin = $('#noLogin');
	window.location.hash = '';
	if($compatibility.WebSocket) {
		$webSocket.init('145.24.222.119','8081','/admin');
		$webSocket.receive = function($msg) {
			var $json = JSON.parse($msg.data);
			$formButton.attr('disabled',false);
			if(!$json.login) {
				flashWarning($wrongLogin);
			} else {
				$('a.navbar-brand').attr('href','#');
				$webSocket.receive = htmlHandler;
			}
		};
	}
	$('form').submit(function($e) {
		var $username = $('input[name="username"]').val(),
		$password = $('input[name="password"]').val();
		$e.preventDefault();
		if($username === '' || $password === '') {
			flashWarning($noLogin);
			return;
		}
		$formButton.attr('disabled',true);
		$webSocket.send({
			login: {
				username: $username,
				password: $password
			}
		});
	});

	function flashWarning($loginWarning) {
		$loginWarning.removeClass('hidden').fadeIn({
			complete: function() {
				$loginWarning.fadeOut(2000);
			}
		});
	}
});