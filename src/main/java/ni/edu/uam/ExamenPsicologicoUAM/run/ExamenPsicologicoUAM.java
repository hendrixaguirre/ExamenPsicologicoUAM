package ni.edu.uam.ExamenPsicologicoUAM.run;

import org.openxava.util.*;

/**
 * Ejecuta esta clase para arrancar la aplicación.
 */

public class ExamenPsicologicoUAM {

	public static void main(String[] args) throws Exception {
		DBServer.start("ExamenPsicologicoUAM-db"); // Para usar tu propia base de datos comenta esta línea y configura src/main/webapp/META-INF/context.xml
		AppServer.run("ExamenPsicologicoUAM"); // Usa AppServer.run("") para funcionar en el contexto raíz
	}

}
