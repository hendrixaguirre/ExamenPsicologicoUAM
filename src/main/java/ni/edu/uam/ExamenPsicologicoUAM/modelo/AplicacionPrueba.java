package ni.edu.uam.ExamenPsicologicoUAM.modelo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.openxava.annotations.Hidden;
import org.openxava.annotations.View;

import javax.persistence.*;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Representa un evento concreto de evaluación: la ocasión en que un sujeto
 * realiza un cuestionario. Controla el ciclo de vida de la prueba y concentra
 * la lógica de calificación (puntuación directa, percentil e interpretación).
 */
@Entity
@View(members =
        "sujeto;" +
                "cuestionario;" +
                "fechaAplicacion, horaInicio, estado;" +
                "puntuacionDirecta, percentil, interpretacion;" +
                "respuestas"
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AplicacionPrueba {
    /** Límite de tiempo de la prueba, en minutos. */
    private static final int MINUTOS_LIMITE = 15;

    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    @Hidden
    private String id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Sujeto sujeto;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Cuestionario cuestionario;

    private LocalDate fechaAplicacion;
    private LocalDateTime horaInicio;

    @Enumerated(EnumType.STRING)
    private EstadoAplicacion estado;

    @OneToMany(mappedBy = "aplicacionPrueba", cascade = CascadeType.ALL, orphanRemoval = true)
    private Collection<RespuestaSujeto> respuestas = new ArrayList<>();

    /**
     * Inicia la prueba: registra la fecha y hora de inicio y pasa al estado EN_PROGRESO.
     * Solo es válido desde el estado ASIGNADO; evita iniciar dos veces la misma aplicación.
     */
    public void iniciarPrueba() {
        if (estado != EstadoAplicacion.ASIGNADO)
            throw new IllegalStateException("La prueba solo puede iniciarse desde el estado ASIGNADO.");
        fechaAplicacion = LocalDate.now();
        horaInicio = LocalDateTime.now();
        estado = EstadoAplicacion.EN_PROGRESO;
    }

    /**
     * Finaliza la prueba (por envío manual o por tiempo agotado) y pasa al estado FINALIZADO.
     * Solo es válido si la aplicación está EN_PROGRESO; bloquea cambios posteriores.
     */
    public void finalizarPrueba() {
        if (estado != EstadoAplicacion.EN_PROGRESO)
            throw new IllegalStateException("Solo se puede finalizar una prueba que está EN_PROGRESO.");
        estado = EstadoAplicacion.FINALIZADO;
    }

    /**
     * Comprueba si aún queda tiempo dentro del límite de 15 minutos desde horaInicio.
     *
     * @return true si todavía hay tiempo disponible; false si se agotó o la prueba no ha iniciado.
     */
    public boolean verificarTiempo() {
        if (horaInicio == null) return false;
        long transcurridos = Duration.between(horaInicio, LocalDateTime.now()).toMinutes();
        return transcurridos < MINUTOS_LIMITE;
    }

    /**
     * Calcula la puntuación directa (PD): número de respuestas acertadas.
     * Los errores no restan y el ejercicio de ejemplo no puntúa.
     *
     * @return cantidad de aciertos del sujeto.
     */
    public int calcularPuntuacionDirecta() {
        if (respuestas == null) return 0;
        int aciertos = 0;
        for (RespuestaSujeto r : respuestas) {
            if (r.isCorrecta()) aciertos++;
        }
        return aciertos;
    }

    /**
     * Convierte la puntuación directa a percentil según la tabla de baremos
     * de Comprensión Verbal (Normas Nacionales BFA).
     *
     * @return percentil correspondiente (entre 1 y 99).
     */
    public int calcularPercentil() {
        int pd = calcularPuntuacionDirecta();
        if (pd >= 17) return 99;
        switch (pd) {
            case 16: return 97;
            case 15: return 90;
            case 14: return 85;
            case 13: return 80;
            case 12: return 70;
            case 11: return 60;
            case 10: return 50;
            case 9:  return 40;
            case 8:  return 30;
            case 7:  return 15;
            case 6:  return 10;
            case 5:  return 5;
            default: return 1; // PD de 0 a 4
        }
    }

    /**
     * Genera la interpretación textual del desempeño según el percentil obtenido.
     * Solo es válida si la aplicación está FINALIZADA o CALIFICADA.
     *
     * @return nivel de desempeño: "Por debajo del promedio", "Promedio/adecuado" o "Superior".
     */
    public String generarInterpretacion() {
        if (estado != EstadoAplicacion.FINALIZADO && estado != EstadoAplicacion.CALIFICADO)
            throw new IllegalStateException("La interpretación solo está disponible al finalizar la prueba.");
        int percentil = calcularPercentil();
        if (percentil < 50) return "Por debajo del promedio";
        if (percentil <= 75) return "Promedio/adecuado";
        return "Superior";
    }
    /**
     * Puntuación directa para mostrar en la interfaz administrativa.
     * @return número de aciertos.
     */
    @Transient
    public int getPuntuacionDirecta() {
        return calcularPuntuacionDirecta();
    }

    /**
     * Percentil para mostrar en la interfaz administrativa.
     * @return percentil, o 0 si la prueba aún no fue calificada.
     */
    @Transient
    public int getPercentil() {
        if (estado != EstadoAplicacion.FINALIZADO && estado != EstadoAplicacion.CALIFICADO) return 0;
        return calcularPercentil();
    }

    /**
     * Interpretación del desempeño para mostrar en la interfaz administrativa.
     * @return nivel de desempeño, o un guion si la prueba aún no fue calificada.
     */
    @Transient
    public String getInterpretacion() {
        if (estado != EstadoAplicacion.FINALIZADO && estado != EstadoAplicacion.CALIFICADO) return "?";
        return generarInterpretacion();
    }

}