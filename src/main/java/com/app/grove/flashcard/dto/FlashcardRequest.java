package com.app.grove.flashcard.dto;

import java.util.List;
import lombok.Data;

@Data
public class FlashcardRequest {
    private String front;
    private String back;
    private String hint;
    private Integer difficulty;
    private List<String> conceptIds;
}
