package org.neo4j.example.cluster.service;

import java.util.stream.Stream;

import org.neo4j.example.cluster.domain.User;
import org.neo4j.example.cluster.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // retry in case of error. notice that the retry policy is ridiculously high here
    // and may not be a good example of a production usage, as more an more calls (and the associated contexts)
    // could quickly saturate memory
    // in case of previsible high load, a retry policy relying on an specialized infrastructure component
    // such as a message queue, would be much more reliable and effective
    @Retryable(value = Exception.class, maxAttempts=10, backoff=@Backoff(delay= 3000, multiplier = 2))
    @Transactional
    public User createUser(User user) {
        return userRepository.save(user);
    }

    // read only allows to route to replicas and offload core servers
    @Transactional(readOnly = true)
    public Stream<User> getUsers() {
        return userRepository.streamByFirstNameLike("*");
    }
}
