 var grafiekList = [
    "/img/LineChartProcessorenCoolers.jpeg?",
    "/img/LineChartProcessoren.jpeg?",
    "/img/LineChartVoedingen.jpeg?",
    "/img/LineChartMoederborden.jpeg?",
    "/img/LineChartGeheugen.jpeg?",
    "/img/LineChartGrafischeKaarten.jpeg?",
    "/img/LineChartBehuizingen.jpeg?",
    "/img/LineChartSchijven.jpeg?",  ];


$('#grafiek').change(function () {
    d = new Date();
    var select = $('#grafiek option:selected').text();
    $webSocket.send({action:'makeChart','makeChart': select});
    var val = parseInt($('#grafiek').val());
    alert("Grafiek wordt geladen..");
    $('#image').attr("src",grafiekList[val]+d.getTime());
});