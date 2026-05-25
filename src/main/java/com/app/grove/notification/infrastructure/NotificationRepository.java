package com.app.grove.notification.infrastructure;

import com.app.grove.notification.domain.Notification;

import com.app.grove.user.domain.User;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends Neo4jRepository<Notification, String> {
    Page<Notification> findByUser(User user, Pageable pageable);
}
