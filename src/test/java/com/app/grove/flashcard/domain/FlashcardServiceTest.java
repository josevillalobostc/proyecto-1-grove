package com.app.grove.flashcard.domain;

import com.app.grove.flashcard.dto.FlashcardRequest;
import com.app.grove.flashcard.dto.FlashcardResponse;
import com.app.grove.flashcard.infrastructure.FlashcardRepository;
import com.app.grove.flashcard.infrastructure.UserFlashcardProgressRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FlashcardServiceTest {

    @Mock
    private FlashcardRepository flashcardRepository;

    @Mock
    private UserFlashcardProgressRepository progressRepository;

    private FlashcardService flashcardService;

    @BeforeEach
    void setUp() {
        flashcardService = new FlashcardService(flashcardRepository, progressRepository, new ModelMapper());
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
    void shouldFindFlashcardById() {
        Flashcard flashcard = new Flashcard();
        flashcard.setId("f1");
        flashcard.setFront("Front");
        flashcard.setBack("Back");
        flashcard.setCreatedAt(LocalDateTime.now());

        when(flashcardRepository.findById("f1")).thenReturn(Optional.of(flashcard));

        FlashcardResponse response = flashcardService.findById("f1");

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo("f1");
    }

    @Test
    void shouldReturnFlashcardsByDifficulty() {
        Flashcard flashcard = new Flashcard();
        flashcard.setId("f1");
        flashcard.setDifficulty(2);
        flashcard.setCreatedAt(LocalDateTime.now());

        var pageable = PageRequest.of(0, 10);
        when(flashcardRepository.findByDifficulty(2, pageable))
                .thenReturn(new PageImpl<>(List.of(flashcard), pageable, 1));

        var page = flashcardService.findByDifficulty(2, pageable);

        assertThat(page.getContent()).hasSize(1);
        assertThat(page.getContent().get(0).getDifficulty()).isEqualTo(2);
    }

    @Test
    void shouldDeleteExistingFlashcard() {
        when(flashcardRepository.existsById("f1")).thenReturn(true);

        flashcardService.deleteById("f1");

        verify(flashcardRepository).deleteById("f1");
    }
}
