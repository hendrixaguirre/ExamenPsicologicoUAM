package ni.edu.uam.ExamenPsicologicoUAM.run;

import ni.edu.uam.ExamenPsicologicoUAM.modelo.Cuestionario;
import ni.edu.uam.ExamenPsicologicoUAM.modelo.Pregunta;
import org.openxava.jpa.XPersistence;

import javax.persistence.EntityManager;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * Siembra el cuestionario de Comprensión Verbal con sus 31 ítems y la clave de
 * respuestas cuando arranca la aplicación web. Si el cuestionario ya existe,
 * no hace nada (evita duplicar los datos).
 */
public class CargadorDatos implements ServletContextListener {

    /** Se ejecuta automáticamente al iniciar la aplicación web. */
    @Override
    public void contextInitialized(ServletContextEvent evento) {
        cargar();
    }

    /**
     * Crea el cuestionario de Comprensión Verbal y sus 31 preguntas si aún no existen.
     */
    public static void cargar() {
        EntityManager em = XPersistence.getManager();
        try {
            Long existentes = (Long) em.createQuery(
                            "SELECT COUNT(c) FROM Cuestionario c WHERE c.nombre = :n")
                    .setParameter("n", "Comprensión Verbal")
                    .getSingleResult();
            if (existentes != null && existentes > 0) {
                XPersistence.commit();
                return;
            }

            Cuestionario cv = new Cuestionario();
            cv.setNombre("Comprensión Verbal");
            cv.setDescripcion("Prueba de la Batería Factorial de Aptitudes (BFA) que evalúa la capacidad de interpretar el significado de proverbios y pensamientos.");
            cv.setInstrucciones("Cada pensamiento principal va acompañado de tres opciones (A, B, C). Elija aquella cuya significación más se aproxima o menos se separa del pensamiento principal. Una sola respuesta por ejercicio. Dispone de 15 minutos.");
            cv.setEjemplo("A VECES UN GIGANTE NECESITA LOS SERVICIOS DE UN ENANO. (Respuesta correcta: B)");
            em.persist(cv);

            agregar(em, cv, 1, true, "B",
                    "A VECES UN GIGANTE NECESITA LOS SERVICIOS DE UN ENANO.",
                    "El éxito es a menudo difícil de prever.",
                    "A menudo se tiene necesidad de alguien inferior a uno.",
                    "Dar es una cosa fácil.");

            agregar(em, cv, 2, false, "C",
                    "SI LA TECNICA NOS HACE PERDER EL ESPIRITU, ES QUE NO MERECEMOS CONSERVARLO. (J. ROSTAND)",
                    "Si no dejamos invadir por la técnica estamos perdidos.",
                    "El espíritu y la técnica son irreconciliables.",
                    "Si el hombre se destruye por la técnica sólo él es responsable de su derrota.");

            agregar(em, cv, 3, false, "C",
                    "Y POR TANTO NO PUEDE HACER NADA PARA LA FELICIDAD DE LOS DEMAS AQUEL QUE ES INCAPAZ DE SER FELIZ EL MISMO. (A. GIDE)",
                    "La felicidad es lo único que puede darse sin poseerla.",
                    "Toda felicidad es altruista.",
                    "Seamos felices para poder hacer felices a otros.");

            agregar(em, cv, 4, false, "A",
                    "PARA QUE PONER ESTA PALABRA EN SINGULAR QUE SE NOS ESCAPA, SI PUESTA EN PLURAL LA ENCONTRAMOS TODOS LOS DIAS. (COLETTE)",
                    "El mito de la felicidad absoluta oculta la realidad de las alegrías cotidianas.",
                    "La felicidad es un mito que nos resulta imposible alcanzar.",
                    "Esperemos la felicidad que pueda traernos el mañana.");

            agregar(em, cv, 5, false, "B",
                    "PARA CADA UNO ES BARBARIE AQUELLO QUE NO LE ES FAMILIAR (MONTAIGNE)",
                    "El uso define la barbarie como aquello que es propio de un individuo.",
                    "La noción de barbarie es relativa en cada civilización.",
                    "La barbarie es difícil de definir.");

            agregar(em, cv, 6, false, "A",
                    "EN UN ACTO TAN COMPLEJO COMO ES EL JUICIO ESTETICO DISTINGUIMOS CON DIFICULTAD AQUELLO QUE NOSOTROS APORTAMOS DE LO QUE PROCEDE DE LAS ADQUISICIONES CULTURALES. (J. CHARBONNEAUX)",
                    "Es difícil separar el juicio personal de la impregnación recibida cuando decimos: \"Esto es bonito\".",
                    "El juicio estético es complejo ya que pone en juego toda una educación.",
                    "Un ser sin cultura no podrá emitir un juicio estético.");

            agregar(em, cv, 7, false, "B",
                    "RIE BIEN EL QUE RIE ULTIMO",
                    "Es necesario esperar haber pasado la última para conocer el final de la historia.",
                    "A pícaro, pícaro y medio.",
                    "Lo importante es decir la última palabra.");

            agregar(em, cv, 8, false, "C",
                    "UNA GOLONDRINA NO HACE VERANO",
                    "Las apariencias no son la realidad.",
                    "De la mano a la boca se pierde la sopa.",
                    "Debe tenerse cuidado con las apariencias, son a menudo engañosas.");

            agregar(em, cv, 9, false, "C",
                    "LA SOBREPROTECCIÓN RETRASA LA MADUREZ DEL NIÑO, CUANDO PODRIA PENSARSE QUE LO BENEFICIA. (P. GASCAR)",
                    "Un niño sobreprotegido por sus padres se beneficia de una madurez psicológica más lenta, por lo que tiene un yo más sólido.",
                    "Es necesario proteger incluso con exceso, al niño que madura lentamente.",
                    "Si los padres se preocupasen menos de proteger al niño, éste llegaría más rápidamente a ser autónomo.");

            agregar(em, cv, 10, false, "A",
                    "QUIEN HA APRENDIDO A MORIR, HA DESAPRENDIDO A SERVIR. (MONTAIGNE)",
                    "No puede hacerse esclavo a aquel que no tiene medio a la muerte.",
                    "Es necesario saber servir para saber morir.",
                    "Si no se sirve a nadie no se aprecia la vida.");

            agregar(em, cv, 11, false, "B",
                    "LOS PERROS LADRAN, LA CARAVANA SIGUE.",
                    "Para un corazón valiente, nada hay imposible.",
                    "Haz bien y deja que digan.",
                    "El hombre que sigue su quimera, no oye las advertencias que le da el cielo.");

            agregar(em, cv, 12, false, "B",
                    "LOS ÁRBOLES NO DEJAN VER EL BOSQUE.",
                    "Siempre se ve la paja en el ojo ajeno y no se ve la viga en el propio.",
                    "Quien mira demasiado por donde va, no ve a donde debe ir.",
                    "Aquellos que se aplican a las pequeñas cosas generalmente se vuelven incapaces de las grandes.");

            agregar(em, cv, 13, false, "C",
                    "ES PRECISO QUE UNA PUERTA BONITA SEA PRIMERO UNA PUERTA. (ALAIN)",
                    "El objeto útil deja de ser bello.",
                    "Es bastante más bonito porque es inútil.",
                    "Lo funcional es una condición necesaria para lo bello.");

            agregar(em, cv, 14, false, "B",
                    "PUEDO NEGAR UNA COSA SIN VERME OBLIGADO A DESHONRARLA Y SIN NEGAR A LOS DEMAS EL DERECHO DE CREER EN ELLA. (A. CAMUS)",
                    "Incluso si soy escéptico debo respetar las ideas de los otros.",
                    "Cualquiera que sea mi opinión, mi compromiso no llega hasta la intolerancia.",
                    "Incluso si creo en una cosa no puedo imponerla a otros.");

            agregar(em, cv, 15, false, "C",
                    "MALDITO SEA AQUEL POR EL CUAL LLEGA EL ESCANDALO.",
                    "No hay humo sin fuego.",
                    "Ten cuidado que al manchar a los otros no te salpiques tu mismo.",
                    "Quien siembra vientos recoge tempestades.");

            agregar(em, cv, 16, false, "A",
                    "CUANTO MAS ALTO SUBE EL MONO, MAS SE LE VE EL TRASERO.",
                    "El hombre que se hace ver demasiado no tiene vida intima.",
                    "Nobleza obliga.",
                    "Una ascensión no está jamás exenta de peligro.");

            agregar(em, cv, 17, false, "A",
                    "LA PERSONA PRUDENTE RIEGA LENTAMENTE, LA INSENSATA LO INUNDA TODO DE GOLPE. (FLORIAN)",
                    "Quien quiere llegar lejos arregla su cabalgadura.",
                    "Poco a poco hilaba la vieja el copo.",
                    "El tiempo no importa para el negocio.");

            agregar(em, cv, 18, false, "B",
                    "CADA DESEO ME HA ENRIQUECIDO MÁS QUE LA POSESION SIEMPRE FALSA DEL OBJETO DE MI DESEO. (A. GIDE)",
                    "Una cosa tiene tanto más valor cuanto más tiempo ha sido deseada.",
                    "La verdadera riqueza reside en el deseo y no en la posesión.",
                    "La posesión es un espejismo decepcionante.");

            agregar(em, cv, 19, false, "C",
                    "QUE IMPORTA LA BOTELLA SIEMPRE QUE SE TENGA LA BORRACHERA.",
                    "El fin justifica los medios.",
                    "Es a menudo en las pequeñas ostras, donde se encuentran las más bellas perlas.",
                    "Los decorados están hechos de cartón, los accesorios en papel pero la magia del teatro suscita la ilusión.");

            agregar(em, cv, 20, false, "B",
                    "A BURRA VIEJA, ALBARDA NUEVA.",
                    "El hábito no hace al monje.",
                    "La riqueza ilumina la mediocridad.",
                    "Es en las viejas ollas donde se hace la mejor sopa.");

            agregar(em, cv, 21, false, "A",
                    "TERMINADA LA FIESTA, ADIOS AL SANTO.",
                    "En la mayor parte del mundo, la prosperidad no tiene compañía más segura que la ignorancia y el olvido de los servicios prestados.",
                    "El tiempo pasa y trae consigo recuerdos agradables.",
                    "Lo que el viento se llevó.");

            agregar(em, cv, 22, false, "C",
                    "UNICAMENTE LA RISA ESCAPA AL CONTROL. (N. CLIFFORD BARNEY)",
                    "La risa es el sonido del espíritu; ciertas risas parecen tontas como una pieza suena a falsa. (Ed. y J. Goncourt)",
                    "El hombre no ríe más que cuando sabe que una cosa es cómica. (A. Porchia)",
                    "La risa se resiste cuando se la llama y explota cuando se la teme.");

            agregar(em, cv, 23, false, "A",
                    "MUCHAS PERSONAS PODRIAN VER SI SE QUITARAN SUS GAFAS. (F. HERBEL)",
                    "Ver me cuesta abrir los ojos a todo lo que no quisiera ver. (A. Porchia)",
                    "Disimula su rostro con una máscara, oculta su mirada con gafas.",
                    "Hay espíritus parecidos a espejos convexos o cóncavos que representan los objetos tal como los reciben, pero que no los reciben jamás tal como son. (J. Joubert)");

            agregar(em, cv, 24, false, "C",
                    "ES MAS DIFICIL QUE PERDURE LA BUENA FORTUNA QUE LA MALA SI LAS HADAS HAN ADORNADO TU CUNA DESCONFIA. (ALAIN)",
                    "Para vivir felices, vivamos ocultos. (Florian)",
                    "Seguir su estrella, no es elegir su vida.",
                    "El destino que designa, asigna.");

            agregar(em, cv, 25, false, "B",
                    "NO HAY GRANDES HOMBRES SIN UN GRAMO DE LOCURA. (DIDEROT)",
                    "No se ha llegado todavía a un acuerdo sobre si el genio es la perfección de lo que va a morir, o la singularidad de lo que va a nacer. (P.J. Toulet)",
                    "El genio, en definitiva, no es más que la forma de percibir de un modo poco usual. (W. Jones)",
                    "Los grandes hombres no son siempre aquellos que tienen más ingenio.");

            agregar(em, cv, 26, false, "B",
                    "HABLO CON LA IDEA DE CALLARME... Y HABLO. (A. PORCHIA)",
                    "Las palabras son a los pensamientos lo que el oro es al diamante; son necesarias para ponerlos de manifiesto pero en poca cantidad. (Voltaire)",
                    "En muchos hombres la palabra precede al pensamiento. Saben solamente lo que piensan después de haber oído lo que dicen. (G. Le-Bon)",
                    "Sólo cuando uno piensa que no es escuchado y continúa hablando, es cuando empieza a decir cosas que merecen ser escuchadas. (R. de Montesquieu)");

            agregar(em, cv, 27, false, "C",
                    "LA VERDAD ES COMO DIOS, NO SE MUESTRA CON EL ROSTRO DESCUBIERTO. (GOETHE)",
                    "La verdad no es una bella mujer escondida en el fondo de un pozo, sino un tímido pájaro que sólo la astucia puede apresar. (J. Conrad)",
                    "Una verdad muy clara pronto deja de ser una verdad fecunda. (G. Lebon)",
                    "A menos que se ame la verdad no se sabrá conocerla. (B. Pascal)");

            agregar(em, cv, 28, false, "C",
                    "EL ABURRIMIENTO ES QUIZAS UN PRIVILEGIO. LOS IMBECILES NO SIENTEN ABURRIRSE, QUIZAS NO SE ABURREN. (Ed. y J. de GONCOURT)",
                    "No es uno de los menores méritos de la gente de mucho el saber aburrirse sin que lo parezca.",
                    "En el teatro, el recreo de la vista y el oído dificulta mucho la reflexión. (Goethe)",
                    "Rosine: \"El aburrimiento me mata\". Fígaro: \"Lo creo, no engorda más que a los tontos\". (Beaumarchais)");

            agregar(em, cv, 29, false, "A",
                    "CUANDO NO SE TIENE PELO, SE ENCUENTRAN RIDICULAS LAS CABELLERAS. (LEAUTAUD)",
                    "Los viejos creen lamentarse de su tiempo, se equivocan, no se lamentan más que de su edad. (V. Hugo)",
                    "Tanto sabe la juventud como puede la vejez. (Proverbio)",
                    "A los viejos les gusta dar buenos consejos para consolarse de no tener edad para dar malos ejemplos. (La Rochefoucault)");

            agregar(em, cv, 30, false, "B",
                    "LO QUE PROBABLEMENTE FALSEA TODA LA VIDA, ES EL ESTAR CONVENCIDO DE QUE SE DICE LA VERDAD PORQUE SE DICE LO QUE SE PIENSA. (S. GUITRY)",
                    "A cada uno su verdad. (Pirandello)",
                    "El principal uso que hacemos de nuestro amor a la verdad, es el de persuadirnos de que aquello que amamos es verdadero. (P. Nicole)",
                    "El deseo de hablar de nosotros y de mostrar nuestros defectos del lado que queremos, constituye una gran parte de nuestra sinceridad. (La Rochefoucault)");

            agregar(em, cv, 31, false, "C",
                    "DESDE HACE TREINTA Y CINCO AÑOS REVIENTO DE ORGULLO, ES MI MANERA DE MORIR DE VERGUENZA. (JEAN PAUL SARTRE)",
                    "Es necesario matar el orgullo sin herirlo, ya que si se le hiere no muere. (Rivarol)",
                    "El orgullo que a menudo nos inspira tanta envidia también nos sirve a menudo para moderarla. (La Rochefoucault)",
                    "El amor propio encuentra su goce en el sufragio y aprobación de los hombres, el último grado del orgullo es gozar de su desprecio. (S. de Meihan)");

            XPersistence.commit();
        } catch (Exception e) {
            XPersistence.rollback();
            throw new RuntimeException("Error al sembrar el cuestionario de Comprensión Verbal", e);
        }
    }

    /**
     * Crea y persiste una pregunta asociada al cuestionario indicado.
     *
     * @param em        gestor de entidades activo.
     * @param cv        cuestionario al que pertenece la pregunta.
     * @param numero    número de orden del ítem (1 a 31).
     * @param esEjemplo true si es el ejemplo introductorio (no puntúa).
     * @param correcta  letra de la opción correcta (A, B o C).
     * @param enunciado proverbio o pensamiento principal.
     * @param a         texto de la opción A.
     * @param b         texto de la opción B.
     * @param c         texto de la opción C.
     */
    private static void agregar(EntityManager em, Cuestionario cv, int numero, boolean esEjemplo,
                                String correcta, String enunciado, String a, String b, String c) {
        Pregunta p = new Pregunta();
        p.setCuestionario(cv);
        p.setNumero(numero);
        p.setEsEjemplo(esEjemplo);
        p.setRespuestaCorrecta(correcta);
        p.setEnunciado(enunciado);
        p.setOpcionA(a);
        p.setOpcionB(b);
        p.setOpcionC(c);
        em.persist(p);
    }
}