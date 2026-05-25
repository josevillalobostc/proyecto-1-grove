package com.app.grove.config;

import java.time.LocalDateTime;
import java.util.ArrayList;
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

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner{
    private final PasswordEncoder passwordEncoder;
    private final WorkspaceRepository workspaceRepository;
    private final ConceptRepository conceptRepository;
    
    private final UserRepository userRepository;

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
    
                userRepository.save(admin);
                Workspace publicWorkspace = new Workspace();
                publicWorkspace.setName("Grove Global Community");
                publicWorkspace.setDescription("Espacio público donde todos los usuarios pueden ver conceptos base.");
                publicWorkspace.setPublic(true);
                publicWorkspace.setCreatedAt(LocalDateTime.now());
                publicWorkspace.setMembers(new ArrayList<>());
                publicWorkspace.getMembers().add(savedAdmin);

                Workspace savedWorkspace = workspaceRepository.save(publicWorkspace);
                
                Concept welcomeConcept = new Concept();
                welcomeConcept.setTitle("¿Qué es Grove?");
                welcomeConcept.setContent("Grove es una plataforma colaborativa basada en grafos de conocimiento para el aprendizaje.");
                welcomeConcept.setCreatedAt(LocalDateTime.now());
                welcomeConcept.setUpdatedAt(LocalDateTime.now());
                welcomeConcept.setWorkspace(savedWorkspace);     
                conceptRepository.save(welcomeConcept);
                crearConceptoBase("Teoría de Grupos", "Estructura algebraica formada por un conjunto y una operación binaria que satisface cierre, asociatividad, elemento neutro e inverso.", savedWorkspace);
                crearConceptoBase("Teorema de Lagrange", "En teoría de grupos, establece que el orden de cualquier subgrupo de un grupo finito divide exactamente al orden del grupo original.", savedWorkspace);
                crearConceptoBase("Isomorfismos", "Un homomorfismo biyectivo. Si existe un isomorfismo entre dos estructuras, ambas son estructuralmente idénticas.", savedWorkspace);
                crearConceptoBase("Teoría de Grafos", "Estudio de las relaciones entre objetos, representadas por vértices (nodos) y aristas (conexiones).", savedWorkspace);
                crearConceptoBase("Axiomas del Álgebra de Boole", "Reglas fundamentales como conmutatividad, distributividad, identidad y complementos que rigen la lógica binaria.", savedWorkspace);
                crearConceptoBase("Combinatoria", "Rama matemática que estudia la enumeración, combinación y permutación de conjuntos finitos.", savedWorkspace);
                crearConceptoBase("Aritmética Modular", "Sistema aritmético para enteros, donde los números se 'envuelven' tras alcanzar cierto valor llamado módulo.", savedWorkspace);
                crearConceptoBase("Teoría de Números", "Estudio de las propiedades de los números enteros, fundamental en áreas como la criptografía moderna.", savedWorkspace);
                crearConceptoBase("Cálculo Tensorial", "Extensión del álgebra lineal. Un tensor es una matriz multidimensional que describe relaciones lineales entre escalares, vectores u otros tensores.", savedWorkspace);
                crearConceptoBase("Árboles (Matemática)", "En teoría de grafos, un árbol es un grafo no dirigido, conectado y acíclico.", savedWorkspace);
                crearConceptoBase("Notación Big O", "Medida teórica para clasificar la complejidad temporal o espacial de un algoritmo según el crecimiento de los datos de entrada.", savedWorkspace);
                crearConceptoBase("Suffix Trees (Árbol de Sufijos)", "Estructura de datos tipo Trie que contiene todos los sufijos de una cadena dada, permitiendo búsquedas de subcadenas extremadamente rápidas.", savedWorkspace);
                crearConceptoBase("Trie (Árbol de Prefijos)", "Estructura de árbol usada para almacenar arrays asociativos o cadenas, muy útil para autocompletado y diccionarios.", savedWorkspace);
                crearConceptoBase("Programación Dinámica", "Método de optimización que resuelve problemas complejos dividiéndolos en subproblemas superpuestos más simples y almacenando sus resultados.", savedWorkspace);
                crearConceptoBase("Modelado Entidad-Relación", "Técnica de diseño de bases de datos. Incluye manejo de relaciones reflexivas (ej. ventajas de tipos elem                // --- CIENCIAS DE LA COMPUTACIÓN Y ESTRUCTURAS ---entales entre sí).", savedWorkspace);
                crearConceptoBase("Normalización de BD", "Proceso de organizar los datos en una base de datos relacional para reducir la redundancia y mejorar la integridad de la información.", savedWorkspace);
                crearConceptoBase("Bases de Datos Orientadas a Grafos", "Sistemas NoSQL basados en la teoría de grafos, donde los datos se almacenan en nodos y sus conexiones en relaciones (Ej. Neo4j).", savedWorkspace);
                crearConceptoBase("Patrones de Diseño", "Soluciones típicas a problemas comunes en el diseño de software. Ejemplos: Singleton, Factory, Observer.", savedWorkspace);
                crearConceptoBase("Clean Architecture", "Filosofía de diseño de software que separa las responsabilidades en capas concéntricas, manteniendo el dominio en el centro y aislado de frameworks.", savedWorkspace);
                crearConceptoBase("Redes Neuronales Artificiales", "Modelos computacionales inspirados en el cerebro humano, basados en capas de nodos interconectados y funciones de activación.", savedWorkspace);
            }
        }
    private void crearConceptoBase(String titulo, String contenido, Workspace workspace) {
            Concept concept = new Concept();
            concept.setTitle(titulo);
            concept.setContent(contenido);
            concept.setCreatedAt(LocalDateTime.now());
            concept.setUpdatedAt(LocalDateTime.now());
            concept.setWorkspace(workspace);
            conceptRepository.save(concept);
        }
    
}