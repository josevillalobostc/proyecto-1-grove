package com.app.grove.notification.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class NotificationRequestDTO {

    @NotBlank(message = "El mensaje es obligatorio")
    private String message;

    @NotNull(message = "El ID del usuario es obligatorio")
    private String userId;

    private boolean isRead = false;

}
