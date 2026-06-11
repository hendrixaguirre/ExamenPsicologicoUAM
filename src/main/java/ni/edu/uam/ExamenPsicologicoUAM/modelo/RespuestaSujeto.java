package ni.edu.uam.ExamenPsicologicoUAM.modelo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.openxava.annotations.Hidden;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RespuestaSujeto {
    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    @Hidden
    private String id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private AplicacionPrueba aplicacionPrueba;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Pregunta pregunta;

    @Column(length = 1)
    private String respuestaSeleccionada;


    private boolean correcta;
}
