document.addEventListener("DOMContentLoaded", () => {

    const estado = document.getElementById("Estado");
    const textoVoz = document.getElementById("Textovoz");

    // Obtener el plugin
    function obtenerPluginBT() {
        if (window.Capacitor && window.Capacitor.Plugins && window.Capacitor.Plugins.AndroidBluetooth) {
            return window.Capacitor.Plugins.AndroidBluetooth;
        }
        return null;
    }

    // Función principal
    async function enviarComando(letra) {
        const Bluetooth = obtenerPluginBT();
        if (estado) estado.textContent = "Enviando: " + letra;

        if (Bluetooth) {
            try {
                await Bluetooth.enviar({ mensaje: letra });
            } catch (error) {
                if (estado) estado.textContent = "Error enviando " + letra;
            }
        } else {
            if (estado) estado.textContent = "Plugin BT no disponible";
        }
    }

    // Botones de Movimiento
    document.getElementById("Adelante").onclick = () => enviarComando("F");
    document.getElementById("Atras").onclick    = () => enviarComando("B");
    document.getElementById("Izq").onclick      = () => enviarComando("L");
    document.getElementById("Derecha").onclick  = () => enviarComando("R");
    document.getElementById("Parar").onclick    = () => enviarComando("S");

    // Botón Conectar
    document.getElementById("Conectar").onclick = async () => {
        const Bluetooth = obtenerPluginBT();

        if (Bluetooth) {
            try {
                if (estado) estado.textContent = "Conectando al HC-05...";
                await Bluetooth.conectar();
                if (estado) estado.textContent = "¡¡¡HC-05 CONECTADO!!!";
            } catch (error) {
                if (estado) estado.textContent = "Error: " + (error.message || error);
            }
        } else {
            if (estado) estado.textContent = "Plugin BT no disponible";
        }
    };

    // Botón Hablar
    document.getElementById("Hablar").onclick = () => {
        if (window.AndroidVoz && window.AndroidVoz.abrirMicrofono) {
            window.AndroidVoz.abrirMicrofono();
        } else {
            if (textoVoz) textoVoz.textContent = "Error: micrófono no listo";
        }
    };

    // Procesar la voz
    window.procesarVozNativa = function(texto) {
        if (textoVoz) textoVoz.textContent = "Dijiste: " + texto;

        if (texto.includes("adelante") || texto.includes("avanza")) return enviarComando("F");
        if (texto.includes("atrás") || texto.includes("atras") || texto.includes("retrocede")) return enviarComando("B");
        if (texto.includes("izquierda")) return enviarComando("L");
        if (texto.includes("derecha")) return enviarComando("R");
        if (texto.includes("para") || texto.includes("parar") || texto.includes("alto")) return enviarComando("S");

        if (textoVoz) textoVoz.textContent = "Comando no reconocido: " + texto;
    };

});






