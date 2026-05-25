package com.app.grove.notification.application;

import com.app.grove.exceptions.GlobalExceptionHandler;
import com.app.grove.notification.domain.NotificationService;
import com.app.grove.notification.dto.NotificationRequestDTO;
import com.app.grove.notification.dto.NotificationResponseDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class NotificationControllerTest {

    @Mock
    private NotificationService notificationService;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
            .standaloneSetup(new NotificationController(notificationService))
            .setControllerAdvice(new GlobalExceptionHandler())
            .build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void shouldCreateNotificationWhenRequestIsValid() throws Exception {
        NotificationRequestDTO request = new NotificationRequestDTO();
        request.setMessage("New alert");
        request.setUserId("u1");

        NotificationResponseDTO response = new NotificationResponseDTO();
        response.setId("n1");
        response.setMessage("New alert");
        response.setUserId("u1");
        response.setRead(false);
        response.setCreatedAt(LocalDateTime.now());

        when(notificationService.createNotification(any(NotificationRequestDTO.class))).thenReturn(response);

        mockMvc.perform(post("/api/notifications")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value("n1"))
            .andExpect(jsonPath("$.message").value("New alert"));
    }

    @Test
    void shouldMarkNotificationAsReadWhenIdExists() throws Exception {
        NotificationResponseDTO response = new NotificationResponseDTO();
        response.setId("n1");
        response.setRead(true);

        when(notificationService.markAsRead("n1")).thenReturn(response);

        mockMvc.perform(put("/api/notifications/n1/read"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.read").value(true));
    }
}
