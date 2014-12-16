(function() {
	var $saving = false;

	$webSocket.send({action: 'getAdmins'});
	$webSocket.receive = function($msg) {
		var $json = parseJSON($msg.data);
		if($json !== null) {
			if(!handleJSON($json)) {
				htmlHandler($msg);
			}
		}
	};
	$('#newAdmin').click(function($e) {
		var $json = addAdmin('',-1);
		$e.preventDefault();
		$('#admins').before($json);
		$json.find('a[title="Bewerk"]').click();
	});

	function handleJSON($json) {
		var $admins,$aid;
		if($json.admins !== undefined) {
			$admins = $('#admins');
			$admins.empty();
			for($aid in $json.admins) {
				$admins.append(addAdmin($json.admins[$aid],$aid));
			}
			return true;
		} else if($json.adminModified !== undefined && $saving) {
			if(!$json.adminModified) {
				window.alert('Uw veranderingen zijn niet opgeslagen.');
			}
			$('a.glyphicon').attr('disabled',false);
			$saving = false;
			return true;
		}
		return false;
	}

	function addAdmin($admin,$aid) {
		var $div,$button,$out = $('<div class="row" data-aid="'+$aid+'">');
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
		$this.off();
		$this.click(editAdmin($name));
	}

	function deleteAdminClick($e) {
		$e.preventDefault();
		if(window.confirm('Weet u zeker dat u deze beheerder wilt verwijderen?')) {
			window.alert('Jammer dan');
		}
	}

	function editAdmin($name) {
		return function($e) {
			var $this = $(this),
			$div = $this.parent().parent().find('.name'),
			$icon = $this.find('span')
			$aid = $div.parent().attr('data-aid');
			$e.preventDefault();
			$newName = $div.find('[name="username"]').val(),
			$newPass = $div.find('[name="password"]').val();
			if($newName != $name || $newPass !== '') {
				if($newName != $name && $newPass !== '') {
					saveAdmin({aid: $aid, username: $newName, password: $newPass});
				} else if($newPass !== '') {
					saveAdmin({aid: $aid, password: $newPass});
				} else {
					saveAdmin({aid: $aid, username: $newName});
				}
				window.alert('Uw wijzigingen zijn niet opgeslagen.');
			}
			$div.empty();
			$div.text($newName);
			$this.off();
			$this.click(editAdminClick);
			$this.addClass('btn-default');
			$this.removeClass('btn-primary');
			$icon.addClass('glyphicon-pencil');
			$icon.removeClass('glyphicon-floppy-disk');
		};
	}

	function saveAdmin($json) {
		$webSocket.send($json);
		$('a.glyphicon').attr('disabled',true);
		$saving = true;
	}

	function parseJSON($str) {
		try {
			return JSON.parse($str);
		} catch($e) {}
		return null;
	}
})();