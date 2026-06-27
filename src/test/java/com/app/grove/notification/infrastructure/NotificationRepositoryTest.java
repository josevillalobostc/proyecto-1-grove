package com.app.grove.notification.infrastructure;

import com.app.grove.AbstractNeo4jTest;
import com.app.grove.notification.domain.Notification;
import com.app.grove.user.domain.Role;
import com.app.grove.user.domain.User;
import com.app.grove.user.infrastructure.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class NotificationRepositoryTest extends AbstractNeo4jTest {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        notificationRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void shouldFindNotificationsByUser() {
        User user = new User();
        user.setUsername("notifyuser");
        user.setEmail("notifyuser@example.com");
        user.setPassword("password");
        user.setRole(Role.ROLE_USER);
        user.setCreatedAt(LocalDateTime.now());
        userRepository.save(user);

        Notification first = new Notification();
        first.setMessage("First notification");
        first.setRead(false);
        first.setCreatedAt(LocalDateTime.now());
        first.setUser(user);

        Notification second = new Notification();
        second.setMessage("Second notification");
        second.setRead(false);
        second.setCreatedAt(LocalDateTime.now());
        second.setUser(user);

        notificationRepository.saveAll(List.of(first, second));

        var pageable = PageRequest.of(0, 10);
        var notifications = notificationRepository.findByUser_Id(user.getId(), pageable).getContent();

        assertThat(notifications).hasSize(2);
        assertThat(notifications).extracting(Notification::getMessage)
                .containsExactlyInAnyOrder("First notification", "Second notification");
    }
}
