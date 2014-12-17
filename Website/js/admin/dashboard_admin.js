(function() {
	var $saving = false, $deleting = false;

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
		if($('[data-aid="-1"]').length === 0) {
			var $json = addAdmin('',-1);
			$e.preventDefault();
			$('#admins').prepend($json);
			$json.find('a[title="Bewerk"]').click();
		}
	});

	function handleJSON($json) {
		if($json.admins !== undefined) {
			return handleShowAdmins($json);
		} else if($json.adminModified !== undefined && $saving == 'adminModified') {
			handleModifyAdmin($json);
		} else if($json.adminAdded !== undefined && $saving == 'adminAdded') {
			return handleAddAdmin($json);
		} else if($json.adminDeleted !== undefined && $deleting !== false) {
			return handleDeleteAdmin($json);
		}
		return false;
	}

	function handleShowAdmins($json) {
		var $admins,$aid;
		$admins = $('#admins');
		$admins.empty();
		for($aid in $json.admins) {
			$admins.append(addAdmin($json.admins[$aid],$aid));
		}
		return true;
	}

	function handleModifyAdmin($json) {
		if(!$json.adminModified) {
			window.alert('Uw veranderingen zijn niet opgeslagen.');
		}
		$('a.btn').attr('disabled',false);
		$saving = false;
		return true;
	}

	function handleAddAdmin($json) {
		var $div,$name,$icon,$button;
		$div = $('[data-aid="-1"]');
		$name = $div.find('input[name="username"]').val();
		$div.attr('data-name',$name);
		$div.text($name);
		$button = $div.parent().find('a.btn-primary');
		$icon = $button.find('.glyphicon');
		$icon.addClass('glyphicon-pencil');
		$icon.removeClass('glyphicon-floppy-disk');
		$button.addClass('btn-default');
		$button.removeClass('btn-primary');
		$div.attr('data-aid',$json.adminAdded);
		$('a.btn').attr('disabled',false);
		$saving = false;
		return true;
	}

	function handleDeleteAdmin($json) {
		if($json.adminDeleted) {
			$('[data-aid="'+$deleting+'"]').remove();
		} else {
			window.alert('De gebruiker is niet verwijderd.');
		}
		$('a.btn').attr('disabled',false);
		$deleting = false;
		return true;
	}

	function addAdmin($admin,$aid) {
		var $div,$button,$out = $('<div class="row" data-aid="'+$aid+'" data-name="'+$admin+'">');
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
		$this.click(editAdmin);
	}

	function deleteAdminClick($e) {
		var $div = $(this).parent().parent(),
		$aid = $div.attr('data-aid');
		$e.preventDefault();
		if(window.confirm('Weet u zeker dat u deze beheerder wilt verwijderen?')) {
			if($aid != -1) {
				$webSocket.send({action: 'deleteAdmin', aid: $aid});
				$deleting = $aid;
				$('a.btn').attr('disabled',true);
			} else {
				$div.remove();
			}
		}
	}

	function editAdmin($e) {
		var $this = $(this),
		$div = $this.parent().parent().find('.name'),
		$icon = $this.find('span')
		$aid = $div.parent().attr('data-aid'),
		$name = $div.parent().attr('data-name');
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
		}
		$div.empty();
		$div.text($newName);
		$this.off();
		$this.click(editAdminClick);
		$this.addClass('btn-default');
		$this.removeClass('btn-primary');
		$icon.addClass('glyphicon-pencil');
		$icon.removeClass('glyphicon-floppy-disk');
	}

	function saveAdmin($json) {
		if($json.aid != -1) {
			$json.action = 'modifyAdmin';
			$saving = 'adminModified';
		} else {
			delete $json.aid;
			$json.action = 'addAdmin';
			$saving = 'adminAdded';
		}
		$webSocket.send($json);
		$('a.btn').attr('disabled',true);
	}

	function parseJSON($str) {
		try {
			return JSON.parse($str);
		} catch($e) {}
		return null;
	}
})();