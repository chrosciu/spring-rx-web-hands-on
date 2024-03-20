package eu.chrost.rxweb.web.sse;

import eu.chrost.rxweb.repository.UserRepository;
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

    //TODO: Send following sequence of SSE
    //1) event: "pre", data: "START"
    //2) for each user - data representing user, then 2 seconds delay
    //3) event: "post", data: "STOP"
    @GetMapping("/users/events")
    public Flux<ServerSentEvent<?>> getAllUsersSseEvents() {
        return null;
    }
}
