function htmlHandler($msg) {
	var $json = parseJSON($msg.data);
	if($json === null) {
		return;
	}
	if($json.html !== undefined) {
		handleHTML($json.html);
	}

	function parseJSON($str) {
		var $json = null;
		try {
			$json = JSON.parse($str);
		} catch($er) {}
		return $json;
	}

	function handleHTML($json) {
		for(var $key in $json) {
			$($key).html($json[$key]);
		}
	}
}