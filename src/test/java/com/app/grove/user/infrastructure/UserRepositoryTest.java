package com.app.grove.user.infrastructure;

import com.app.grove.AbstractNeo4jTest;
import com.app.grove.user.domain.Role;
import com.app.grove.user.domain.User;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class UserRepositoryTest extends AbstractNeo4jTest {

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    void shouldFindUserByUsernameAndExistenceByEmailOrUsername() {
        User user = new User();
        user.setUsername("testuser");
        user.setEmail("testuser@example.com");
        user.setPassword("password");
        user.setRole(Role.ROLE_USER);
        user.setCreatedAt(LocalDateTime.now());

        userRepository.save(user);

        java.util.List<User> foundUser = userRepository.findByUsername("testuser");

        assertThat(foundUser).isNotEmpty();
        assertThat(foundUser.get(0).getEmail()).isEqualTo("testuser@example.com");
        assertThat(userRepository.existsByUsername("testuser")).isTrue();
        assertThat(userRepository.existsByEmail("testuser@example.com")).isTrue();
    }
}
