 $(function() {
 	var grafiekList = [
 		"/img/LineChartProcessoren.jpeg?",
 		"/img/LineChartVoedingen.jpeg?",
 		"/img/LineChartMoederborden.jpeg?",
 		"/img/LineChartGrafischeKaarten.jpeg?",
 		"/img/LineChartBehuizingen.jpeg?",
 		"/img/LineChartSchijven.jpeg?"
 	];

 	$("#grafiek option").each(function() {
 		var select = $(this).text();
 		$webSocket.send({
 			action: 'makeChart',
 			'makeChart': select
 		});
 	});

 	$('#grafiek').change(function() {
 		var d = new Date();
 		var val = parseInt($('#grafiek').val());
 		$('#image').attr("src", grafiekList[val] + d.getTime());
 	});
 });