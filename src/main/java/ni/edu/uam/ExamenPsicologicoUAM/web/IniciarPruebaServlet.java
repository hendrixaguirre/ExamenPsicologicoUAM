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
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Inicia una aplicación de la prueba de Comprensión Verbal. Recibe los datos del
 * sujeto, crea el Sujeto y la AplicacionPrueba (estado EN_PROGRESO con horaInicio),
 * y devuelve las preguntas SIN la clave de respuestas.
 */
public class IniciarPruebaServlet extends HttpServlet {

    private final Gson gson = new Gson();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        EntityManager em = XPersistence.getManager();
        try {
            String cuerpo = leerCuerpo(req);
            JsonObject datos = cuerpo.isEmpty() ? new JsonObject() : gson.fromJson(cuerpo, JsonObject.class);

            Sujeto sujeto = new Sujeto();
            sujeto.setNombre(texto(datos, "nombre"));
            sujeto.setPrimerApellido(texto(datos, "primerApellido"));
            sujeto.setSegundoApellido(texto(datos, "segundoApellido"));
            sujeto.setEstudiosRealizados(texto(datos, "estudiosRealizados"));
            sujeto.setProfesion(texto(datos, "profesion"));
            if (datos.has("sexo") && !datos.get("sexo").isJsonNull()) {
                try { sujeto.setSexo(Sexo.valueOf(datos.get("sexo").getAsString())); }
                catch (IllegalArgumentException ignorado) { }
            }
            String fechaNac = texto(datos, "fechaNacimiento");
            if (fechaNac != null && !fechaNac.isEmpty()) {
                sujeto.setFechaNacimiento(LocalDate.parse(fechaNac));
            }
            em.persist(sujeto);

            Cuestionario cv = em.createQuery(
                            "SELECT c FROM Cuestionario c WHERE c.nombre = :n", Cuestionario.class)
                    .setParameter("n", "Comprensión Verbal")
                    .getSingleResult();

            AplicacionPrueba ap = new AplicacionPrueba();
            ap.setSujeto(sujeto);
            ap.setCuestionario(cv);
            ap.setEstado(EstadoAplicacion.ASIGNADO);
            ap.iniciarPrueba();
            em.persist(ap);

            List<Pregunta> preguntas = em.createQuery(
                            "SELECT p FROM Pregunta p WHERE p.cuestionario = :c ORDER BY p.numero", Pregunta.class)
                    .setParameter("c", cv)
                    .getResultList();

            JsonObject salida = new JsonObject();
            salida.addProperty("aplicacionId", ap.getId());
            salida.addProperty("instrucciones", cv.getInstrucciones());
            JsonArray arr = new JsonArray();
            for (Pregunta p : preguntas) {
                JsonObject jp = new JsonObject();
                jp.addProperty("numero", p.getNumero());
                jp.addProperty("enunciado", p.getEnunciado());
                jp.addProperty("esEjemplo", p.isEsEjemplo());
                JsonObject ops = new JsonObject();
                ops.addProperty("A", p.getOpcionA());
                ops.addProperty("B", p.getOpcionB());
                ops.addProperty("C", p.getOpcionC());
                jp.add("opciones", ops);
                arr.add(jp);
            }
            salida.add("preguntas", arr);

            XPersistence.commit();
            resp.getWriter().write(gson.toJson(salida));
        } catch (Exception e) {
            XPersistence.rollback();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\":\"No se pudo iniciar la prueba\"}");
        }
    }

    /** Lee el cuerpo de la petición como texto plano. */
    private String leerCuerpo(HttpServletRequest req) throws IOException {
        try (BufferedReader r = req.getReader()) {
            return r.lines().collect(Collectors.joining());
        }
    }

    /** Devuelve el valor de un campo de texto del JSON, o null si no está presente. */
    private String texto(JsonObject o, String campo) {
        return (o.has(campo) && !o.get(campo).isJsonNull()) ? o.get(campo).getAsString() : null;
    }
}