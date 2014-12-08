$(function() {
	var $compatibility = checkCompatibility($('form input[type="submit"]'));
	if($compatibility.WebSocket) {
		$webSocket.init('145.24.222.119','8081','/admin');
		$webSocket.receive = function($msg) {
			console.log($msg);
		};
	}
	$('form').submit(function($e) {
		$e.preventDefault();
		var $username = $('input[name="username"]').val(),
		$password = $('input[name="password"]').val();
		$webSocket.send({
			login: {
				username: $username,
				password: $password
			}
		});
	});
});