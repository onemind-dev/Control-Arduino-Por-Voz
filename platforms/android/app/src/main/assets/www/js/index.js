const adelante = document.querySelector("#Adelante");
const atras = document.querySelector("#Atras");
const izquierda = document.querySelector("#Izq");
const derecha = document.querySelector("#Derecha");
const parar = document.querySelector("#Parar");
const estado = document.querySelector("#Estado");
const conectar = document.querySelector("#Conectar");
const Hablar = document.querySelector("#Hablar");

function enviarBluetooth(comando) {
    console.log("Bluetooth:", comando);
}

const botones = {
    Adelante: "a",
    Atras: "e",
    Izq: "b",
    Derecha: "d",
    Parar: "c"
};

for (const id in botones) {
    const boton = document.querySelector(`#${id}`);

    boton.addEventListener("click", () => {
        const comando = botones[id];

        console.log(comando);
    });
}
