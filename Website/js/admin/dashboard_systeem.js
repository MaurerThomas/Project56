/**
 * Created by Thomas on 7-1-2015.
 */

 $('#clearlogs').click(function()
 {   
	$webSocket.send({action: 'clearLog'});
 });
 
 

