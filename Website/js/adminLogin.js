$(function() {
	var $compatibility = compatibilityCheck($('form input[type="submit"]'));
	if($compatibility.WebSocket) {
		$webSocket.init('145.24.222.119','8081','/admin');
		$webSocket.receive = function($msg) {
			console.log($msg);
		};
	}
	$('form').submit(function($e) {
		$e.preventDefault();
		var $username = $('input name="username"'),
		$password = $('input name="password"');
		$webSocket.send({
			login: {
				username: $username,
				password: $password
			}
		});
	});
});