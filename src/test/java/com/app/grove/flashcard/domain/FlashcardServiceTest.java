package com.app.grove.flashcard.domain;

import com.app.grove.concept.infrastructure.ConceptRepository;
import com.app.grove.exceptions.ResourceNotFoundException;
import com.app.grove.flashcard.dto.FlashcardRequest;
import com.app.grove.flashcard.dto.FlashcardResponse;
import com.app.grove.flashcard.infrastructure.FlashcardRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FlashcardServiceTest {

    @Mock
    private FlashcardRepository flashcardRepository;

    @Mock
    private ConceptRepository conceptRepository;

    private FlashcardService flashcardService;

    @BeforeEach
    void setUp() {
        flashcardService = new FlashcardService(flashcardRepository, conceptRepository);
    }

    @Test
    void shouldCreateFlashcard() {
        FlashcardRequest request = new FlashcardRequest();
        request.setFront("Front");
        request.setBack("Back");
        request.setHint("Hint");
        request.setDifficulty(1);

        Flashcard saved = new Flashcard();
        saved.setId("f1");
        saved.setFront(request.getFront());
        saved.setBack(request.getBack());
        saved.setHint(request.getHint());
        saved.setDifficulty(request.getDifficulty());
        saved.setCreatedAt(LocalDateTime.now());

        when(flashcardRepository.save(any(Flashcard.class))).thenReturn(saved);

        FlashcardResponse response = flashcardService.create(request);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo("f1");
        assertThat(response.getFront()).isEqualTo("Front");
        assertThat(response.getCreatedAt()).isNotNull();

        ArgumentCaptor<Flashcard> captor = ArgumentCaptor.forClass(Flashcard.class);
        verify(flashcardRepository).save(captor.capture());
        assertThat(captor.getValue().getFront()).isEqualTo(request.getFront());
    }

    @Test
    void shouldThrowWhenConceptNotFound() {
        FlashcardRequest request = new FlashcardRequest();
        request.setFront("Front");
        request.setBack("Back");
        request.setConceptIds(List.of("missing"));

        when(conceptRepository.findAllById(request.getConceptIds())).thenReturn(List.of());

        assertThatThrownBy(() -> flashcardService.create(request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Al menos un concepto no fue encontrado");
    }
}
