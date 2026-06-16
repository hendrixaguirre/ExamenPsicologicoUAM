package ni.edu.uam.ExamenPsicologicoUAM.modelo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.openxava.annotations.Depends;
import org.openxava.annotations.Hidden;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.Period;

/**
 * Representa a la persona evaluada. Almacena sus datos de identificación y
 * demográficos. La edad no se almacena: se calcula a partir de fechaNacimiento.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Sujeto {

    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    @Hidden
    private String id;

    private String nombre;
    private String primerApellido;
    private String segundoApellido;
    private LocalDate fechaNacimiento;

    @Enumerated(EnumType.STRING)
    private Sexo sexo;

    private String estudiosRealizados;
    private String profesion;

    /**
     * Calcula la edad del sujeto a partir de su fecha de nacimiento.
     * Valor derivado: no se almacena en la base de datos.
     *
     * @return edad en años cumplidos; 0 si no hay fecha de nacimiento.
     */
    @Transient
    @Depends("fechaNacimiento")
    public int getEdad() {
        if (fechaNacimiento == null) return 0;
        return Period.between(fechaNacimiento, LocalDate.now()).getYears();
    }
}
