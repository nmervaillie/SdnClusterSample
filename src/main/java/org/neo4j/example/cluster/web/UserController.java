package org.neo4j.example.cluster.web;

import java.util.stream.Stream;

import org.neo4j.example.cluster.domain.User;
import org.neo4j.example.cluster.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController("users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @RequestMapping(method = RequestMethod.POST, consumes = "application/json")
    public User create(@RequestBody User user) {
        return userService.createUser(user);
    }

    @GetMapping
    public Stream<User> search() {
        return userService.getUsers();
    }

}
