package com.app.grove.notification.domain;


import com.app.grove.user.domain.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;
import org.springframework.data.neo4j.core.support.UUIDStringGenerator;

import java.time.LocalDateTime;

@Node("Notification")
@Getter
@Setter
@NoArgsConstructor
public class Notification {
    @Id
    @GeneratedValue(UUIDStringGenerator.class)
    private String id;

    private String message;
    private boolean isRead;
    private LocalDateTime createdAt;

    @Relationship(type = "NOTIFIES", direction = Relationship.Direction.OUTGOING)
    private User user;

}
