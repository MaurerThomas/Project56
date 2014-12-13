function htmlHandler($msg) {
	var $json = parseJSON($msg.data),
	$replacements = {},
	$templates = {};
	if($json === null) {
		return;
	}
	if($json.replace !== undefined) {
		handleReplace($json.replace);
	}
	if($json.html !== undefined) {
		handleJQuery($json.html,'html');
	}
	if($json.text !== undefined) {
		handleJQuery($json.text,'text');
	}

	function parseJSON($str) {
		var $json = null;
		try {
			$json = JSON.parse($str);
		} catch($er) {}
		return $json;
	}

	function handleJQuery($json,$function) {
		for(var $key in $json) {
			$($key)[$function](parseKey($json[$key]));
		}
	}

	function parseKey($key) {
		var $json = parseJSON($key);
		if($json === null || $json.template !== undefined) {
			return getTemplate($json.template);
		}
		return $key;
	}

	function getTemplate($template) {
		if($templates[$template] === undefined) {
			$.ajax({
				type: 'GET',
				url: './templates/'+$template+'.html',
				async: false
			}).success(function($responseText) {
				$templates[$template] = $responseText;
			});
		}
		return parseTemplate($templates[$template]);
	}

	function parseTemplate($template) {
		var $html = $($template), $element;
		$html.find('[data-replace]').each(function() {
			$element = $(this);
			$element.html($replacements[$element.attr('data-replace')]);
		});
		return $html;
	}

	function handleReplace($json) {
		for(var $key in $json) {
			$replacements[$key] = $json[$key];
		}
	}
}