function initFilters() {
	$webSocket.receive = function($msg) {
		var $json = parseJSON($msg.data);
		if($json !== null) {
			initProcessors($json.init.processors);
			initMemory($json.init.geheugen);
		}
	};
	$webSocket.send({action: 'init'});

	function parseJSON($string) {
		try {
			return JSON.parse($string);
		} catch($e) {
			return null;
		}
	}

	function initProcessors($processors) {
		var $merk,$n,
		$brand = $('.pcbuilder-processor-brand'),
		$socket = $('.pcbuilder-processor-socket');
		for($merk in $processors) {
			$brand.append('<option value="'+$merk+'">'+$merk+'</option>');
			for($n=0;$n < $processors[$merk].length;$n++) {
				$socket.append('<option value="'+$merk+'-'+$processors[$merk][$n]+'">'+$processors[$merk][$n]+'</option>');
			}
		}

		$brand.change(function($e) {
			var $this = $(this),
			$val = $this.val();
			$brand.val($val);
			if($val == 'none') {
				$socket.addClass('hidden');
			} else {
				$socket.find('option').show();
				$socket.find('option:not([value|='+$val+'])').hide();
				$socket.find('option[value="none"]').show();
				$socket.removeClass('hidden');
				$socket.val('none');
			}
		});

		$socket.change(function($e) {
			$socket.val($(this).val());
		});
	}

	function initMemory($memory) {
		var $n,
		$types = $('.pcbuilder-ram');
		for($n=0;$n < $memory.length;$n++) {
			$types.append('<option value="'+$memory[$n]+'">'+$memory[$n]+'</option>');
		}
	}
/*
	$('#moederbordsocketmerk').change(function (event) {
		var socketmerk = $('#moederbordsocketmerk').val();
		if(socketmerk == 'AMD') {
			$('#group1').show().fadeIn(); 
			$('#group2').hide().fadeOut();
		} else if ((socketmerk == 'Intel')){
			$('#group2').show().fadeIn(); 
			$('#group1').hide().fadeOut();
		} else {
			$('#group1').hide().fadeOut();
			$('#group2').hide().fadeOut();
		}
	});

	$('#processormerk').change(function (event) {
		var processormerk = $('#processormerk').val();
		var processorsocket = $('#processorsocketintel').val();

		if(processormerk == 'AMD') {
			$('#group3').show().fadeIn(); 
			$('#group4').hide().fadeOut();
			$('#group5').hide().fadeOut();
		} else if ((processormerk == 'Intel')){
			$('#group4').show().fadeIn(); 
			$('#group3').hide().fadeOut();
			$('#group5').show().fadeIn();
		} else {
			$('#group3').hide().fadeOut();
			$('#group4').hide().fadeOut();
			$('#group5').hide().fadeOut();
		}
	});
		
		$('input[type="range"]').each(function() {
				$(this).change(function() {
						$('output[for="'+this.id+'"]').text($(this).val());
				});
		});*/
}