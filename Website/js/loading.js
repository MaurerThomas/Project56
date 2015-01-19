$(function() {
    var canvas = document.getElementById('infinito');
    var ctx = canvas.getContext('2d');

    var radio = 50;
    var num_pasos = 100;
    ctx.moveTo(250, 150);
    ctx.strokeStyle = "#ff0000";
    ctx.lineWidth = 25;
    var posicion_corte = 0;
    var radianes = 2 * Math.PI / num_pasos;
    var rad_act = radio;
    var tempo = 0;

    function repintar() {
        tempo++;
        posicion_corte++;
        if (posicion_corte > num_pasos) {
            posicion_corte = 0;
        }
        ctx.clearRect(0, 0, 500, 300);

        ctx.strokeStyle = "rgba(52,52,215,0.3)";
        ctx.beginPath();
        var modo = 2;
        var pos_act = 0;
        ctx.moveTo(Math.cos(0) * radio + 250 - radio, Math.sin(0) * radio + 150);
        for (var i = 0; i <= num_pasos; i++) {
            var px = Math.cos(pos_act * 2) * radio;
            var py = Math.sin(pos_act * 2) * radio;
            rad_act = radio;
            if (i > num_pasos / 2) {
                px = -px;
                rad_act = -radio;
            }
            pos_act += radianes;
            ctx.lineTo(px + 250 - rad_act, py + 150);
        }
        ctx.stroke();

        ctx.strokeStyle = "#ff0000";
        ctx.beginPath();
        var modo = 2;
        var pos_act = 0;
        ctx.moveTo(Math.cos(0) * radio + 250 - radio, Math.sin(0) * radio + 150);
        for (var i = 0; i <= num_pasos; i++) {
            var px = Math.cos(pos_act * 2) * radio;
            var py = Math.sin(pos_act * 2) * radio;
            rad_act = radio;
            if (i > num_pasos / 2) {
                px = -px;
                rad_act = -radio;
            }
            pos_act += radianes;
            if ((i + posicion_corte) % num_pasos > num_pasos / 3) {
                if (modo != 1) {
                    modo = 1;
                    ctx.lineTo(px + 250 - rad_act, py + 150);
                    ctx.stroke();
                    ctx.strokeStyle = "rgba(0,0,0,0)";
                    ctx.beginPath();
                }
            } else {
                if (modo != 2) {
                    modo = 2;
                    ctx.lineTo(px + 250 - rad_act, py + 150);
                    ctx.stroke();
                    ctx.beginPath();
                }
                verde = parseInt(Math.sin(tempo / 20) * 128 + 127, 10);
                azul = parseInt(Math.sin(tempo / 27.3) * 128 + 127, 10);
                rojo = parseInt(Math.sin(tempo / 38) * 128 + 127, 10);
                ctx.strokeStyle = "rgba(" + rojo + "," + verde + "," + azul + ",1)";
            }
            ctx.lineTo(px + 250 - rad_act, py + 150);
        }
        ctx.stroke();
    }

    setInterval(repintar, 30);
});