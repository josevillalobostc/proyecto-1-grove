package com.app.grove.exercise.dto;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;

@Data
public class ExerciseResponse {
	private String id;
	private String question;
    private String answer;
    private String explanation;
    private String type;
    private List<String> options;
    private Integer difficulty;
    private LocalDateTime createdAt;
}
