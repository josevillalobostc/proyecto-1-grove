package com.app.grove.events;
import lombok.Value;

@Value
public class WorkspaceInvitationEvent {
    String invitedUserEmail;
    String workspaceName;
    String invitedByUserName;
    String invitationLink;
}
