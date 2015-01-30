(function() {
	var $categorie = $('.categorie'),
		$case = "Behuizing",
		$ram = "Geheugen",
		$gpu = "Grafischekaart",
		$hdd = "Hardeschijf",
		$mobo = "Moederbord",
		$cpu = "Processor",
		$cpucooler = "Processorkoeler",
		$psu = "Voeding";
		
    $webSocket.send({
        action: 'init'
    });
	
    $webSocket.receive = function($msg) {
        var $json = parseJSON($msg.data);
        if ($json !== null) {
            if (!handleJSON($json)) {
                htmlHandler($msg);
            }
        }
    };

    function parseJSON($str) {
        try {
            return JSON.parse($str);
        } catch ($e) {}
        return null;
    }

    function setupDependantSelect($parent, $child) {
        $parent.change(function() {
            var $this = $(this),
                $val = $this.val();
            $parent.val($val);
            if ($val == 'none') {
                $child.addClass('hidden');
            } else {
                $child.find('option').show();
                $child.find('option:not([data-parent="' + $val + '"])').hide();
                $child.find('option[value="none"]').show();
                $child.removeClass('hidden');
                $child.val('none');
            }
			console.log("setupDependantSelect uitgevoerd");
        });
		setupSyncingSelect($child);
	}

	function setupSyncingSelect($select) {
		$select.change(function() {
			$select.val($(this).val());
		});
	}
	
    function handleJSON($json) {
		if ($json.processors !== undefined && $json.hardeschijven !== undefined && $json.processors !== undefined && $json.geheugen !== undefined && $json.behuizing !== undefined && $json.grafischekaarten !== undefined) {
			initFilterCategory();
			
			$categorie.change(function() {
				if($('.categorie :selected').val() == $cpu) {
					initProcessors($json.processors);
				} else if($('.categorie :selected').val() == $ram) {
					initMemory($json.geheugen);
				} else if($('.categorie :selected').val() == $gpu) {
					initGPU($json.grafischekaarten);
				} else if($('.categorie :selected').val() == $hdd){
					initHDD($json.hardeschijven);
				} else if($('.categorie :selected').val() == $case) {
					initCases($json.behuizing);
				} else {
					
				}
			});
		}
    }

    function initFilterCategory() {
        $categorie.append('<option value="Maak een keuze">Maak een keuze</option>');
        $categorie.append('<option value="' + $case + '">' + $case + '</option>');
        $categorie.append('<option value="' + $ram + '">' + $ram + '</option>');
        $categorie.append('<option value="' + $gpu + '">' + $gpu + '</option>');
        $categorie.append('<option value="' + $hdd + '">' + $hdd + '</option>');
        $categorie.append('<option value="' + $mobo + '">' + $mobo + '</option>');
        $categorie.append('<option value="' + $cpu + '">' + $cpu + '</option>');
        $categorie.append('<option value="' + $cpucooler + '">' + $cpucooler + '</option>');
        $categorie.append('<option value="' + $psu + '">' + $psu + '</option>');
    }

    function initProcessors($processors) {
        var $merk, $n,
            $brand = $('.hoofdfilter'),
            $socket = $('.subfilter');
        for ($merk in $processors) {
            $brand.append('<option value="' + $merk + '">' + $merk + '</option>');
            for ($n = 0; $n < $processors[$merk].length; $n++) {
                $socket.append('<option data-parent="' + $merk + '" value="' + $processors[$merk][$n] + '">' + $processors[$merk][$n] + '</option>');
            }
        }
        setupDependantSelect($brand, $socket);
    }

    function initMemory($geheugen) {
        var $n, $types = $('.hoofdfilter');
        for ($n = 0; $n < $geheugen.length; $n++) {
            $types.append('<option value="' + $geheugen[$n] + '">' + $geheugen[$n] + '</option>');
        }
    }

    function initHDD($hardeschijven) {
        var $n, $hdd, $types = $('.hoofdfilter'), $interface = $('.subfilter');
        for ($hdd in $hardeschijven) {
            $types.append('<option value="' + $hdd + '">' + $hdd + '</option>');
            for ($n = 0; $n < $hardeschijven[$hdd].length; $n++) {
                $interface.append('<option data-parent="' + $hdd + '" value="' + $hardeschijven[$hdd][$n] + '">' + $hardeschijven[$hdd][$n] + '</option>');
            }
        }
        setupDependantSelect($types, $interface);
    }

    function initCases($behuizing) {
        var $n, $types = $('.hoofdfilter');
        for ($n = 0; $n < $behuizing.length; $n++) {
            $types.append('<option value="' + $behuizing[$n] + '">' + $behuizing[$n] + '</option>');
        }
    }

    function initGPU($grafischekaarten) {
        var $n, $brands = $('.hoofdfilter'), $types = $('.subfilter');
        for ($n = 0; $n < $grafischekaarten.aansluitingen.length; $n++) {
            $types.append('<option value="' + $grafischekaarten.aansluitingen[$n] + '">' + $grafischekaarten.aansluitingen[$n] + '</option>');
        }
        for ($n = 0; $n < $grafischekaarten.merken.length; $n++) {
            $brands.append('<option value="' + $grafischekaarten.merken[$n] + '">' + $grafischekaarten.merken[$n] + '</option>');
        }
    }

})();