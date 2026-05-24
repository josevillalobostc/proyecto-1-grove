package com.app.grove.notification.infrastructure;

import com.app.grove.notification.domain.Notification;

import com.app.grove.user.domain.User;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends Neo4jRepository<Notification, String> {
    List<Notification> findByUser(User user);
}
