package ni.edu.uam.ExamenPsicologicoUAM.modelo;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.openxava.annotations.Hidden;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Getter
@Setter
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
    private int edad;

    private String sexo;


    private String estudiosRealizados;
    private String profesion;
}
