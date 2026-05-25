package com.app.grove;

import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.Neo4jContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public abstract class AbstractNeo4jTest {

    static final Neo4jContainer<?> neo4jContainer;

    static {
        neo4jContainer = new Neo4jContainer<>("neo4j:5.26")
                .withAdminPassword("grove-test-pass")
                .withReuse(true);
        neo4jContainer.start();
    }

    @ServiceConnection
    static Neo4jContainer<?> neo4jConnection() {
        return neo4jContainer;
    }
}
