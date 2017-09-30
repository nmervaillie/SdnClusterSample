package org.neo4j.example.cluster.web;

import org.neo4j.driver.v1.exceptions.ServiceUnavailableException;
import org.neo4j.driver.v1.exceptions.SessionExpiredException;
import org.neo4j.example.cluster.domain.User;
import org.neo4j.example.cluster.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController("users")
public class UserController {

    private static final Logger LOG = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    // retry in case of error. notice that the retry policy is ridiculously high here
    // and may not be a good example of a production usage, as more an more calls (and the associated contexts)
    // could quickly saturate memory
    // in case of previsible high load, a retry policy relying on an specialized infrastructure component
    // such as a message queue, would be much more reliable and effective
    @Retryable(value = {SessionExpiredException.class, ServiceUnavailableException.class}, maxAttempts=10, backoff=@Backoff(delay= 3000, multiplier = 2))
    @RequestMapping(method = RequestMethod.POST, consumes = "application/json")
    public User create(@RequestBody User user) {
        LOG.info("Received request to create user {}", user);
        User result = userService.createUser(user);
        LOG.info("User created {}", user);
        return result;
    }

    @GetMapping
    public Iterable<User> search() {
        return userService.getUsers();
    }

    @DeleteMapping
    public void deleteAll() {
        userService.deleteUsers();
    }

}
