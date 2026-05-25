package com.app.grove.exercise.dto;

import lombok.Data;

import java.util.List;

@Data
public class ExerciseRequest {
    private String question;
    private String answer;
    private String explanation;
    private String type;
    private List<String> options;
    private Integer difficulty;
    private String userId;
    private List<String> conceptIds;
}
