package com.chrosciu.rxweb.web.sse;

import com.chrosciu.rxweb.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/sse")
@RequiredArgsConstructor
public class UserSseController {
    private final UserRepository userRepository;

    @GetMapping("/users/events")
    public Flux<ServerSentEvent> getAllUsersSseEvents() {
        return null;
    }
}
