/**
 * Created by Thomas on 7-1-2015.
 */

 $('#clearlogs').click(function()
 {   
	console.log("DEZE DING");
	$webSocket.send({action: 'clearLog'});
 });
 $('#savesettings').click(function()
 {
	console.log("Save");
	$webSocket.send({action: 'cronjob',minute: $('minute1').val() , hour: $('hour1').val(), alternate: $('checkbox1').checked, cdromland: $('checkbox2').checked});
 });

