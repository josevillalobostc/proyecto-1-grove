package com.app.grove.notification.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NotificationResponseDTO {
    private String id;
    private String message;
    private boolean isRead;
    private LocalDateTime createdAt;
    private String userId;

}
