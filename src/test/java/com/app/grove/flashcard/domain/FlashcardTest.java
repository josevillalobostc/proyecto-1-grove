package com.app.grove.flashcard.domain;

import com.app.grove.concept.domain.Concept;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class FlashcardTest {

    @Test
    void shouldCreateFlashcardWithValues() {
        Concept concept = new Concept();
        concept.setId("c1");

        Flashcard flashcard = new Flashcard();
        flashcard.setId("f1");
        flashcard.setFront("Front text");
        flashcard.setBack("Back text");
        flashcard.setHint("Hint text");
        flashcard.setDifficulty(3);
        flashcard.setConcepts(List.of(concept));

        assertThat(flashcard.getId()).isEqualTo("f1");
        assertThat(flashcard.getFront()).isEqualTo("Front text");
        assertThat(flashcard.getBack()).isEqualTo("Back text");
        assertThat(flashcard.getHint()).isEqualTo("Hint text");
        assertThat(flashcard.getDifficulty()).isEqualTo(3);
        assertThat(flashcard.getConcepts()).hasSize(1).contains(concept);
    }
}
