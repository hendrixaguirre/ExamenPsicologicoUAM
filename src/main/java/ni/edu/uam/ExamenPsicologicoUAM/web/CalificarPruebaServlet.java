package ni.edu.uam.ExamenPsicologicoUAM.web;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import ni.edu.uam.ExamenPsicologicoUAM.modelo.*;
import org.openxava.jpa.XPersistence;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Recibe las respuestas del sujeto, las califica contra la clave (que vive en el
 * servidor), persiste el resultado y devuelve la puntuación directa, el percentil,
 * la interpretación y el detalle de corrección. La validación del tiempo se hace
 * aquí, no en el navegador.
 */
public class CalificarPruebaServlet extends HttpServlet {

    private final Gson gson = new Gson();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        EntityManager em = XPersistence.getManager();
        try {
            String cuerpo = leerCuerpo(req);
            JsonObject datos = gson.fromJson(cuerpo, JsonObject.class);
            String aplicacionId = datos.get("aplicacionId").getAsString();
            JsonObject respuestas = datos.has("respuestas") ? datos.getAsJsonObject("respuestas") : new JsonObject();

            AplicacionPrueba ap = em.find(AplicacionPrueba.class, aplicacionId);
            if (ap == null) {
                XPersistence.rollback();
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                resp.getWriter().write("{\"error\":\"Aplicación no encontrada\"}");
                return;
            }

            boolean tiempoAgotado = !ap.verificarTiempo();

            List<Pregunta> preguntas = em.createQuery(
                            "SELECT p FROM Pregunta p WHERE p.cuestionario = :c ORDER BY p.numero", Pregunta.class)
                    .setParameter("c", ap.getCuestionario())
                    .getResultList();

            for (Pregunta p : preguntas) {
                if (p.isEsEjemplo()) continue;
                String clave = String.valueOf(p.getNumero());
                if (respuestas.has(clave) && !respuestas.get(clave).isJsonNull()) {
                    String letra = respuestas.get(clave).getAsString();
                    if (letra != null && !letra.isEmpty()) {
                        RespuestaSujeto r = new RespuestaSujeto();
                        r.setAplicacionPrueba(ap);
                        r.setPregunta(p);
                        r.setRespuestaSeleccionada(letra);
                        em.persist(r);
                        ap.getRespuestas().add(r);
                    }
                }
            }

            ap.finalizarPrueba();
            int pd = ap.calcularPuntuacionDirecta();
            int percentil = ap.calcularPercentil();
            String interpretacion = ap.generarInterpretacion();
            ap.setEstado(EstadoAplicacion.CALIFICADO);

            JsonArray detalle = new JsonArray();
            for (Pregunta p : preguntas) {
                if (p.isEsEjemplo()) continue;
                JsonObject d = new JsonObject();
                d.addProperty("numero", p.getNumero());
                String dada = respuestas.has(String.valueOf(p.getNumero()))
                        && !respuestas.get(String.valueOf(p.getNumero())).isJsonNull()
                        ? respuestas.get(String.valueOf(p.getNumero())).getAsString() : null;
                d.addProperty("dada", dada);
                d.addProperty("clave", p.getRespuestaCorrecta());
                d.addProperty("correcta", dada != null && dada.equalsIgnoreCase(p.getRespuestaCorrecta()));
                detalle.add(d);
            }

            JsonObject salida = new JsonObject();
            salida.addProperty("puntuacionDirecta", pd);
            salida.addProperty("percentil", percentil);
            salida.addProperty("interpretacion", interpretacion);
            salida.addProperty("tiempoAgotado", tiempoAgotado);
            salida.add("detalle", detalle);

            XPersistence.commit();
            resp.getWriter().write(gson.toJson(salida));
        } catch (Exception e) {
            XPersistence.rollback();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\":\"No se pudo calificar la prueba\"}");
        }
    }

    /** Lee el cuerpo de la petición como texto plano. */
    private String leerCuerpo(HttpServletRequest req) throws IOException {
        try (BufferedReader r = req.getReader()) {
            return r.lines().collect(Collectors.joining());
        }
    }
}