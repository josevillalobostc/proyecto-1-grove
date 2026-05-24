package com.app.grove.events;

import lombok.Value;

@Value
public class ConceptCreatedNotificationEvent {
    String conceptId;
    String conceptTitle;
    String workspaceId;
    String createdByUserId;

}
