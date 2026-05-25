[![Review Assignment Due Date](https://classroom.github.com/assets/deadline-readme-button-22041afd0340ce965d47ae6ef1cefeee28c7c493a6346c4f15d667ab976d596c.svg)](https://classroom.github.com/a/qZKM4_z6)
# Grove – Knowledge Graph Learning Platform

**Course:** CS 2031 Desarrollo Basado en Plataforma  
**Team Members:**  
- Collazos Solis, Maxwell Lupo Gregorio  
- Cuba Zari, Renzo Cuba  
- Paz Raymondi, Elías Emanuel  
- Misael Osvaldo Aquino Hidalgo  
- José Luis Villalobos Jiménez  

---

## Índice
1. [Introducción](#introducción)
2. [Identificación del Problema o Necesidad](#identificación-del-problema-o-necesidad)
3. [Descripción de la Solución](#descripción-de-la-solución)
5. [Testing y Manejo de Errores](#testing-y-manejo-de-errores)
6. [Medidas de Seguridad Implementadas](#medidas-de-seguridad-implementadas)
7. [Eventos y Asincronía](#eventos-y-asincronía)
8. [GitHub & Management](#github--management)
9. [Conclusión](#conclusión)

---

## Introducción
**Contexto**  
En el ámbito educativo, los estudiantes necesitan materiales de estudio que reflejen las relaciones entre conceptos. Las herramientas tradicionales (PDFs, wikis lineales) no muestran cómo se conectan las ideas, carecen de rutas de aprendizaje guiadas y separan la teoría de la práctica. Grove nace como una plataforma colaborativa basada en grafos de conocimiento para superar estas limitaciones.

**Objetivos del Proyecto**  
- Permitir la creación, visualización y edición de un grafo de conocimiento colaborativo.  
- Ofrecer rutas de aprendizaje basadas en prerrequisitos y exploración no lineal.  
- Facilitar la práctica mediante ejercicios y flashcards integrados.  
- Garantizar la seguridad de los datos y la escalabilidad del sistema.

---

## Identificación del Problema o Necesidad
**Descripción del Problema**  
Los estudiantes universitarios tienen dificultades para dominar temas complejos porque los recursos disponibles no muestran la interconexión de los conceptos. No existe un camino guiado que les permita construir conocimiento paso a paso, y a menudo deben saltar entre múltiples herramientas para leer teoría y practicar.

**Justificación**  
Resolver este problema mejora la experiencia de aprendizaje, fomenta la colaboración y permite a los educadores estructurar mejor los contenidos. Una plataforma como Grove, que modela el conocimiento como un grafo, facilita la navegación semántica y ofrece un entorno integral para el estudio activo.

---

## Descripción de la Solución
**Funcionalidades Implementadas**  
- **Autenticación y autorización JWT**: registro, inicio de sesión y control de acceso basado en roles (USER, ADMIN).  
- **Gestión de conceptos (nodos del grafo)**: creación, lectura, actualización, eliminación, y relaciones de prerrequisito entre conceptos.  
- **Workspaces colaborativos**: espacios públicos y privados donde los usuarios pueden agrupar conceptos y compartirlos.  
- **Sistema de tags**: clasificación de conceptos en clústeres temáticos.  
- **Fork de conceptos**: copia independiente de un concepto a otro workspace, preservando la traza de origen.  
- **Búsqueda paginada**: búsqueda insensible a mayúsculas/minúsculas sobre títulos y nombres, con paginación configurable.  
- **Comentarios anidados**: discusión sobre conceptos, con hilos de respuestas.  
- **Notificaciones internas**: alertas cuando se crea un concepto en un workspace o cuando se comenta en un concepto propio.  
- **Envío de correos electrónicos asíncrono**: bienvenida al registro y notificación al ser añadido a un workspace.  
- **Ejercicios y flashcards**: entidades independientes para la práctica y el repaso.

**Tecnologías Utilizadas**  
- **Lenguaje:** Java 21  
- **Framework:** Spring Boot 4.0.5 (Web, Security, Data Neo4j, Validation, Mail)  
- **Base de datos:** Neo4j (grafos)  
- **Autenticación:** JWT (jjwt 0.12.6)  
- **Mapeo de objetos:** ModelMapper 3.2.4  
- **Herramientas de construcción:** Maven  
- **Testing:** JUnit 5, Mockito, TestContainers (Neo4j)  
- **Infraestructura:** Docker Compose para base de datos local, AuraDB para producción  
- **Documentación:** Postman, README

---

**Descripción de Entidades (Nodos)**  
- **User**: representa un usuario del sistema. Atributos: id, username, email, password (cifrada), role (USER/ADMIN), createdAt. Implementa UserDetails para Spring Security.  
- **Concept**: nodo central del conocimiento. Atributos: id, title, content, createdAt, updatedAt. Relaciones: PREREQUISITE (dirigida a otros Concept), TAGGED_AS (a Tag), BELONGS_TO (a Workspace), FORKED_FROM (a otro Concept), CREATED_BY (a User).  
- **Tag**: etiqueta para agrupar conceptos. Atributos: id, name, description, color. Relación inversa TAGGED_AS (desde Concept).  
- **Workspace**: espacio de trabajo colaborativo. Atributos: id, name, description, isPublic, createdAt. Relaciones: BELONGS_TO (entrante desde Concept), MEMBER_OF (entrante desde User), CREATED_BY (saliente a User).  
- **Comment**: comentario en un concepto. Atributos: id, text, createdAt. Relaciones: COMMENTED_BY (a User), ON_CONCEPT (a Concept), REPLIES_TO (a Comment).  
- **Exercise**: ejercicio práctico. Atributos: id, question, answer, explanation, type, options, difficulty, createdAt. Relaciones: HAS_CONCEPT (a Concept), BELONGS_TO (a User).  
- **Flashcard**: tarjeta de memoria. Atributos: id, front, back, hint, difficulty, createdAt. Relación HAS_FLASHCARD (entrante desde Concept).  
- **Notification**: aviso al usuario. Atributos: id, message, isRead, createdAt. Relación NOTIFIES (a User).

**Relaciones Clave**  
- `(:Concept)-[:PREREQUISITE]->(:Concept)` define los conocimientos previos necesarios.  
- `(:Concept)-[:BELONGS_TO]->(:Workspace)` asigna un concepto a un único workspace.  
- `(:User)-[:MEMBER_OF]->(:Workspace)` indica membresía.  
- `(:Concept)-[:FORKED_FROM]->(:Concept)` mantiene la trazabilidad de copias.  
- `(:Concept)-[:CREATED_BY]->(:User)` registra el autor.

---

## Testing y Manejo de Errores
**Niveles de Testing Realizados**  
- **Unitarias (Repositorio):** Utilizando `@SpringBootTest` con TestContainers y Neo4j real, se prueban consultas personalizadas como búsqueda por título, prerrequisitos recursivos y detección de ciclos. Todas las pruebas siguen la nomenclatura BDD (`shouldXxxWhenYyy`).  
- **Unitarias (Servicio):** Con Mockito y JUnit 5, se verifican las reglas de negocio, el manejo de excepciones y la publicación de eventos.  
- **Integración (Controlador):** Con `MockMvc` y Spring Security Test, se validan endpoints REST, códigos de estado HTTP y respuestas paginadas, simulando autenticación.

**Resultados**  
Los tests cubren escenarios exitosos, de error (404, 403, 409) y de validación. Se corrigieron defectos como la inicialización de listas de relaciones y la detección de ciclos en prerrequisitos.

**Manejo de Errores**  
Se implementaron **8 excepciones personalizadas** organizadas por categoría:
- `ResourceNotFoundException` (404)
- `UserAlreadyExistsException`, `DuplicateResourceException`, `AlreadyMemberException` (409)
- `UsernameNotFoundException` (404)
- `InvalidOperationException`, `BadRequestException` (400)
- `ForbiddenException` (403)

Un `GlobalExceptionHandler` centralizado (`@RestControllerAdvice`) captura todas ellas y devuelve `ProblemDetail` con el código HTTP correcto, garantizando respuestas de error consistentes.

---

## Medidas de Seguridad Implementadas
**Seguridad de Datos**  
- **Autenticación JWT:** tokens firmados con HMAC-SHA256, con expiración configurable (acceso y refresco).  
- **Cifrado de contraseñas:** BCryptPasswordEncoder.  
- **Control de acceso:** rutas públicas `/api/v1/auth/**`; el resto requieren token válido. Uso de `@WithMockUser` en tests.  
- **Autorización por roles:** administradores (`ROLE_ADMIN`) pueden modificar/eliminar cualquier recurso; los usuarios normales solo sus propios recursos. Verificaciones en servicios (`ConceptorService`, `WorkspaceService`, `ExerciseService`).  
- **Validación de pertenencia a workspace:** para crear conceptos en workspaces privados se exige membresía.

**Prevención de Vulnerabilidades**  
- **CSRF deshabilitado** para APIs REST stateless.  
- **Protección contra ciclos en prerrequisitos:** consulta Cypher que evita cerrar un camino existente.  
- **Validación de datos de entrada** mediante Jakarta Bean Validation (`@NotBlank`, `@Size`, `@Email`).  
- **Manejo de excepciones global** para no filtrar información sensible.  
- **CORS pendiente de configuración final** para entornos de producción.

---

## Eventos y Asincronía
**Eventos Implementados**  
1. `ConceptCreatedNotificationEvent`: se dispara al crear un concepto. El listener `NotificationEventListener` genera notificaciones para todos los miembros del workspace (excepto el creador).  
2. `NewCommentNotificationEvent`: al comentar en un concepto, notifica al autor del concepto.  
3. `WelcomeEmailEvent`: al registrarse un usuario, envía un correo de bienvenida (asíncrono).  
4. `WorkspaceInvitationEvent`: al añadir un miembro a un workspace, envía un correo informativo (asíncrono).

**Procesamiento Asíncrono**  
- Configuración de `ThreadPoolTaskExecutor` con 2 hilos base, máximo 5, cola de 100.  
- Los métodos `handleWelcomeEmail` e `handleWorkspaceInvitation` están anotados con `@Async` y usan `JavaMailSender` para el envío de correos.  
- Se utiliza `ApplicationEventPublisher` en los servicios para desacoplar la lógica de negocio de las tareas secundarias.

**Servicio de Correo Electrónico**  
- Configurado con Gmail SMTP (o cualquier proveedor mediante variables de entorno).  
- Utiliza `SimpleMailMessage` y envía correos en segundo plano.  
- Los errores de envío se registran sin interrumpir el flujo principal.

---

## GitHub & Management
**Gestión del Proyecto**  
- Se utilizó **GitHub Projects** para organizar las tareas en columnas (To Do, In Progress, Review, Done).  
- Las historias de usuario y bugs se registraron como issues, asignados a cada integrante.  
- Se establecieron milestones semanales para cumplir con los hitos de la entrega.

**Control de Versiones y CI/CD**  
- Flujo de trabajo basado en `develop` como rama principal de integración, y ramas `feature/*` para cada funcionalidad.  
- Pull Requests obligatorios con revisión de código antes de mergear a `develop`.  
- Se utilizó **GitHub Actions** para la ejecución automática de tests en cada push a `develop` y `feature/*`, asegurando la calidad del código.  
- El archivo `pom.xml` incluye la configuración de Maven para compilar con Java 21 y ejecutar los tests unitarios y de integración.

---

## Conclusión
**Logros del Proyecto**  
Se ha desarrollado un backend robusto y escalable para una plataforma de aprendizaje colaborativo basada en grafos. Se implementaron todas las funcionalidades del MVP: creación de conceptos, workspaces, prerrequisitos, búsqueda, notificaciones, correos electrónicos y control de acceso granular. El uso de Neo4j permite modelar relaciones complejas de manera natural y eficiente.

---
