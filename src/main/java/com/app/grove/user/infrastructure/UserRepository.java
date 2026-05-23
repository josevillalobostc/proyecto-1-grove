package com.app.grove.user.infrastructure;

import java.util.Optional;

import org.springframework.data.neo4j.repository.Neo4jRepository;

import com.app.grove.user.domain.User;

public interface UserRepository extends Neo4jRepository<User, String>{
    Optional<User> findByUsername(String username);
}