package com.app.grove.flashcard.dto;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;

@Data
public class FlashcardResponse {
    private String id;
    private String front;
    private String back;
    private String hint;
    private Integer difficulty;
    private LocalDateTime createdAt;
    private List<String> conceptIds;
}
