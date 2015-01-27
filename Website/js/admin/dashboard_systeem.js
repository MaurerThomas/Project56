/**
 * Created by Thomas on 7-1-2015.
 */
 
 (function() {
	$webSocket.send({action: 'getLogs'});
	$webSocket.receive = function($msg) {
		var $json = parseJSON($msg.data);
		if($json !== null) {
			if($json.log)
			{
				$("#logs").html($json.log);
			} else{
				htmlHandler($msg);
			}
		}
		
	};
	
	 $('#clearlogs').click(function()
	 {   
		if (confirm('Weet je zeker dat je de logs wilt legen?')) {
			$webSocket.send({action: 'clearLog'});
			$("#logs").html("cleared")
		} else {
		}
		
		
	 });
	 
	 $('#savesettings').click(function()
	 {
		if (confirm('Weet je zeker dat je deze instellingen wilt opslaan?')) {
			$webSocket.send({action: 'cronjob',minute1: $('#minute1').val()+"" , hour1: $('#hour1').val()+"" , alternate: $('#checkbox1').checked, cdromland: $('#checkbox2').checked});
		} else {
		}
		console.log("Save");
	 });

	
	function parseJSON($str) {
		try {
			return JSON.parse($str);
		} catch($e) {}
		return null;
	}
})();

