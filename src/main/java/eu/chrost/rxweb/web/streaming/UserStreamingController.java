package eu.chrost.rxweb.web.streaming;

import eu.chrost.rxweb.model.User;
import eu.chrost.rxweb.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.time.Duration;

import static org.springframework.http.MediaType.APPLICATION_NDJSON_VALUE;
import static org.springframework.http.MediaType.TEXT_EVENT_STREAM_VALUE;

@RestController
@RequestMapping("/streaming")
@RequiredArgsConstructor
public class UserStreamingController {
    private final UserRepository userRepository;

    //TODO: Delay as query param

    @GetMapping(value = "/users")
    public Flux<User> getAllUsers() {
        return userRepository.findAll()
                .delayElements(Duration.ofSeconds(0));
    }

    @GetMapping(value = "/users/sse", produces = TEXT_EVENT_STREAM_VALUE)
    public Flux<User> getAllUsersAsSse() {
        return userRepository.findAll()
                .delayElements(Duration.ofSeconds(0));
    }

    @GetMapping(value = "/users/ndjson", produces = APPLICATION_NDJSON_VALUE)
    public Flux<User> getAllUsersAsNdJson() {
        return userRepository.findAll()
                .delayElements(Duration.ofSeconds(0));
    }


}
