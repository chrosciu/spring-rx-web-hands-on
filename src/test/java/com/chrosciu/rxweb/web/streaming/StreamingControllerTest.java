package com.chrosciu.rxweb.web.streaming;

import com.chrosciu.rxweb.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.web.reactive.function.client.WebClient;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
public class StreamingControllerTest {
    //TODO: WebTestClient
    //TODO: More accurate assertions

    @LocalServerPort
    private int localServerPort;

    private WebClient webClient;

    @Container
    public static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:6.0.2");

    @DynamicPropertySource
    static void mongoProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getConnectionString);
    }

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
