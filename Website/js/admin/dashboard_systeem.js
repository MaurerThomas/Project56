/**
 * Created by Thomas on 7-1-2015.
 */

function clearLog()
{
    $webSocket.send({action: 'clearLog'});
}

