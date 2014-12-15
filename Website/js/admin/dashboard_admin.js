$webSocket.receive = function($msg) {
	var $json = parseJSON($msg.data);
	if($json !== null) {
		handleJSON($json);
	}

	function handleJSON($json) {
		var $admins,$aid;
		if($json.admins !== undefined) {
			$admins = $('#admins');
			$admins.empty();
			for($aid in $json.admins) {
				addAdmin($json.admins[$aid],$aid);
			}
		}
	}

	function addAdmin($admin,$aid) {
	}

	function parseJSON($str) {
		try {
			return JSON.parse($str);
		} catch($e)
		return null;
	}
};
$webSocket.send({action: 'getAdmins'});