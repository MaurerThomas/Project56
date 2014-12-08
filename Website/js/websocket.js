var $webSocket = (function() {
	var $initialised = false,
	$webSocket = null;

	function init($address,$port,$path) {
		var $protocol = 'ws';
		if(!$initialised) {
			if(window.location.protocol == 'https:') {
				$protocol += 's';
			}
			$webSocket = new WebSocket($protocol+'://'+$address+':'+$port+$path);
			$webSocket.onmessage = function($msg) {
				this.receive($msg);
			};
			$webSocket.onclose = function($msg) {
				this.receiveClose($msg);
			};
			$initialised = true;
		}
	}

	function send($json) {
		$webSocket.send(JSON.stringify($json));
	}

	return {
		send: send,
		receive: function() {},
		receiveClose: function() {},
		init: init
	};
})();