function htmlHandler($msg) {
	var $json = parseJSON($msg.data),
	$replacements = {};
	if($json === null) {
		return;
	}
	if($json.replace !== undefined) {
		handleReplace($json.replace);
	}
	if($json.html !== undefined) {
		handleHTML($json.html);
	}
	if($json.text !== undefined) {
		handleText($json.text);
	}

	function parseJSON($str) {
		var $json = null;
		try {
			$json = JSON.parse($str);
		} catch($er) {}
		return $json;
	}

	function handleHTML($json) {
		handleJQuery($json,'html');
	}
	function handleText($json) {
		handleJQuery($json,'text');
	}
	function handleJQuery($json,$function) {
		for(var $key in $json) {
			$($key)[$function](parseKey($json[$key]));
		}
	}
	function parseKey($key) {
		return $key;
	}
	function handleReplace($json) {
		for(var $key in $json) {
			$replacements[$key] = $json[$key];
		}
	}
}