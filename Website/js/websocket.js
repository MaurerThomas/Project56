var $webSocket = (function() {
	var $initialised = false,
	$ws = null;

	function init($address,$port,$path) {
		var $protocol = 'ws';
		if(!$initialised) {
			if(window.location.protocol == 'https:') {
				$protocol += 's';
			}
			$ws = new WebSocket($protocol+'://'+$address+':'+$port+$path);
			$ws.onmessage = function($msg) {
				$webSocket.receive($msg);
			};
			$ws.onclose = function($msg) {
				$webSocket.receiveClose($msg);
			};
			$initialised = true;
		}
	}

	function send($json) {
		$ws.send(JSON.stringify($json));
	}

	return {
		send: send,
		receive: function() {},
		receiveClose: function() {},
		init: init
	};
})();