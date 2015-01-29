(function($) {
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
		if (confirm('Weet u zeker dat u de logs wilt legen?')) {
			$webSocket.send({action: 'clearLog'});
			$("#logs").html("Het log is geleegd.");
		}
	});
	$('#refreshlogs').click(function()
	{   
		$webSocket.send({action: 'getLogs'});
	});
	$('#savesettings').click(function()
	{
		if (confirm('Weet u zeker dat u deze instellingen wilt opslaan?')) {
			$webSocket.send({action: 'cronjob',minute1: $('#minute1').val()+"" , hour1: $('#hour1').val()+"" , alternate: $('#checkbox1').prop("checked"), cdromland: $('#checkbox2').prop("checked")});
		}
	});
	
	function parseJSON($str) {
		try {
			return JSON.parse($str);
		} catch($e) {
			return null;
		}
	}
})(jQuery);