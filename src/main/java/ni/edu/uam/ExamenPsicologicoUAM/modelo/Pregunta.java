package ni.edu.uam.ExamenPsicologicoUAM.modelo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.openxava.annotations.Hidden;

import javax.persistence.*;

/**
 * Representa cada ejercicio que compone un cuestionario: el proverbio a
 * interpretar, sus tres opciones (A, B, C), la clave correcta y si se trata
 * del ejemplo introductorio que no puntúa.
 */
@Entity
@Getter @Setter @AllArgsConstructor @NoArgsConstructor
public class Pregunta {

    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    @Hidden
    private String id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Cuestionario cuestionario;

    private int numero;

    @Column(length = 1000)
    private String enunciado;

    @Column(length = 1000)
    private String opcionA;
    @Column(length = 1000)
    private String opcionB;
    @Column(length = 1000)
    private String opcionC;

    @Column(length = 1)
    private String respuestaCorrecta;

    private boolean esEjemplo;
}
