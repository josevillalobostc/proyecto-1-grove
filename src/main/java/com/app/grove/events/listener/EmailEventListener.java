package com.app.grove.events.listener;

import com.app.grove.events.WelcomeEmailEvent;
import com.app.grove.events.WorkspaceInvitationEvent;
import com.app.grove.events.util.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmailEventListener {
    private final EmailService emailService;

    @Async
    @EventListener
    public void handleWorkspaceInvitation(WorkspaceInvitationEvent event) {
        String subject = "Invitación al workspace: " + event.getWorkspaceName();
        String body = String.format(
                "Hola,\n\n%s te ha invitado a unirte al workspace '%s'.\n\nPara aceptar, haz clic en el siguiente enlace:\n%s\n\nSaludos.",
                event.getInvitedByUserName(),
                event.getWorkspaceName(),
                event.getInvitationLink()
        );
        emailService.sendSimpleEmail(event.getInvitedUserEmail(), subject, body);
    }

    @Async
    @EventListener
    public void handleWelcomeEmail(WelcomeEmailEvent event) {
        String subject = "¡Bienvenido a " + event.getWorkspaceName() + "!";
        String body = String.format(
                "Hola %s,\n\nTe damos la bienvenida al workspace '%s'. Ya puedes colaborar y aprender.\n\nSaludos cordiales.",
                event.getUserName(),
                event.getWorkspaceName()
        );
        emailService.sendSimpleEmail(event.getUserEmail(), subject, body);
    }
}
