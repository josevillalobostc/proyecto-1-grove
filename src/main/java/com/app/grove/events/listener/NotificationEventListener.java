package com.app.grove.events.listener;

import com.app.grove.events.ConceptCreatedNotificationEvent;
import com.app.grove.events.NewCommentNotificationEvent;
import com.app.grove.notification.domain.Notification;
import com.app.grove.notification.infrastructure.NotificationRepository;
import com.app.grove.user.domain.User;
import com.app.grove.user.infrastructure.UserRepository;
import com.app.grove.workspace.infrastructure.WorkspaceRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationEventListener {
    private final WorkspaceRepository workspaceRepository;
    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;

    @EventListener
    @Transactional
    public void onConceptCreated(ConceptCreatedNotificationEvent event) {

        List<User> members = workspaceRepository.findMembersByWorkspaceId(event.getWorkspaceId());
        User creator = userRepository.findById(event.getCreatedByUserId()).orElse(null);

        for (User member : members) {
            if (creator != null && member.getId().equals(creator.getId())) {
                continue;
            }
            Notification notification = new Notification();
            notification.setMessage("Nuevo concepto: " + event.getConceptTitle() + " en tu workspace");
            notification.setRead(false);
            notification.setCreatedAt(LocalDateTime.now());
            notification.setUser(member);
            notificationRepository.save(notification);
        }
        log.info("Notificaciones creadas para {} miembros del workspace {}", members.size() - (creator != null ? 1 : 0), event.getWorkspaceId());
    }

    @EventListener
    @Transactional
    public void onNewComment(NewCommentNotificationEvent event) {

        User conceptAuthor = userRepository.findById(event.getConceptAuthorId()).orElse(null);
        if (conceptAuthor == null) {
            log.warn("No se encontró el autor del concepto {}", event.getConceptId());
            return;
        }

        Notification notification = new Notification();
        notification.setMessage(event.getCommentAuthorName() + " comentó en tu concepto '" + event.getConceptTitle() + "': " + event.getCommentText());
        notification.setRead(false);
        notification.setCreatedAt(LocalDateTime.now());
        notification.setUser(conceptAuthor);
        notificationRepository.save(notification);
        log.info("Notificación enviada al autor del concepto {}", event.getConceptAuthorId());
    }
}



