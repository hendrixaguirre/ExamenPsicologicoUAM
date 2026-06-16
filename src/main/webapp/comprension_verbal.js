"use strict";

/* Aplica el retardo escalonado de las animaciones desde data-i
 * (los estilos en línea los bloquea la política de seguridad de OpenXava). */
document.querySelectorAll("[data-i]").forEach(function (el) {
  el.style.setProperty("--i", el.getAttribute("data-i"));
});


/* ====================================================================
 *  TEST DE COMPRENSIÓN VERBAL — BFA (cliente)
 *  La interfaz solo presenta y captura. Las preguntas, la clave y la
 *  baremación viven en el servidor (servlets IniciarPrueba / CalificarPrueba).
 * ==================================================================== */

/* ----------------------------- CONFIG ---------------------------- */

/** Ruta base de los endpoints del servidor, relativa al origen del HTML. */
const API = "api";
/** Duración de la prueba en segundos (15 minutos). */
const DURACION_SEG = 15 * 60;

/* ----------------------------- ESTADO ---------------------------- */

/** Banco de preguntas recibido del servidor (sin la clave). @type {Array} */
let PREGUNTAS = [];
/** Identificador de la aplicación creada en el servidor. */
let aplicacionId = null;
/** Detalle de corrección devuelto por el servidor para la tabla de resultados. */
let detalleCorreccion = [];
/** Respuestas del evaluado: índice de pregunta → letra elegida ('A'|'B'|'C'). */
let respuestas = {};
/** Índice de la pregunta visible. */
let actual = 0;
/** Segundos restantes del cronómetro. */
let restante = DURACION_SEG;
/** Identificador del intervalo del cronómetro. */
let timerId = null;
/** Indica si la prueba ya fue finalizada (bloquea cambios). */
let finalizada = false;

/* --------------------------- UTILIDADES -------------------------- */

/**
 * Lee y limpia el valor de un campo del formulario de datos del evaluado.
 * @param {string} id Identificador del campo.
 * @returns {string} Valor sin espacios sobrantes.
 */
function val(id){ return document.getElementById(id).value.trim(); }

/**
 * Devuelve el texto descriptivo del nivel de desempeño que envía el servidor.
 * Es solo texto de presentación: la calificación la decide el servidor.
 * @param {string} nivel Nivel devuelto por el servidor.
 * @returns {string} Descripción para mostrar al evaluado.
 */
function descripcionNivel(nivel){
  switch (nivel){
    case "Por debajo del promedio":
      return "Desempeño por debajo del promedio esperado. Se recomienda fortalecer habilidades de lectura, análisis de textos y comprensión de significados.";
    case "Superior":
      return "Desempeño superior. Presenta una alta capacidad para comprender, interpretar y relacionar conceptos verbales complejos.";
    default:
      return "Desempeño promedio o adecuado. Demuestra una comprensión verbal funcional y capacidad para interpretar información escrita.";
  }
}

/**
 * Anima un número entero desde 0 hasta su valor final.
 * @param {HTMLElement} el Elemento donde se escribe el número.
 * @param {number} destino Valor final.
 * @param {number} [dur=1100] Duración en milisegundos.
 */
function contarHasta(el, destino, dur = 1100){
  const reduce = window.matchMedia("(prefers-reduced-motion: reduce)").matches;
  if (reduce || destino === 0){ el.textContent = destino; return; }
  const inicio = performance.now();
  function paso(t){
    const p = Math.min((t - inicio) / dur, 1);
    el.textContent = Math.round((1 - Math.pow(1 - p, 3)) * destino);
    if (p < 1) requestAnimationFrame(paso);
  }
  requestAnimationFrame(paso);
}

/* ----------------------------- RENDER ---------------------------- */

const qCard = document.getElementById("qCard");
const folio = document.getElementById("folio");
const qText = document.getElementById("qText");
const qAuthor = document.getElementById("qAuthor");
const qOptions = document.getElementById("qOptions");
const qCount = document.getElementById("qCount");
const qMap = document.getElementById("qMap");

