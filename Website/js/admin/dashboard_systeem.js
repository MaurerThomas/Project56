/**
 * Created by Thomas on 7-1-2015.
 */
 
 (function() {
	$webSocket.send({action: 'getLogs'});
	console.log("getlogs")
	$webSocket.receive = function($msg) {
		console.log("received logs")
		var $json = parseJSON($msg.data);
		if($json !== null) {
			alert($json);
			$("#logs").html($json);
		}
	};
	 $('#clearlogs').click(function()
	 {   
		console.log("DEZE DING");
		$webSocket.send({action: 'clearLog'});
		$("#logs").html("cleared")
	 });
	 $('#savesettings').click(function()
	 {
		console.log("Save");
		$webSocket.send({action: 'cronjob',minute: $('minute1').val() , hour: $('hour1').val(), alternate: $('checkbox1').checked, cdromland: $('checkbox2').checked});
	 });

	
	function parseJSON($str) {
		try {
			return JSON.parse($str);
		} catch($e) {}
		return null;
	}
})();

