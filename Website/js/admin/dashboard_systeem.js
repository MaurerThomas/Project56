/**
 * Created by Thomas on 7-1-2015.
 */
 
 (function() {
	$webSocket.send({action: 'getLogs'});
	$webSocket.send({action: 'getCron'});
	$webSocket.receive = function($msg) {
		var $json = parseJSON($msg.data);
		if($json !== null) {
			if($json.log)
			{
				$("#logs").html($json.log);
			}else if($json.cron){
				$("#minute1").val($json.cron.minute);
				$("#hour1").val($json.cron.hour);
				$("#checkbox1").prop("checked", $json.cron.alternate);
				$("#checkbox2").prop("checked", $json.cron.cdromland);
			}
			else{
				htmlHandler($msg);
			}
		}
		
	};
	
	 $('#clearlogs').click(function()
	 {   
		if (confirm('Weet je zeker dat je de logs wilt legen?')) {
			$webSocket.send({action: 'clearLog'});
			$("#logs").html("cleared")
		}
	 });
	 $('#refreshlogs').click(function()
	 {   
		$webSocket.send({action: 'getLogs'});
	 });
	 $('#savesettings').click(function()
	 {
		if (confirm('Weet je zeker dat je deze instellingen wilt opslaan?')) {
			$webSocket.send({action: 'cronjob',minute1: $('#minute1').val()+"" , hour1: $('#hour1').val()+"" , alternate: $('#checkbox1').prop("checked"), cdromland: $('#checkbox2').prop("checked")});
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

