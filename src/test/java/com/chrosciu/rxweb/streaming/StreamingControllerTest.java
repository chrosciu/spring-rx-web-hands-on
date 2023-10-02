package com.chrosciu.rxweb.streaming;

import com.chrosciu.rxweb.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class StreamingControllerTest {
    //TODO: WebTestClient
    //TODO: More accurate assertions

    @LocalServerPort
    private int localServerPort;

    private WebClient webClient;

    @BeforeEach
    void setup() {
        webClient = WebClient.create(String.format("http://localhost:%d", localServerPort));
    }

    @Test
    void shouldStreamUsersAsWholeBody() {
        Flux<User> users = webClient.get()
                .uri("/streaming/users")
                .retrieve()
                .bodyToFlux(User.class);

        assertUsers(users);
    }

    @Test
    void shouldStreamUsersAsSse() {
        Flux<User> users = webClient.get()
                .uri("/streaming/users/sse")
                .retrieve()
                .bodyToFlux(User.class);

        assertUsers(users);
    }

    @Test
    void shouldStreamUsersAsNdJson() {
        Flux<User> users = webClient.get()
                .uri("/streaming/users/ndjson")
                .retrieve()
                .bodyToFlux(User.class);

        assertUsers(users);
    }

    private void assertUsers(Flux<User> users) {
        StepVerifier.create(users)
                .expectNextMatches(user -> "mojombo".equals(user.getLogin()))
                .expectNextCount(11)
                .verifyComplete();
    }
}
