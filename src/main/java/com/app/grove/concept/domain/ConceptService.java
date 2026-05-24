package com.app.grove.concept.domain;

import com.app.grove.concept.dto.ConceptRequest;
import com.app.grove.concept.dto.ConceptResponse;
import com.app.grove.concept.dto.ConceptUpdateRequest;
import com.app.grove.concept.infrastructure.ConceptRepository;
import com.app.grove.exceptions.ResourceNotFoundException;
import com.app.grove.tag.domain.Tag;
import com.app.grove.tag.infrastructure.TagRepository;
import com.app.grove.workspace.domain.Workspace;
import com.app.grove.workspace.infrastructure.WorkspaceRepository;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ConceptService {

    private final ConceptRepository conceptRepository;
    private final TagRepository tagRepository;
    private final WorkspaceRepository workspaceRepository;
    private final ModelMapper modelMapper;

    @Transactional
    public ConceptResponse createConcept(ConceptRequest request) {
        Workspace workspace = workspaceRepository
            .findById(request.getWorkspaceId())
            .orElseThrow(() ->
                new ResourceNotFoundException("Workspace no encontrado: " + request.getWorkspaceId())
            );

        Concept concept = new Concept();
        concept.setTitle(request.getTitle());
        concept.setContent(request.getContent());
        concept.setCreatedAt(LocalDateTime.now());
        concept.setWorkspace(workspace);

        concept = conceptRepository.save(concept);
        return modelMapper.map(concept, ConceptResponse.class);
    }

    @Transactional
    public ConceptResponse forkConcept(String originalConceptId, String targetWorkspaceId) {
        Concept original = conceptRepository
            .findById(originalConceptId)
            .orElseThrow(() ->
                new ResourceNotFoundException("Concepto original no encontrado")
            );

        Workspace targetWorkspace = workspaceRepository
            .findById(targetWorkspaceId)
            .orElseThrow(() ->
                new ResourceNotFoundException("Workspace destino no encontrado")
            );

        Concept fork = new Concept();
        fork.setTitle(original.getTitle());
        fork.setContent(original.getContent());
        fork.setCreatedAt(LocalDateTime.now());
        fork.setWorkspace(targetWorkspace);
        fork.setForkedFrom(original);

        fork = conceptRepository.save(fork);
        return modelMapper.map(fork, ConceptResponse.class);
    }

    public ConceptResponse getConceptById(String id) {
        Concept concept = conceptRepository
            .findById(id)
            .orElseThrow(() ->
                new ResourceNotFoundException("Concepto no encontrado: " + id)
            );
        return modelMapper.map(concept, ConceptResponse.class);
    }

    public List<ConceptResponse> getAllConcepts() {
        return conceptRepository
            .findAll()
            .stream()
            .map(concept -> modelMapper.map(concept, ConceptResponse.class))
            .collect(Collectors.toList());
    }

    @Transactional
    public ConceptResponse updateConcept(
        String id,
        ConceptUpdateRequest request
    ) {
        Concept concept = conceptRepository
            .findById(id)
            .orElseThrow(() ->
                new ResourceNotFoundException("Concepto no encontrado: " + id)
            );
        if (request.getTitle() != null) {
            concept.setTitle(request.getTitle());
        }
        if (request.getContent() != null) {
            concept.setContent(request.getContent());
        }
        concept.setUpdatedAt(LocalDateTime.now());
        concept = conceptRepository.save(concept);
        return modelMapper.map(concept, ConceptResponse.class);
    }

    @Transactional
    public void deleteConcept(String id) {
        Concept concept = conceptRepository
            .findById(id)
            .orElseThrow(() ->
                new ResourceNotFoundException("Concepto no encontrado: " + id)
            );
        conceptRepository.delete(concept);
    }

    @Transactional
    public ConceptResponse addPrerequisite(
        String conceptId,
        String prerequisiteId
    ) {
        Concept concept = conceptRepository
            .findById(conceptId)
            .orElseThrow(() ->
                new ResourceNotFoundException(
                    "Concepto no encontrado: " + conceptId
                )
            );
        Concept prerequisite = conceptRepository
            .findById(prerequisiteId)
            .orElseThrow(() ->
                new ResourceNotFoundException(
                    "Prerrequisito no encontrado: " + prerequisiteId
                )
            );

        if (!concept.getPrerequisites().contains(prerequisite)) {
            concept.getPrerequisites().add(prerequisite);
            concept = conceptRepository.save(concept);
        }
        return modelMapper.map(concept, ConceptResponse.class);
    }

    @Transactional
    public ConceptResponse removePrerequisite(
        String conceptId,
        String prerequisiteId
    ) {
        Concept concept = conceptRepository
            .findById(conceptId)
            .orElseThrow(() ->
                new ResourceNotFoundException(
                    "Concepto no encontrado: " + conceptId
                )
            );
        concept
            .getPrerequisites()
            .removeIf(prereq -> prereq.getId().equals(prerequisiteId));
        concept = conceptRepository.save(concept);
        return modelMapper.map(concept, ConceptResponse.class);
    }

    @Transactional
    public ConceptResponse addTagToConcept(String conceptId, String tagId) {
        Concept concept = conceptRepository
            .findById(conceptId)
            .orElseThrow(() ->
                new ResourceNotFoundException(
                    "Concepto no encontrado: " + conceptId
                )
            );

        Tag tag = tagRepository
            .findById(tagId)
            .orElseThrow(() ->
                new ResourceNotFoundException("Tag no encontrado: " + tagId)
            );

        if (!concept.getTags().contains(tag)) {
            concept.getTags().add(tag);
            concept = conceptRepository.save(concept);
        }
        return modelMapper.map(concept, ConceptResponse.class);
    }

    @Transactional
    public ConceptResponse removeTagFromConcept(
        String conceptId,
        String tagId
    ) {
        Concept concept = conceptRepository
            .findById(conceptId)
            .orElseThrow(() ->
                new ResourceNotFoundException(
                    "Concepto no encontrado: " + conceptId
                )
            );
        concept.getTags().removeIf(tag -> tag.getId().equals(tagId));
        concept = conceptRepository.save(concept);
        return modelMapper.map(concept, ConceptResponse.class);
    }

    public List<ConceptResponse> searchByTitle(String keyword) {
        return conceptRepository
            .searchByTitleContaining(keyword)
            .stream()
            .map(concept -> modelMapper.map(concept, ConceptResponse.class))
            .collect(Collectors.toList());
    }
}
