package com.chrosciu.rxweb.web.streaming;

import com.chrosciu.rxweb.model.User;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Slf4j
public class StreamingControllerTest {
    @LocalServerPort
    private int port;

    private WebClient webClient;

    @BeforeEach
    void setup() {
        webClient = WebClient.create(String.format("http://localhost:%d", port));
    }

    @Test
    @SneakyThrows
    void shouldStreamUsers() {
        Flux<User> users = webClient.get()
                .uri("/streaming/users")
                .retrieve()
                .bodyToFlux(User.class);

        users.subscribe(u -> log.info("{}", u),
                t -> log.info("Error: ", t),
                () -> log.info("Completed"));

        Thread.sleep(30000);
    }

    @Test
    @SneakyThrows
    void shouldStreamUsersAsNdJson() {
        Flux<User> users = webClient.get()
                .uri("/streaming/users/ndjson")
                .retrieve()
                .bodyToFlux(User.class);

        users.subscribe(u -> log.info("{}", u),
                t -> log.info("Error: ", t),
                () -> log.info("Completed"));

        Thread.sleep(30000);
    }

    @Test
    @SneakyThrows
    void shouldStreamUsersAsSse() {
        Flux<User> users = webClient.get()
                .uri("/streaming/users/sse")
                .retrieve()
                .bodyToFlux(User.class);

        users.subscribe(u -> log.info("{}", u),
                t -> log.info("Error: ", t),
                () -> log.info("Completed"));

        Thread.sleep(30000);
    }

}