/**
 * Dibuja la pregunta actual con sus tres opciones y refresca el mapa.
 * @param {number} [dir=0] Dirección de la transición: 1 adelante, -1 atrás, 0 sin animar.
 */
function pintarPregunta(dir = 0){
  const p = PREGUNTAS[actual];
  const nn = String(actual + 1).padStart(2, "0");

  folio.textContent = nn;
  qCount.textContent = `Ejercicio ${nn} / ${PREGUNTAS.length}`;
  qText.textContent = p.texto;
  qAuthor.textContent = p.autor ? `— ${p.autor}` : "";
  qAuthor.style.display = p.autor ? "" : "none";

  qOptions.innerHTML = "";
  ["A", "B", "C"].forEach(letra => {
    const btn = document.createElement("button");
    btn.type = "button";
    btn.className = "opt" + (respuestas[actual] === letra ? " selected" : "");
    btn.setAttribute("role", "radio");
    btn.setAttribute("aria-checked", respuestas[actual] === letra ? "true" : "false");
    btn.innerHTML =
      `<span class="opt__letter" aria-hidden="true">${letra}</span>` +
      `<span class="opt__text">${p[letra.toLowerCase()]}</span>`;
    btn.addEventListener("click", () => seleccionar(letra));
    qOptions.appendChild(btn);
  });

  if (dir !== 0){
    qCard.classList.remove("swap-in", "swap-back");
    void qCard.offsetWidth;                       // reinicia la animación
    qCard.classList.add(dir > 0 ? "swap-in" : "swap-back");
  }

  document.getElementById("btnPrev").disabled = actual === 0;
  document.getElementById("btnNext").disabled = actual === PREGUNTAS.length - 1;
  pintarMapa();
}

/** Dibuja el mapa de navegación (eco visual de la hoja de respuestas). */
function pintarMapa(){
  qMap.innerHTML = "";
  PREGUNTAS.forEach((p, i) => {
    const dot = document.createElement("button");
    dot.type = "button";
    dot.className = "qmap__dot"
      + (respuestas[i] ? " answered" : "")
      + (i === actual ? " current" : "");
    dot.textContent = i + 1;
    dot.setAttribute("aria-label", `Ejercicio ${i + 1}${respuestas[i] ? ", respondido" : ", sin responder"}`);
    dot.addEventListener("click", () => { const d = i > actual ? 1 : -1; actual = i; pintarPregunta(d); });
    qMap.appendChild(dot);
  });
}

/* ---------------------------- ACCIONES --------------------------- */

/**
 * Registra la letra elegida para la pregunta visible y avanza
 * tras una pausa breve al siguiente ejercicio.
 * @param {string} letra Opción elegida ('A'|'B'|'C').
 */
function seleccionar(letra){
  if (finalizada) return;
  respuestas[actual] = letra;
  pintarPregunta(0);
  if (actual < PREGUNTAS.length - 1){
    setTimeout(() => { if (!finalizada){ actual++; pintarPregunta(1); } }, 280);
  }
}

/**
 * Inicia la prueba: pide los datos del evaluado y las preguntas al servidor,
 * que crea la aplicación (EN_PROGRESO) y devuelve los ejercicios sin la clave.
 */
async function iniciarPrueba(){
  const btn = document.getElementById("btnStart");
  btn.disabled = true;
  const textoOriginal = btn.textContent;
  btn.textContent = "Cargando…";
  try {
    const datosSujeto = {
      nombre: val("fNombre"),
      primerApellido: val("fPrimerApellido"),
      segundoApellido: val("fSegundoApellido"),
      fechaNacimiento: val("fFechaNacimiento") || null,
      sexo: val("fSexo") || null,
      estudiosRealizados: val("fEstudios"),
      profesion: val("fProfesion")
    };
    const resp = await fetch(`${API}/iniciar`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(datosSujeto)
    });
    if (!resp.ok) throw new Error("inicio");
    const data = await resp.json();

    aplicacionId = data.aplicacionId;
    PREGUNTAS = data.preguntas
      .filter(p => !p.esEjemplo)
      .sort((a, b) => a.numero - b.numero)
      .map(p => ({ num: p.numero, texto: p.enunciado, autor: null,
                   a: p.opciones.A, b: p.opciones.B, c: p.opciones.C }));

    respuestas = {}; actual = 0; restante = DURACION_SEG; finalizada = false;
    mostrarPantalla("screenTest");
    pintarPregunta(0);
    iniciarCronometro();
  } catch (e) {
    alert("No se pudo iniciar la prueba. Verifique que el servidor esté en ejecución.");
    btn.disabled = false;
    btn.textContent = textoOriginal;
  }
}

