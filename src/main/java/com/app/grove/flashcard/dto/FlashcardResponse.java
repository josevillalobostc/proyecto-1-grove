package com.app.grove.flashcard.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FlashcardResponse {
    private String id;
    private String front;
    private String back;
    private String hint;
    private Integer difficulty;
    private LocalDateTime createdAt;
}
