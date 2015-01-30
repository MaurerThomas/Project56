(function() {
	var $categorie = $('.categorie'),
		$hoofdfilter = $('.hoofdfilter'),
		$subfilter = $('.subfilter'),
		$filtername = $('.filtername'),
		$filternameupdatetext = $('.filternameupdatetext'),
		$filternameaddtext = $('.filternameaddtext'),
		$categorieadd = $('.categorieadd'),
		$categoriedelete = $('.categoriedelete'),
		$categorieupdate = $('.categorieadd'),
		$hoofdfilteradd = $('.hoofdfilteradd'),
		$hoofdfilterupdate = $('.hoofdfilteradd'),
		$hoofdfilterdelete = $('.hoofdfilterdelete'),
		$subfilteradd = $('.subfilteradd'),
        $subfilterupdate = $('.subfilterupdate'),
        $subfilterdelete = $('.subfilterdelete'),
		$btn = $('btn'),
		$btnadd = $('#btn_add'),
		$btnupdate = $('#btn_update'),
		$btndelete = $('#btn_delete'),
		$case = "Behuizing",
		$ram = "Geheugen",
		$gpu = "Grafischekaart",
		$hdd = "Hardeschijf",
		$mobo = "Moederbord",
		$cpu = "Processor",
		$cpucooler = "Processorkoeler",
		$pickone = "Maak een keuze";
		
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
	
    function handleJSON($json) {
		if ($json.processors !== undefined && $json.hardeschijven !== undefined && $json.processors !== undefined && $json.geheugen !== undefined && $json.behuizing !== undefined && $json.grafischekaarten !== undefined) {
			initFilterCategory();
			hideAll();
			
			$categorie.change(function() {
				if($('.categorie :selected').val() == $cpu || $('.categorie :selected').val() == $cpucooler || $('.categorie :selected').val() == $mobo ) {
					clearAll();
					showAll();
					initProcessors($json.processors);
				} else if($('.categorie :selected').val() == $ram) {
					clearAll();
					$hoofdfilter.show();
					$btn.show();
					$filtername.show();
					$subfilter.hide();
					initMemory($json.geheugen);
				} else if($('.categorie :selected').val() == $gpu) {
					clearAll();
					showAll();
					initGPU($json.grafischekaarten);
				} else if($('.categorie :selected').val() == $hdd){
					clearAll();
					showAll();
					initHDD($json.hardeschijven);
				} else if($('.categorie :selected').val() == $case) {
					clearAll();
					$hoofdfilter.show();
					$btn.show();
					$filtername.show();
					$subfilter.hide();
					initCases($json.behuizing);
				} else if($('.categorie :selected').val() == $pickone) {
					clearAll();
					hideAll();
				}
			});
		}
    }
	
	function clearAll() {
		$hoofdfilter.empty();
		$subfilter.empty();
		$filtername.empty();
	}
	
	function hideAll() {
		$hoofdfilter.hide();
		$subfilter.hide();
		$filtername.hide();
		$btn.hide();
	}
	
	function showAll() {
		$hoofdfilter.show();
		$subfilter.show();
		$filtername.show();
		$btn.show();
	}
	
    function initFilterCategory() {
        $categorie.append('<option value="' + $pickone + '">' + $pickone + '</option>');
        $categorie.append('<option value="' + $case + '">' + $case + '</option>');
        $categorie.append('<option value="' + $ram + '">' + $ram + '</option>');
        $categorie.append('<option value="' + $gpu + '">' + $gpu + '</option>');
        $categorie.append('<option value="' + $hdd + '">' + $hdd + '</option>');
        $categorie.append('<option value="' + $mobo + '">' + $mobo + '</option>');
        $categorie.append('<option value="' + $cpu + '">' + $cpu + '</option>');
        $categorie.append('<option value="' + $cpucooler + '">' + $cpucooler + '</option>');
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
    }

    function initMemory($geheugen) {
        var $n, $types = $('.hoofdfilter');
        $types.append('<option value="' + $pickone + '">' + $pickone + '</option>');

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
	
	$btnadd.click(function() {
	    var $cat = $categorieadd.val(),
	    $hoofd = $hoofdfilteradd.val(),
	    $sub = $subfilteradd.val(),
	    $newname = $filternameaddtext.val();

		if($filternameaddtext.length > 0) {
			if (confirm('Weet u zeker dat u deze filter wilt toevoegen?')) {
				$webSocket.send({action: 'createfilter', cat: $cat, hoofd: $hoofd, sub: $sub, newname: $newname});
			}
		}else {
            alert('Vul eerst een waarde in');
        }
	});
	
	$btnupdate.click(function() {
	    var $cat = $categorieupdate.val(),
	    $hoofd = $hoofdfilterupdate.val(),
	    $sub = $subfilterupdate.val(),
	    $newname = $filternameupdatetext.val();

		if($filternameupdatetext.length > 0) {
			if (confirm('Weet u zeker dat u deze filter wilt wijzigen?')) {
			    if($sub != null){
			        $webSocket.send({action: 'updatefilter', cat: $cat, hoofd: $hoofd, sub: $sub, newname: $newname});
			    }else {
			        $sub = $hoofd;
			        $webSocket.send({action: 'updatefilter', cat: $cat, hoofd: $hoofd, sub: $sub, newname: $newname});
			    }
			}
		}else {
		    alert('Vul eerst een waarde in');
		}
	});
	
	$btndelete.click(function() {
	 var $cat = $categoriedelete.val(),
	 $hoofd = $hoofdfilterdelete.val(),
	 $sub = $subfilterdelete.val();

		if (confirm('Weet u zeker dat u deze filter wilt verwijderen?')) {
            if($sub != null){
                $webSocket.send({action: 'deletefilter', cat: $cat, hoofd: $hoofd, sub: $sub});
            }else {
                $sub = $hoofd;
                $webSocket.send({action: 'deletefilter', cat: $cat, hoofd: $hoofd, sub: $sub});
            }
		}
	});
})();