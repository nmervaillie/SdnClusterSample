package org.neo4j.example.cluster.service;

import org.neo4j.example.cluster.domain.User;
import org.neo4j.example.cluster.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private static final Logger LOG = LoggerFactory.getLogger(UserService.class);
    private UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public User createUser(User user) {
        LOG.info("Creating user {}", user);
        return userRepository.save(user);
    }

    // read only allows to route to replicas and offload core servers
    @Transactional(readOnly = true)
    public Iterable<User> getUsers() {
        return userRepository.findAll();
    }

    public void deleteUsers() {
        userRepository.deleteAll();
    }
}
