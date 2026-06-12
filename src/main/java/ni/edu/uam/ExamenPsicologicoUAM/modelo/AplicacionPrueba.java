package ni.edu.uam.ExamenPsicologicoUAM.modelo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.openxava.annotations.Hidden;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AplicacionPrueba {

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
}