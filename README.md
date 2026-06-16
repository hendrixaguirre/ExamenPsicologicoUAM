# ExamenPsicologicoUAM

## Descripción

**ExamenPsicologicoUAM** es un sistema desarrollado para digitalizar la aplicación de la prueba de **Comprensión Verbal** de la **Batería Factorial de Aptitudes (BFA)**.

El proyecto permite registrar evaluados, administrar cuestionarios, aplicar pruebas de forma digital, controlar el tiempo de evaluación, corregir automáticamente las respuestas y generar resultados e interpretaciones de manera inmediata.

La solución fue desarrollada utilizando **Java**, **OpenXava**, **PostgreSQL**, **HTML**, **CSS** y **JavaScript**.

---

## Objetivo

Automatizar el proceso de aplicación y corrección de la prueba de Comprensión Verbal, reduciendo errores humanos, mejorando los tiempos de respuesta y permitiendo el almacenamiento digital de la información.

---

## Funcionalidades Principales

### Administración del Sistema

* Gestión de sujetos evaluados.
* Gestión de cuestionarios.
* Gestión de preguntas.
* Gestión de aplicaciones de prueba.
* Consulta de respuestas y resultados.

### Aplicación de la Prueba

* Visualización de instrucciones.
* Registro de datos del evaluado.
* Cronómetro de 15 minutos.
* Navegación entre preguntas.
* Validación de campos obligatorios.
* Finalización manual o automática por tiempo agotado.

### Calificación Automática

* Cálculo de puntuación directa.
* Cálculo de percentil.
* Generación automática de interpretación.
* Detalle de corrección por pregunta.

---

## Tecnologías Utilizadas

### Backend

* Java
* OpenXava
* JPA / Hibernate
* Servlets
* Maven

### Frontend

* HTML5
* CSS3
* JavaScript

### Base de Datos

* PostgreSQL

### Control de Versiones

* Git
* GitHub

---

## Arquitectura del Sistema

### Modelo

Las principales entidades del sistema son:

* Sujeto
* Cuestionario
* Pregunta
* AplicacionPrueba
* RespuestaSujeto

### Enumeraciones

* Sexo
* EstadoAplicacion

### Componentes Web

* IniciarPruebaServlet
* CalificarPruebaServlet

### Carga Inicial

* CargadorDatos

Este componente registra automáticamente el cuestionario y las preguntas de Comprensión Verbal cuando la aplicación inicia por primera vez.

---

## Flujo General del Sistema

1. El usuario accede a la prueba.
2. Completa sus datos personales.
3. El sistema crea una AplicacionPrueba.
4. Se muestran las preguntas del cuestionario.
5. Se inicia el cronómetro de 15 minutos.
6. El usuario responde las preguntas.
7. Las respuestas se envían al servidor.
8. El sistema corrige automáticamente.
9. Se calculan los resultados.
10. Se muestra la interpretación obtenida.
11. Los datos quedan almacenados en PostgreSQL.

---

## Estructura Principal del Proyecto

```text
src/
├── main/
│   ├── java/
│   │   └── ni.edu.uam.ExamenPsicologicoUAM/
│   │       ├── modelo/
│   │       ├── run/
│   │       └── web/
│   ├── resources/
│   └── webapp/
│       ├── comprension_verbal.html
│       ├── comprension_verbal.css
│       └── comprension_verbal.js
```

---

## Instalación y Ejecución

### Requisitos

* Java 21 o superior
* Maven
* PostgreSQL
* Apache Tomcat
* OpenXava

### Clonar el repositorio

```bash
git clone https://github.com/hendrixaguirre/ExamenPsicologicoUAM.git
```

### Compilar el proyecto

```bash
mvn clean compile
```

### Generar el paquete

```bash
mvn package
```

Posteriormente, desplegar el archivo WAR generado en Apache Tomcat.

---

## Conceptos Aplicados

* Programación Orientada a Objetos (POO)
* Encapsulamiento
* Abstracción
* Persistencia de Datos
* Arquitectura Cliente-Servidor
* Desarrollo Web
* Control de Versiones
* Documentación JavaDoc

---

## Equipo de Desarrollo

Proyecto desarrollado por estudiantes de Ingeniería en Sistemas de Información de la Universidad Americana (UAM).

---

## Licencia

Proyecto desarrollado con fines académicos para la asignatura de Programación Orientada a Objetos.
