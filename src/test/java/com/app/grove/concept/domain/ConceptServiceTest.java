package com.app.grove.concept.domain;

import com.app.grove.concept.dto.ConceptRequest;
import com.app.grove.concept.dto.ConceptResponse;
import com.app.grove.concept.infrastructure.ConceptRepository;
import com.app.grove.concept.infrastructure.UserConceptProgressRepository;
import com.app.grove.exceptions.ResourceNotFoundException;
import com.app.grove.flashcard.infrastructure.FlashcardRepository;
import com.app.grove.tag.infrastructure.TagRepository;
import com.app.grove.user.domain.User;
import com.app.grove.workspace.domain.Workspace;
import com.app.grove.workspace.infrastructure.WorkspaceRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConceptServiceTest {

    @Mock
    private ConceptRepository conceptRepository;

    @Mock
    private TagRepository tagRepository;

    @Mock
    private WorkspaceRepository workspaceRepository;

    @Mock
    private FlashcardRepository flashcardRepository;

    @Mock
    private UserConceptProgressRepository progressRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    private ConceptService conceptService;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
        conceptService = new ConceptService(
            conceptRepository,
            tagRepository,
            workspaceRepository,
            flashcardRepository,
            progressRepository,
            new ModelMapper(),
            eventPublisher
        );
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldCreateConceptWhenWorkspaceExists() {
        Workspace workspace = new Workspace();
        workspace.setId("w1");
        workspace.setName("Workspace One");
        workspace.setPublic(true);

        ConceptRequest request = new ConceptRequest();
        request.setTitle("Graph Theory");
        request.setContent("Study of graphs");
        request.setWorkspaceId("w1");

        Concept savedConcept = new Concept();
        savedConcept.setId("c1");
        savedConcept.setTitle("Graph Theory");
        savedConcept.setContent("Study of graphs");
        savedConcept.setWorkspace(workspace);
        savedConcept.setCreatedAt(LocalDateTime.now());

        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        User creator = new User();
        creator.setId("u1");
        when(authentication.getPrincipal()).thenReturn(creator);
        when(workspaceRepository.findById("w1")).thenReturn(Optional.of(workspace));
        when(conceptRepository.save(org.mockito.ArgumentMatchers.any(Concept.class))).thenReturn(savedConcept);

        ConceptResponse response = conceptService.createConcept(request);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo("c1");
        assertThat(response.getTitle()).isEqualTo("Graph Theory");
        assertThat(response.getWorkspaceId()).isEqualTo("w1");
    }

    @Test
    void shouldThrowWhenWorkspaceDoesNotExist() {
        ConceptRequest request = new ConceptRequest();
        request.setTitle("Graph Theory");
        request.setContent("Study of graphs");
        request.setWorkspaceId("missing");

        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        User creator = new User();
        creator.setId("u1");
        when(authentication.getPrincipal()).thenReturn(creator);

        when(workspaceRepository.findById("missing")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> conceptService.createConcept(request))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Workspace no encontrado");
    }

    @Test
    void shouldAddPrerequisiteWhenNotAlreadyPresent() {
        Concept concept = new Concept();
        concept.setId("c1");
        concept.setTitle("Advanced");
        concept.setPrerequisites(new ArrayList<>());

        Concept prerequisite = new Concept();
        prerequisite.setId("c2");
        prerequisite.setTitle("Basic");

        when(conceptRepository.findById("c1")).thenReturn(Optional.of(concept));
        when(conceptRepository.findById("c2")).thenReturn(Optional.of(prerequisite));
        when(conceptRepository.save(concept)).thenReturn(concept);

        ConceptResponse response = conceptService.addPrerequisite("c1", "c2");

        assertThat(response).isNotNull();
        assertThat(response.getPrerequisiteIds()).containsExactly("c2");
    }
}
