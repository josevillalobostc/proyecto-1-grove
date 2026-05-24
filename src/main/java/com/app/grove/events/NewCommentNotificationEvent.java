package com.app.grove.events;

import lombok.Value;

@Value
public class NewCommentNotificationEvent {
    String conceptId;
    String conceptTitle;
    String commentText;
    String commentAuthorName;
    String conceptAuthorId;
}
