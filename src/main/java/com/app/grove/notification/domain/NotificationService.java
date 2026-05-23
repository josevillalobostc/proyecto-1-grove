package com.app.grove.notification.domain;

import com.app.grove.notification.dto.NotificationRequestDTO;
import com.app.grove.notification.dto.NotificationResponseDTO;
import com.app.grove.notification.infrastructure.NotificationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    @Transactional
    public NotificationResponseDTO createNotification(NotificationRequestDTO request) {

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        Notification notification = modelMapper.map(request, Notification);
        notification.setCreatedAt(LocalDateTime.now());
        notification.setUser(user);

        Notification saved = notificationRepository.save(notification);
        return convertToResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<NotificationResponseDTO> getNotificationsByUser(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        List<Notification> notifications = notificationRepository.findByUser(user);
        return notifications.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public NotificationResponseDTO markAsRead(String notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notificación no encontrada"));
        notification.setRead(true);

        Notification updated = notificationRepository.save(notification);
        return convertToResponse(updated);
    }

    @Transactional
    public void deleteNotification(String notificationId) {
        if (!notificationRepository.existsById(notificationId)) {
            throw new ResourceNotFoundException("Notificación no encontrada");
        }
        notificationRepository.deleteById(notificationId);
    }

    private NotificationResponseDTO convertToResponse(Notification notification) {
        NotificationResponseDTO response = modelMapper.map(notification, NotificationResponseDTO.class);


        if (notification.getUser() != null) {
            response.setUserId(notification.getUser().getId());
        }

        return response;
    }
}