/**
 * Cierra la prueba: detiene el cronómetro, envía las respuestas al servidor
 * para su calificación y muestra la pantalla de resultados con el detalle.
 * @param {boolean} porTiempo true si el cierre lo provocó el cronómetro.
 */
async function finalizarPrueba(porTiempo){
  if (finalizada) return;
  finalizada = true;
  clearInterval(timerId);

  const envio = {};
  PREGUNTAS.forEach((p, i) => { if (respuestas[i]) envio[p.num] = respuestas[i]; });

  let data;
  try {
    const resp = await fetch(`${API}/calificar`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ aplicacionId, respuestas: envio })
    });
    if (!resp.ok) throw new Error("calificar");
    data = await resp.json();
  } catch (e) {
    alert("No se pudo enviar la prueba para su calificación. Verifique la conexión con el servidor.");
    return;
  }

  const pd = data.puntuacionDirecta;
  const pc = data.percentil;
  const nivel = data.interpretacion;
  detalleCorreccion = data.detalle || [];
  const contestadas = Object.keys(respuestas).length;
  const usados = DURACION_SEG - Math.max(restante, 0);

  contarHasta(document.getElementById("rPD"), pd, 900);
  contarHasta(document.getElementById("rPC"), pc, 1300);
  document.getElementById("rAnswered").textContent = `${contestadas} / ${PREGUNTAS.length}`;
  document.getElementById("rTime").textContent =
    `${String(Math.floor(usados / 60)).padStart(2, "0")}:${String(usados % 60).padStart(2, "0")}`;
  document.getElementById("rLevel").textContent = nivel;
  document.getElementById("rText").textContent = descripcionNivel(nivel);
  document.getElementById("timeoutNote").hidden = !porTiempo;
  document.getElementById("gaugeWrap").setAttribute("aria-label", `Percentil ${pc} de 99`);

  pintarDetalle();
  mostrarPantalla("screenResult");
  timebarFill.style.transform = "scaleX(0)";

  // Medidor radial: animar el trazo hasta el percentil.
  const r = 88, circ = 2 * Math.PI * r;
  const fill = document.getElementById("gaugeFill");
  fill.style.strokeDasharray = circ;
  fill.style.strokeDashoffset = circ;
  requestAnimationFrame(() => requestAnimationFrame(() => {
    fill.style.strokeDashoffset = circ * (1 - pc / 100);
  }));
}

/** Construye la tabla del detalle de corrección con los datos del servidor. */
function pintarDetalle(){
  const body = document.getElementById("reviewBody");
  body.innerHTML = "";
  detalleCorreccion.forEach((d, i) => {
    const dada = d.dada ?? null;
    const resultado = dada === null
      ? `<td class="blank">sin responder</td>`
      : d.correcta
        ? `<td class="hit">acierto</td>`
        : `<td class="miss">error</td>`;
    const tr = document.createElement("tr");
    tr.style.animationDelay = `${i * 22}ms`;
    tr.innerHTML = `<td>${d.numero}</td><td>${dada ?? "—"}</td><td>${d.clave}</td>${resultado}`;
    body.appendChild(tr);
  });
}

/**
 * Cambia la pantalla visible y reinicia su secuencia de revelado.
 * @param {string} id Identificador de la sección a mostrar.
 */
