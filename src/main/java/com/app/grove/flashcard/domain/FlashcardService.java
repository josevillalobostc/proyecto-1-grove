package com.app.grove.flashcard.domain;

import com.app.grove.concept.domain.Concept;
import com.app.grove.concept.infrastructure.ConceptRepository;
import com.app.grove.exceptions.ResourceNotFoundException;
import com.app.grove.flashcard.dto.FlashcardRequest;
import com.app.grove.flashcard.dto.FlashcardResponse;
import com.app.grove.flashcard.infrastructure.FlashcardRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class FlashcardService {

    private final FlashcardRepository flashcardRepository;
    private final ConceptRepository conceptRepository;

    @Transactional
    public FlashcardResponse create(FlashcardRequest request) {
        Flashcard flashcard = mapToEntity(request);
        flashcard.setCreatedAt(LocalDateTime.now());
        Flashcard saved = flashcardRepository.save(flashcard);
        return mapToResponse(saved);
    }

    public List<FlashcardResponse> getAll() {
        return flashcardRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public FlashcardResponse findById(String id) {
        Flashcard flashcard = flashcardRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Flashcard no encontrada: " + id));
        return mapToResponse(flashcard);
    }

    public List<FlashcardResponse> findByDifficulty(Integer difficulty) {
        return flashcardRepository.findByDifficulty(difficulty)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<FlashcardResponse> searchByFront(String keyword) {
        return flashcardRepository.findByFrontContaining(keyword)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteById(String id) {
        if (!flashcardRepository.existsById(id)) {
            throw new ResourceNotFoundException("Flashcard no encontrada: " + id);
        }
        flashcardRepository.deleteById(id);
    }

    private Flashcard mapToEntity(FlashcardRequest request) {
        Flashcard flashcard = new Flashcard();
        flashcard.setFront(request.getFront());
        flashcard.setBack(request.getBack());
        flashcard.setHint(request.getHint());
        flashcard.setDifficulty(request.getDifficulty());
        if (request.getConceptIds() != null && !request.getConceptIds().isEmpty()) {
            List<Concept> concepts = StreamSupport.stream(
                    conceptRepository.findAllById(request.getConceptIds()).spliterator(), false)
                    .collect(Collectors.toList());
            if (concepts.size() != request.getConceptIds().size()) {
                throw new ResourceNotFoundException("Al menos un concepto no fue encontrado");
            }
            flashcard.setConcepts(concepts);
        }
        return flashcard;
    }

    private FlashcardResponse mapToResponse(Flashcard flashcard) {
        FlashcardResponse response = new FlashcardResponse();
        response.setId(flashcard.getId());
        response.setFront(flashcard.getFront());
        response.setBack(flashcard.getBack());
        response.setHint(flashcard.getHint());
        response.setDifficulty(flashcard.getDifficulty());
        response.setCreatedAt(flashcard.getCreatedAt());
        if (flashcard.getConcepts() != null) {
            response.setConceptIds(
                    flashcard.getConcepts().stream().map(Concept::getId).collect(Collectors.toList())
            );
        }
        return response;
    }
}
