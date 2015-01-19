/**
 * Created by Thomas on 7-1-2015.
 */

 $('#clearlogs').click(function()
 {   
	console.log("DEZE DING");
	$webSocket.send({action: 'clearLog'});
 });
 
 

