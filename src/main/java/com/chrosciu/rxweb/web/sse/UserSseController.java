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

    @GetMapping("/users")
    public Flux<User> getAllUsers() {
        return userRepository.findAll().repeat(5).delayElements(Duration.ofSeconds(5));
    }

    @GetMapping("/users/events")
    public Flux<ServerSentEvent> getAllUsersSseEvents() {
        Mono<ServerSentEvent> pre = Mono.just(ServerSentEvent.builder().event("pre").data("dupa").build());
        Flux<ServerSentEvent> flux = userRepository.findAll().repeat(5).delayElements(Duration.ofSeconds(5))
                .map(u -> ServerSentEvent.builder().data(u).build());
        Mono<ServerSentEvent> post = Mono.just(ServerSentEvent.builder().event("post").data("wieloryba").build());
        return pre.concatWith(flux).concatWith(post);
    }
}
