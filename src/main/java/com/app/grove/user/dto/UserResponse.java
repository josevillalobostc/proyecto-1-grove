package com.app.grove.user.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class UserResponse {
    private String id;    
    private String username;
    private String email;
    private LocalDateTime createdAt;

}