function mostrarPantalla(id){
  document.querySelectorAll(".screen").forEach(s => s.classList.remove("active"));
  const target = document.getElementById(id);
  target.querySelectorAll(".reveal").forEach(el => {
    el.style.animation = "none";
    void el.offsetWidth;
    el.style.animation = "";
  });
  target.classList.add("active");
  window.scrollTo({ top: 0, behavior: "smooth" });
}

/* --------------------------- CRONÓMETRO -------------------------- */

const clock = document.getElementById("clock");
const timebarFill = document.getElementById("timebarFill");

/** Inicia el cronómetro regresivo; al llegar a cero finaliza por tiempo agotado. */
function iniciarCronometro(){
  pintarReloj();
  timerId = setInterval(() => {
    restante--;
    pintarReloj();
    if (restante <= 0){
      clearInterval(timerId);
      finalizarPrueba(true);
    }
  }, 1000);
}

/** Actualiza el reloj numérico y la barra de tiempo, con estados de alerta. */
function pintarReloj(){
  const m = Math.floor(restante / 60);
  const s = restante % 60;
  clock.textContent = `${m}:${String(s).padStart(2, "0")}`;
  timebarFill.style.transform = `scaleX(${restante / DURACION_SEG})`;

  const warn = restante <= 300, danger = restante <= 60;
  clock.classList.toggle("warn", warn && !danger);
  clock.classList.toggle("danger", danger);
  timebarFill.classList.toggle("warn", warn && !danger);
  timebarFill.classList.toggle("danger", danger);
}

/* ---------------------------- EVENTOS ---------------------------- */

document.getElementById("btnStart").addEventListener("click", iniciarPrueba);

document.getElementById("btnPrev").addEventListener("click", () => {
  if (actual > 0){ actual--; pintarPregunta(-1); }
});
document.getElementById("btnNext").addEventListener("click", () => {
  if (actual < PREGUNTAS.length - 1){ actual++; pintarPregunta(1); }
});

const confirmDialog = document.getElementById("confirmDialog");
document.getElementById("btnFinish").addEventListener("click", () => {
  const sinResponder = PREGUNTAS.length - Object.keys(respuestas).length;
  document.getElementById("confirmText").textContent = sinResponder > 0
    ? `Le quedan ${sinResponder} ejercicio${sinResponder === 1 ? "" : "s"} sin responder. Una vez finalizada, la prueba no se puede modificar.`
    : "Respondió todos los ejercicios. Una vez finalizada, la prueba no se puede modificar.";
  confirmDialog.showModal();
});
document.getElementById("btnCancelFinish").addEventListener("click", () => confirmDialog.close());
document.getElementById("btnConfirmFinish").addEventListener("click", () => {
  confirmDialog.close();
  finalizarPrueba(false);
});

document.getElementById("btnRestart").addEventListener("click", () => {
  respuestas = {}; actual = 0; restante = DURACION_SEG; finalizada = false;
  aplicacionId = null; PREGUNTAS = []; detalleCorreccion = [];
  timebarFill.style.transform = "scaleX(1)";
  timebarFill.classList.remove("warn", "danger");
  clock.classList.remove("warn", "danger");
  clock.textContent = "15:00";
  const btn = document.getElementById("btnStart");
  btn.disabled = false; btn.textContent = "Comenzar la prueba";
  mostrarPantalla("screenIntro");
});

/* Atajos de teclado: A/B/C para responder, flechas para navegar. */
document.addEventListener("keydown", e => {
  if (!document.getElementById("screenTest").classList.contains("active") || finalizada) return;
  if (e.target.closest("dialog") || e.target.closest("input") || e.target.closest("select")) return;
  const k = e.key.toUpperCase();
  if (["A", "B", "C"].includes(k)) seleccionar(k);
  else if (e.key === "ArrowRight" && actual < PREGUNTAS.length - 1){ actual++; pintarPregunta(1); }
  else if (e.key === "ArrowLeft" && actual > 0){ actual--; pintarPregunta(-1); }
});