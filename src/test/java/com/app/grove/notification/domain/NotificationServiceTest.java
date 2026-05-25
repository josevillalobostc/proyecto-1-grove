package com.app.grove.notification.domain;

import com.app.grove.exceptions.ResourceNotFoundException;
import com.app.grove.notification.dto.NotificationRequestDTO;
import com.app.grove.notification.dto.NotificationResponseDTO;
import com.app.grove.notification.infrastructure.NotificationRepository;
import com.app.grove.user.domain.User;
import com.app.grove.user.infrastructure.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private UserRepository userRepository;

    private NotificationService notificationService;

    @BeforeEach
    void setUp() {
        notificationService = new NotificationService(notificationRepository, userRepository, new ModelMapper());
    }

    @Test
    void shouldCreateNotificationWhenUserExists() {
        User user = new User();
        user.setId("u1");

        NotificationRequestDTO request = new NotificationRequestDTO();
        request.setMessage("Hello");
        request.setUserId("u1");

        Notification saved = new Notification();
        saved.setId("n1");
        saved.setMessage("Hello");
        saved.setRead(false);
        saved.setCreatedAt(LocalDateTime.now());
        saved.setUser(user);

        when(userRepository.findById("u1")).thenReturn(Optional.of(user));
        when(notificationRepository.save(org.mockito.ArgumentMatchers.any(Notification.class))).thenReturn(saved);

        NotificationResponseDTO response = notificationService.createNotification(request);

        assertThat(response).isNotNull();
        assertThat(response.getUserId()).isEqualTo("u1");
        assertThat(response.getMessage()).isEqualTo("Hello");
    }

    @Test
    void shouldMarkNotificationAsReadWhenExists() {
        Notification existing = new Notification();
        existing.setId("n1");
        existing.setRead(false);

        when(notificationRepository.findById("n1")).thenReturn(Optional.of(existing));
        when(notificationRepository.save(existing)).thenReturn(existing);

        NotificationResponseDTO response = notificationService.markAsRead("n1");

        assertThat(response).isNotNull();
        assertThat(response.isRead()).isTrue();
    }

    @Test
    void shouldThrowWhenNotificationNotFound() {
        when(notificationRepository.findById("missing")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> notificationService.markAsRead("missing"))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Notificación no encontrada");
    }
}
