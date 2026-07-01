package com.app.grove.config;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.app.grove.concept.domain.Concept;
import com.app.grove.concept.infrastructure.ConceptRepository;
import com.app.grove.user.domain.Role;
import com.app.grove.user.domain.User;
import com.app.grove.user.infrastructure.UserRepository;
import com.app.grove.workspace.domain.Workspace;
import com.app.grove.workspace.infrastructure.WorkspaceRepository;
import com.app.grove.tag.domain.Tag;
import com.app.grove.tag.infrastructure.TagRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner{
    private final PasswordEncoder passwordEncoder;
    private final WorkspaceRepository workspaceRepository;
    private final ConceptRepository conceptRepository;
    private final UserRepository userRepository;
    private final TagRepository tagRepository;

    @Value("${ADMIN_NAME}")
    private String adminName;

    @Value("${ADMIN_EMAIL}")
    private String adminEmail;

    @Value("${ADMIN_PASSWORD}")
    private String adminPassword;

    @Override
    public void run(String... args) throws Exception {
        Optional<User> existingAdmin = userRepository.findByUsername(adminName);

        if (existingAdmin.isEmpty()) {
            User admin = new User();
            admin.setUsername(adminName);
            admin.setEmail(adminEmail);
            admin.setPassword(passwordEncoder.encode(adminPassword));
            admin.setCreatedAt(LocalDateTime.now());
            admin.setRole(Role.ROLE_ADMIN);
            User savedAdmin = userRepository.save(admin);

            Workspace publicWorkspace = new Workspace();
            publicWorkspace.setName("Grove Global Community");
            publicWorkspace.setDescription("Espacio público donde todos los usuarios pueden ver conceptos base.");
            publicWorkspace.setPublic(true);
            publicWorkspace.setCreatedAt(LocalDateTime.now());
            publicWorkspace.setMembers(new ArrayList<>(Arrays.asList(savedAdmin)));
            Workspace savedWorkspace = workspaceRepository.save(publicWorkspace);

            Concept welcomeConcept = crearConceptoBase("¿Qué es Grove?", "Grove es una plataforma colaborativa basada en grafos de conocimiento para el aprendizaje.", savedWorkspace, savedAdmin);
            
            // Conceptos de Matemáticas y Estadística
            Concept calculoDif = crearConceptoBase("Cálculo Diferencial", "Estudio de las tasas de cambio (derivadas).", savedWorkspace, savedAdmin);
            Concept calculoInt = crearConceptoBase("Cálculo Integral", "Estudio de las áreas bajo curvas (integrales).", savedWorkspace, savedAdmin);
            Concept algebraLineal = crearConceptoBase("Álgebra Lineal", "Estudio de vectores, matrices y transformaciones lineales.", savedWorkspace, savedAdmin);
            Concept probabilidad = crearConceptoBase("Probabilidad", "Medida de la certidumbre de eventos aleatorios.", savedWorkspace, savedAdmin);
            Concept estadisticaDesc = crearConceptoBase("Estadística Descriptiva", "Resumen y descripción cuantitativa de datos.", savedWorkspace, savedAdmin);
            Concept estadisticaInf = crearConceptoBase("Estadística Inferencial", "Deducción de propiedades poblacionales a partir de muestras.", savedWorkspace, savedAdmin);
            Concept cadenasMarkov = crearConceptoBase("Cadenas de Markov", "Modelos estocásticos que dependen solo del estado actual.", savedWorkspace, savedAdmin);

            // Algoritmos y Optimización
            Concept complejidad = crearConceptoBase("Análisis de Complejidad", "Evaluación de uso de tiempo y espacio de algoritmos (Notación Big-O).", savedWorkspace, savedAdmin);
            Concept estDatos = crearConceptoBase("Estructuras de Datos Avanzadas", "Organización eficiente de datos complejos.", savedWorkspace, savedAdmin);
            Concept teoriaGrafos = crearConceptoBase("Teoría de Grafos", "Modelado de relaciones entre pares de objetos.", savedWorkspace, savedAdmin);
            Concept progDinamica = crearConceptoBase("Programación Dinámica", "Optimización resolviendo subproblemas superpuestos.", savedWorkspace, savedAdmin);
            Concept algOptimizacion = crearConceptoBase("Algoritmos de Optimización", "Métodos para hallar mínimos o máximos de funciones.", savedWorkspace, savedAdmin);
            Concept descensoGradiente = crearConceptoBase("Descenso de Gradiente", "Algoritmo iterativo para minimizar funciones de costo.", savedWorkspace, savedAdmin);

            // Machine Learning y Datos
            Concept regresionLineal = crearConceptoBase("Regresión Lineal", "Modelo lineal para predecir variables continuas.", savedWorkspace, savedAdmin);
            Concept regresionLogistica = crearConceptoBase("Regresión Logística", "Modelo para clasificación probabilística binaria.", savedWorkspace, savedAdmin);
            Concept machineLearning = crearConceptoBase("Machine Learning", "Algoritmos que mejoran su rendimiento con la experiencia.", savedWorkspace, savedAdmin);
            Concept redesNeuronales = crearConceptoBase("Redes Neuronales", "Modelos computacionales multicapa inspirados en biología.", savedWorkspace, savedAdmin);
            Concept deepLearning = crearConceptoBase("Deep Learning", "Aprendizaje automático basado en redes neuronales profundas.", savedWorkspace, savedAdmin);

            // Relaciones de Matemáticas y Estadística
            calculoInt.setPrerequisites(Arrays.asList(calculoDif));
            estadisticaInf.setPrerequisites(Arrays.asList(probabilidad, estadisticaDesc));
            cadenasMarkov.setPrerequisites(Arrays.asList(probabilidad, algebraLineal));

            // Relaciones de Algoritmos
            complejidad.setPrerequisites(Arrays.asList(calculoDif, algebraLineal));
            teoriaGrafos.setPrerequisites(Arrays.asList(estDatos));
            progDinamica.setPrerequisites(Arrays.asList(complejidad, estDatos));
            algOptimizacion.setPrerequisites(Arrays.asList(calculoDif, algebraLineal));
            descensoGradiente.setPrerequisites(Arrays.asList(algOptimizacion, calculoDif));

            // Relaciones de Machine Learning (Fuertemente interconectados)
            regresionLineal.setPrerequisites(Arrays.asList(algebraLineal, estadisticaInf, calculoDif));
            regresionLogistica.setPrerequisites(Arrays.asList(regresionLineal, probabilidad));
            machineLearning.setPrerequisites(Arrays.asList(estadisticaInf, algebraLineal, algOptimizacion));
            redesNeuronales.setPrerequisites(Arrays.asList(machineLearning, descensoGradiente, algebraLineal));
            deepLearning.setPrerequisites(Arrays.asList(redesNeuronales, algebraLineal, calculoDif));

            // Tags
            Tag mathTag = new Tag(null, "Matemáticas", "Conceptos matemáticos", "#ff0000", null);
            Tag statsTag = new Tag(null, "Estadística", "Conceptos estadísticos", "#00ff00", null);
            Tag algoTag = new Tag(null, "Algoritmos", "Algoritmos y estructuras", "#0000ff", null);
            Tag aiTag = new Tag(null, "Inteligencia Artificial", "IA y Machine Learning", "#ffff00", null);
            tagRepository.saveAll(Arrays.asList(mathTag, statsTag, algoTag, aiTag));

            calculoDif.setTags(Arrays.asList(mathTag));
            calculoInt.setTags(Arrays.asList(mathTag));
            algebraLineal.setTags(Arrays.asList(mathTag));
            probabilidad.setTags(Arrays.asList(statsTag, mathTag));
            estadisticaDesc.setTags(Arrays.asList(statsTag));
            estadisticaInf.setTags(Arrays.asList(statsTag));
            cadenasMarkov.setTags(Arrays.asList(statsTag, aiTag));

            complejidad.setTags(Arrays.asList(algoTag));
            estDatos.setTags(Arrays.asList(algoTag));
            teoriaGrafos.setTags(Arrays.asList(algoTag, mathTag));
            progDinamica.setTags(Arrays.asList(algoTag));
            algOptimizacion.setTags(Arrays.asList(algoTag, mathTag));
            descensoGradiente.setTags(Arrays.asList(aiTag, mathTag));

            regresionLineal.setTags(Arrays.asList(aiTag, statsTag));
            regresionLogistica.setTags(Arrays.asList(aiTag, statsTag));
            machineLearning.setTags(Arrays.asList(aiTag));
            redesNeuronales.setTags(Arrays.asList(aiTag));
            deepLearning.setTags(Arrays.asList(aiTag));

            // Más conceptos
            Concept ciberseguridad = crearConceptoBase("Ciberseguridad", "Protección de sistemas y redes", savedWorkspace, savedAdmin);
            Concept criptografia = crearConceptoBase("Criptografía", "Técnicas de cifrado de datos", savedWorkspace, savedAdmin);
            Concept redes = crearConceptoBase("Redes de Computadoras", "Comunicación entre sistemas", savedWorkspace, savedAdmin);
            
            Tag netTag = new Tag(null, "Redes", "Infraestructura", "#00ffff", null);
            Tag secTag = new Tag(null, "Seguridad", "Protección", "#ff00ff", null);
            tagRepository.saveAll(Arrays.asList(netTag, secTag));
            
            ciberseguridad.setPrerequisites(Arrays.asList(redes, criptografia));
            ciberseguridad.setTags(Arrays.asList(secTag, netTag));
            criptografia.setTags(Arrays.asList(secTag, mathTag));
            redes.setTags(Arrays.asList(netTag));

            // Guardar en la Base de Datos
            conceptRepository.saveAll(Arrays.asList(
                calculoDif, calculoInt, algebraLineal, probabilidad, estadisticaDesc, estadisticaInf, cadenasMarkov,
                complejidad, estDatos, teoriaGrafos, progDinamica, algOptimizacion, descensoGradiente,
                regresionLineal, regresionLogistica, machineLearning, redesNeuronales, deepLearning,
                ciberseguridad, criptografia, redes
            ));

            // Agregar Flashcards
            com.app.grove.flashcard.domain.Flashcard f1 = new com.app.grove.flashcard.domain.Flashcard();
            f1.setFront("¿Qué es la derivada de una función en un punto?");
            f1.setBack("La tasa de cambio instantánea de la función en ese punto, equivalente a la pendiente de la recta tangente.");
            f1.setDifficulty(2);
            f1.setCreatedAt(LocalDateTime.now());
            calculoDif.setFlashcards(Arrays.asList(f1));
            conceptRepository.save(calculoDif);

            com.app.grove.flashcard.domain.Flashcard f2 = new com.app.grove.flashcard.domain.Flashcard();
            f2.setFront("¿Qué es el Teorema Fundamental del Cálculo?");
            f2.setBack("Establece que la derivación y la integración son operaciones inversas.");
            f2.setDifficulty(3);
            f2.setCreatedAt(LocalDateTime.now());
            calculoInt.setFlashcards(Arrays.asList(f2));
            conceptRepository.save(calculoInt);
        }
    }

    private Concept crearConceptoBase(String titulo, String contenido, Workspace workspace, User creador) {
        Concept concept = new Concept();
        concept.setTitle(titulo);
        concept.setContent(contenido);
        concept.setCreatedAt(LocalDateTime.now());
        concept.setUpdatedAt(LocalDateTime.now());
        concept.setWorkspace(workspace);
        concept.setCreatedBy(creador);
        return conceptRepository.save(concept);
    }
}
