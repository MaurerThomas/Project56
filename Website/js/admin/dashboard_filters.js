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
	
})();