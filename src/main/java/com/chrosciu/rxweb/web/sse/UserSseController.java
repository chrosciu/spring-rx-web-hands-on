package com.chrosciu.rxweb.web.sse;

import com.chrosciu.rxweb.model.User;
import com.chrosciu.rxweb.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

@RestController
@RequestMapping("/sse")
@RequiredArgsConstructor
public class UserSseController {
    private final UserRepository userRepository;

    //TODO: Send following sequence of SSE
    //1) event: "pre", data: "START"
    //2) for each user - data representing user, then 2 seconds delay
    //3) event: "post", data: "STOP"
    @GetMapping("/users/events")
    public Flux<ServerSentEvent<?>> getAllUsersSseEvents() {
        Mono<ServerSentEvent<?>> pre = Mono.just(ServerSentEvent.builder().event("pre").data("START").build());
        Flux<ServerSentEvent<?>> users = getUsersWithDelay().map(user -> ServerSentEvent.builder().data(user).build());
        Mono<ServerSentEvent<?>> post = Mono.just(ServerSentEvent.builder().event("post").data("STOP").build());

        Flux<ServerSentEvent<?>> aggregate = Flux.concat(pre, users, post);
        return aggregate;
    }

    private Flux<User> getUsersWithDelay() {
        return userRepository.findAll().delayElements(Duration.ofSeconds(2));
    }
}
