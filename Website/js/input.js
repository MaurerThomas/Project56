$(function() {
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
    });
});