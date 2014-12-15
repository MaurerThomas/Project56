$webSocket.receive = function($msg) {
	var $json = parseJSON($msg.data);
	if($json !== null) {
		if(!handleJSON($json)) {
			htmlHandler($msg);
		}
	}

	function handleJSON($json) {
		var $admins,$aid;
		if($json.admins !== undefined) {
			$admins = $('#admins');
			$admins.empty();
			for($aid in $json.admins) {
				$admins.append(addAdmin($json.admins[$aid],$aid));
			}
			return true;
		}
		return false;
	}

	function addAdmin($admin,$aid) {
		var $div,$button,$out = $('<div class="row">');
		$out.append('<div class="name col-sm-10">'+$admin+'</div>');
		$div = $('<div class="col-sm-1"></div>');
		$button = $('<a href="#adminfunctions" class="btn btn-default" title="Bewerk"><span class="glyphicon glyphicon-pencil"></span></a>');
		$button.click(editAdminClick);
		$div.append($button);
		$out.append($div);
		$div = $('<div class="col-sm-1"></div>');
		$button = $('<a href="#adminfunctions" class="btn btn-danger" title="Verwijder"><span class="glyphicon glyphicon-remove"></span></a>');
		$button.click(deleteAdminClick);
		$div.append($button);
		$out.append($div);
		return $out;
	}

	function editAdminClick($e) {
		var $this = $(this),
		$div = $this.parent().parent().find('.name'),
		$name = $div.text(),
		$icon = $this.find('span');
		$e.preventDefault();
		$div.empty();
		$div.append('<div class="row"><div class="col-sm-6"><div class="input-group"><div class="input-group-addon"><span class="glyphicon glyphicon-user"></span></div><input class="form-control" type="text" name="username" placeholder="Gebruikersnaam" value="'+$name+'" /></div></div><div class="col-sm-6"><div class="input-group"><div class="input-group-addon"><span class="glyphicon glyphicon-lock"></span></div><input class="form-control" type="password" name="password" placeholder="Wachtwoord" /></div></div></div>');
		$this.attr('title','Opslaan');
		$this.removeClass('btn-default');
		$this.addClass('btn-primary');
		$icon.removeClass('glyphicon-pencil');
		$icon.addClass('glyphicon-floppy-disk');
	}

	function deleteAdminClick($e) {
		$e.preventDefault();
		if(window.confirm('Weet u zeker dat u deze beheerder wilt verwijderen?')) {
			window.alert('Jammer dan');
		}
	}

	function parseJSON($str) {
		try {
			return JSON.parse($str);
		} catch($e) {}
		return null;
	}
};
$webSocket.send({action: 'getAdmins'});