package com.chrosciu.rxweb.web.streaming;

import com.chrosciu.rxweb.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.chrosciu.rxweb.model.User;
import reactor.core.publisher.Flux;

import java.time.Duration;

import static org.springframework.http.MediaType.APPLICATION_NDJSON_VALUE;
import static org.springframework.http.MediaType.TEXT_EVENT_STREAM_VALUE;

@RestController
@RequestMapping("/streaming/users")
@RequiredArgsConstructor
public class UserStreamingController {
    private final UserRepository userRepository;

    @GetMapping
    public Flux<User> getAllUsers() {
        return getUsersWithDelay();
    }

    @GetMapping(value = "/ndjson", produces = APPLICATION_NDJSON_VALUE)
    public Flux<User> getAllUsersAsNdJson() {
        return getUsersWithDelay();
    }

    @GetMapping(value = "/sse", produces = TEXT_EVENT_STREAM_VALUE)
    public Flux<User> getAllUsersAsSse() {
        return getUsersWithDelay();
    }

    @GetMapping(value = "/sse/full")
    public Flux<ServerSentEvent<User>> getAllUsersAsSseFull() {
        return getUsersWithDelay()
                .map(user -> ServerSentEvent.<User>builder().event("USER").data(user).build());
    }

    private Flux<User> getUsersWithDelay() {
        return userRepository.findAll().delayElements(Duration.ofSeconds(2));
    }
}
