(function() {
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
        });
    }

    function handleJSON($json) {
        return initProcessors($json.processors);
        return initMemory($json.geheugen);
        return initHDD($json.hardeschijven);
        return initCases($json.behuizing);
        return initGPU($json.grafischekaarten);
    }

    function initFilterCategory() {
        var $case = "Behuizing",
            $ram = "Geheugen",
            $gpu = "Grafischekaart",
            $hdd = "Hardeschijf",
            $mobo = "Moederbord",
			$cpu = "Processor",
            $cpucooler = "Processorkoeler",
            $psu = "Voeding",
            $categorie = $('.categorie');

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
        console.log("in de init processor");
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
		initFilterCategory();
    }

    function initMemory($geheugen) {
        var $n, $types = $('.hoofdfilter');
        for ($n = 0; $n < $geheugen.length; $n++) {
            $types.append('<option value="' + $geheugen[$n] + '">' + $geheugen[$n] + '</option>');
        }
		initFilterCategory();
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
		initFilterCategory();
    }

    function initCases($behuizing) {
        var $n, $types = $('.hoofdfilter');
        for ($n = 0; $n < $behuizing.length; $n++) {
            $types.append('<option value="' + $behuizing[$n] + '">' + $behuizing[$n] + '</option>');
        }
		initFilterCategory();
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
	initFilterCategory();

})();