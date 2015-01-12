function initFilters() {
	$webSocket.receive = function($msg) {
		var $json = parseJSON($msg.data);
		if($json !== null && $json.init !== undefined) {
			initProcessors($json.init.processors);
			initMemory($json.init.geheugen);
			initHDD($json.init.hardeschijven);
			initCases($json.init.behuizing);
			initGPU($json.init.grafischekaarten);
			initSliders();
			initSearch();
		}
	};
	$webSocket.send({action: 'init'});
	initFilters = undefined;

	function parseJSON($string) {
		try {
			return JSON.parse($string);
		} catch($e) {
			return null;
		}
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
				$child.removeClass('hidden');
				$child.val('none');
			}
		});

		setupSyncingSelect($child);
	}

	function setupSyncingSelect($select) {
		$select.change(function() {
			$select.val($(this).val());
		});
	}

	function initProcessors($processors) {
		var $merk,$n,
		$brand = $('.pcbuilder-processor-brand'),
		$socket = $('.pcbuilder-processor-socket');
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
		$types = $('.pcbuilder-ram');
		for($n=0;$n < $memory.length;$n++) {
			$types.append('<option value="'+$memory[$n]+'">'+$memory[$n]+'</option>');
		}
	}

	function initHDD($harddrives) {
		var $n,$hdd,
		$types = $('.pcbuilder-hdd-type'),
		$interface = $('.pcbuilder-hdd-interface');
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
		$types = $('.pcbuilder-case');
		for($n=0;$n < $cases.length;$n++) {
			$types.append('<option value="'+$cases[$n]+'">'+$cases[$n]+'</option>');
		}

		setupSyncingSelect($types);
	}

	function initGPU($gpus) {
		var $n,
		$brands = $('.pcbuilder-gpu-brand'),
		$types = $('.pcbuilder-gpu-interface');
		for($n=0;$n < $gpus.aansluitingen.length;$n++) {
			$types.append('<option value="'+$gpus.aansluitingen[$n]+'">'+$gpus.aansluitingen[$n]+'</option>');
		}
		for($n=0;$n < $gpus.merken.length;$n++) {
			$brands.append('<option value="'+$gpus.merken[$n]+'">'+$gpus.merken[$n]+'</option>');
		}
	}

	function initSliders() {
		var $ranges = $('input[type="range"]');
		$ranges.change(changeOutput);
		$ranges.on('input',changeOutput);
		$ranges.change();

		function changeOutput() {
			$('output[for="'+this.id+'"]').text($(this).val());
			checkMinMax(this);
		}

		function checkMinMax($slider) {
			var $val,$other,
			$id = this.id.split('-');
			if($id.length == 2) {
				if($id[1].indexOf('max') === 0) {
					$val = $(this).val();
					$other = $('#'+$id[0]+$id[1].replace('max','min'));
					if($other.val() > $val) {
						$other.val($val);
					}
				} else if($id[1].indexOf('min') === 0) {
					$val = $(this).val();
					$other = $('#'+$id[0]+$id[1].replace('min','max'));
					if($other.val() < $val) {
						$other.val($val);
					}
				}
			}
		}
	}
}