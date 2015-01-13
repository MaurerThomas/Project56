$(function() {
	var $formButton = $('form input[type="submit"]'),
	$compatibility = checkCompatibility($formButton),
	$wrongLogin = $('#wrongLogin'),
	$noLogin = $('#noLogin'),
	$loginMenu = $('#menu');

	window.location.hash = '';

	if($compatibility.WebSocket) {
		$webSocket.init(window.location.host,'8081','/admin');
		$webSocket.receive = function($msg) {
			var $json = JSON.parse($msg.data);
			$formButton.attr('disabled',false);
			if(!$json.login) {
				flashWarning($wrongLogin);
			} else {
				$('a.navbar-brand').attr('href','#');
				$webSocket.receive = htmlHandler;
				$loginMenu.removeClass('hidden');
				$loginMenu.hide().show(400);
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