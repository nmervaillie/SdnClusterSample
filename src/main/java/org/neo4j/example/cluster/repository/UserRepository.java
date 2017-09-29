package org.neo4j.example.cluster.repository;

import java.util.stream.Stream;

import org.neo4j.example.cluster.domain.User;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface UserRepository extends Neo4jRepository<User, Long> {

	Stream<User> streamByFirstNameLike(String query);
}
