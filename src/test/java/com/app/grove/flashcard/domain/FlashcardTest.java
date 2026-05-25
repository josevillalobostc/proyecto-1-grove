package com.app.grove.flashcard.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class FlashcardTest {

    @Test
    void shouldCreateFlashcardWithValues() {
        Flashcard flashcard = new Flashcard();
        flashcard.setId("f1");
        flashcard.setFront("Front text");
        flashcard.setBack("Back text");
        flashcard.setHint("Hint text");
        flashcard.setDifficulty(3);

        assertThat(flashcard.getId()).isEqualTo("f1");
        assertThat(flashcard.getFront()).isEqualTo("Front text");
        assertThat(flashcard.getBack()).isEqualTo("Back text");
        assertThat(flashcard.getHint()).isEqualTo("Hint text");
        assertThat(flashcard.getDifficulty()).isEqualTo(3);
    }
}
