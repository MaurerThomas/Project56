(function() {
	$webSocket.send({action: 'init'});
	$webSocket.receive = function($msg) {
		var $json = parseJSON($msg.data);
		if($json !== null) {
			if(!handleJSON($json)) {
				htmlHandler($msg);
			}
		}
	};
	
	function parseJSON($str) {
		try {
			return JSON.parse($str);
		} catch($e) {}
		return null;
	}
	
	function setupDependantSelect($parent,$child) {
		$parent.change(function() {
			var $this = $(this),
			$val = $this.val();
			$parent.val($val);
			if($val == 'none') {
				$child.addClass('hidden');
			} else {
				$child.find('option').show();
				$child.find('option:not([data-parent="'+$val+'"])').hide();
				$child.find('option[value="none"]').show();
				$child.removeClass('hidden');
				$child.val('none');
			}
		});
	}
	
	function handleJSON($json) {
		return initFilterCategory($json);
		return initProcessors($json.processors);
		return initMemory($json.memory);
		return initHDD($json.harddrives);
		return initCases($json.cases);
		return initGPU($json.gpus);
	}

	function initFilterCategory($json) {
		var $case = "Behuizing",
		$ram = "Geheugen", 
		$gpu = "Grafischekaart",
		$hdd = "Hardeschijf",
		$mobo = "Moederbord"	
		$cpu = "Processor",
		$cpucooler = "Processorkoeler",
		$psu = "Voeding",
		$categorie = $('.categorie');

		$categorie.append('<option value="'+$case+'">'+$case+'</option>');
		$categorie.append('<option value="'+$ram+'">'+$ram+'</option>');
		$categorie.append('<option value="'+$gpu+'">'+$gpu+'</option>');
		$categorie.append('<option value="'+$hdd+'">'+$hdd+'</option>');
		$categorie.append('<option value="'+$mobo+'">'+$mobo+'</option>');
		$categorie.append('<option value="'+$cpu+'">'+$cpu+'</option>');
		$categorie.append('<option value="'+$cpucooler+'">'+$cpucooler+'</option>');
		$categorie.append('<option value="'+$psu+'">'+$psu+'</option>');
	}
	
		function initProcessors($processors) {
		var $merk,$n,
		$brand = $('.hoofdfilter'),
		$socket = $('.subfilter');
		for($merk in $processors) {
			$brand.append('<option value="'+$merk+'">'+$merk+'</option>');
			for($n=0;$n < $processors[$merk].length;$n++) {
				$socket.append('<option data-parent="'+$merk+'" value="'+$processors[$merk][$n]+'">'+$processors[$merk][$n]+'</option>');
			}
		}

		setupDependantSelect($brand,$socket);
	}

	function initMemory($memory) {
		var $n,
		$types = $('.hoofdfilter');
		for($n=0;$n < $memory.length;$n++) {
			$types.append('<option value="'+$memory[$n]+'">'+$memory[$n]+'</option>');
		}
	}

	function initHDD($harddrives) {
		var $n,$hdd,
		$types = $('.hoofdfilter'),
		$interface = $('.subfilter');
		for($hdd in $harddrives) {
			$types.append('<option value="'+$hdd+'">'+$hdd+'</option>');
			for($n=0;$n < $harddrives[$hdd].length;$n++) {
				$interface.append('<option data-parent="'+$hdd+'" value="'+$harddrives[$hdd][$n]+'">'+$harddrives[$hdd][$n]+'</option>');
			}
		}

		setupDependantSelect($types,$interface);
	}

	function initCases($cases) {
		var $n,
		$types = $('.hoofdfilter');
		for($n=0;$n < $cases.length;$n++) {
			$types.append('<option value="'+$cases[$n]+'">'+$cases[$n]+'</option>');
		}

		setupSyncingSelect($types);
	}

	function initGPU($gpus) {
		var $n,
		$brands = $('.hoofdfilter'),
		$types = $('.subfilter');
		for($n=0;$n < $gpus.aansluitingen.length;$n++) {
			$types.append('<option value="'+$gpus.aansluitingen[$n]+'">'+$gpus.aansluitingen[$n]+'</option>');
		}
		for($n=0;$n < $gpus.merken.length;$n++) {
			$brands.append('<option value="'+$gpus.merken[$n]+'">'+$gpus.merken[$n]+'</option>');
		}
	}
	
})();