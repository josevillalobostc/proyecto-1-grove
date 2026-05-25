package com.app.grove;

import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.Neo4jContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers(disabledWithoutDocker = true)
@SuppressWarnings("resource")
public abstract class AbstractNeo4jTest {

    @Container
    static final Neo4jContainer<?> neo4jContainer = new Neo4jContainer<>("neo4j:5.26")
            .withAdminPassword("grove-test-pass")
            .withReuse(true);

    @ServiceConnection
    static Neo4jContainer<?> neo4jConnection() {
        return neo4jContainer;
    }
}
