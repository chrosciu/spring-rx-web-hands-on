package com.chrosciu.rxweb.web.mvc;

import com.chrosciu.rxweb.model.User;
import com.chrosciu.rxweb.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/mvc")
@RequiredArgsConstructor
public class UserMvcController {
    private final UserRepository userRepository;

    @GetMapping("/users")
    public Flux<User> getAllUsers() {
        return userRepository.findAll();
    }

    @GetMapping("/users/{id}")
    public Mono<User> getUsers(@PathVariable("id") String id) {
        return userRepository.findById(id);
    }

    @PostMapping("/users")
    public Mono<User> addUser(@RequestBody Mono<User> user) {
        return user.flatMap(userRepository::save);
    }

    @DeleteMapping("/users/{id}")
    public Mono<Void> deleteUser(@PathVariable("id") String id) {
        return userRepository.deleteById(id);
    }
}

