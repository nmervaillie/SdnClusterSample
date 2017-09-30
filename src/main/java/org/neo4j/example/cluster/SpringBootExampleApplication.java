package org.neo4j.example.cluster;

import org.neo4j.ogm.session.SessionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;
import org.springframework.data.neo4j.transaction.Neo4jTransactionManager;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.transaction.PlatformTransactionManager;

@SpringBootApplication
@EnableNeo4jRepositories("org.neo4j.example.cluster.repository")
@EntityScan(basePackages = "org.neo4j.example.cluster.domain")
@Configuration
@EnableRetry
public class SpringBootExampleApplication {

	@Value("${spring.data.neo4j.uri}")
	private String uri;

	@Value("${spring.data.neo4j.uris}")
	private String[] uris;

	@Value("${spring.data.neo4j.username}")
	private String username;

	@Value("${spring.data.neo4j.password}")
	private String password;

	@Value("${spring.data.neo4j.connection.liveness.check.timeout}")
	private int livenessTimeout;

	public static void main(String[] args) {
		SpringApplication.run(SpringBootExampleApplication.class, args);
	}

	@Bean
	SessionFactory sessionFactory() {

		org.neo4j.ogm.config.Configuration configuration = new org.neo4j.ogm.config.Configuration.Builder()
				.uri(uri)
				.uris(uris)
				.credentials(username, password)
				.connectionLivenessCheckTimeout(livenessTimeout)
				.verifyConnection(true)
				.build();

		return new SessionFactory(configuration, "org.neo4j.example.cluster.domain");
	}

	@Bean
	PlatformTransactionManager transactionManager() {
		return new Neo4jTransactionManager(sessionFactory());
	}
}
