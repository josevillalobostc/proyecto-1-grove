package com.app.grove.notification.application;

import com.app.grove.notification.domain.NotificationService;
import com.app.grove.notification.dto.NotificationRequestDTO;
import com.app.grove.notification.dto.NotificationResponseDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;

    @PostMapping
    public ResponseEntity<NotificationResponseDTO> create(@Valid @RequestBody NotificationRequestDTO request) {
        NotificationResponseDTO created = notificationService.createNotification(request);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<NotificationResponseDTO>> getByUser(
            @PathVariable String userId,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(notificationService.getNotificationsByUser(userId, pageable));
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<NotificationResponseDTO> markAsRead(@PathVariable String id) {
        return ResponseEntity.ok(notificationService.markAsRead(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        notificationService.deleteNotification(id);
        return ResponseEntity.noContent().build();
    }
}
