package com.app.grove.events;

import lombok.Value;

@Value
public class WelcomeEmailEvent {
    String userEmail;
    String userName;
    String workspaceName;
}
