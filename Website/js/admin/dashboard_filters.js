(function() {
	$webSocket.send({action: 'init'});
	$webSocket.receive = function($msg) {
		var $json = parseJSON($msg.data);
		if($json !== null) {
			if(!handleJSON($json)) {
				htmlHandler($msg);
			}
		}
	};
	function handleJSON($json) {
			return getProcessors($json);
	}
	
	function getProcessors($json) {
		console.log($json.processors);
	}
	
	function parseJSON($str) {
		try {
			return JSON.parse($str);
		} catch($e) {}
		return null;
	}
})();