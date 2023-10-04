package com.chrosciu.rxweb.web.mvc;

import com.chrosciu.rxweb.model.User;
import com.chrosciu.rxweb.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Optional;

@RestController
@RequestMapping("/mvc/users")
@RequiredArgsConstructor
public class UserMvcController {
    private final UserRepository userRepository;

    //TODO Create following REST endpoints using annotated controller methods.
    // Both request body and response body should use reactive types only
    // 1) GET /mvc/users - should return all users

    @GetMapping
    public Flux<User> getAllUsers() {
        return userRepository.findAll();
    }

    // 2) GET /mvc/users/{id} - should return user with given id

//    @GetMapping("/{id}")
//    public Mono<User> getUser(@PathVariable String id) {
//        return userRepository.findById(id).switchIfEmpty(Mono.error(new UserNotFoundException()));
//    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<?>> getUser(@PathVariable String id) {
        Mono<ResponseEntity<?>> positive =  userRepository.findById(id)
                .map(user -> ResponseEntity.ok(user));
        Mono<ResponseEntity<?>> alternate = Mono.just(ResponseEntity.notFound().build());
        Mono<ResponseEntity<?>> aggregate = positive.switchIfEmpty(alternate);
        return aggregate;
    }

    // 3) POST /mvc/users - should save user passed as body and return saved user

    @PostMapping
    public Mono<User> addUser(@RequestBody Mono<User> user) {
        return user.flatMap(u -> userRepository.save(u));
    }

    // 4) DELETE /mvc/users/{id} - should delete user with given id

//    @DeleteMapping("/{id}")
//    public Mono<Void> deleteUser(@PathVariable String id) {
//        return userRepository.deleteById(id);
//    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<?>> deleteUser(@PathVariable String id) {
        Mono<Optional<User>> maybeUser = userRepository.findById(id)
                .map(Optional::of)
                .switchIfEmpty(Mono.just(Optional.empty()));

        return maybeUser
                .flatMap(maybe -> maybe
                        .map(user -> userRepository.deleteById(id).then(ok))
                        .orElse(notFound)
                );
    }

    private final static Mono<ResponseEntity<?>> ok = Mono.just(ResponseEntity.ok().build());
    private final static Mono<ResponseEntity<?>> notFound = Mono.just(ResponseEntity.notFound().build());
}

