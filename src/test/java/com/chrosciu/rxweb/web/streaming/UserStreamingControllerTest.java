package com.chrosciu.rxweb.web.streaming;

import com.chrosciu.rxweb.init.DBInitializer;
import com.chrosciu.rxweb.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.web.reactive.function.client.WebClient;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
public class UserStreamingControllerTest {
    //TODO: WebTestClient
    //TODO: More accurate assertions

    @Autowired
    private DBInitializer dbInitializer;

    @LocalServerPort
    private int localServerPort;

    private WebClient webClient;

    public static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:6.0.2").withReuse(true);

    static {
        mongoDBContainer.start();
    }

    @DynamicPropertySource
    static void mongoProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getConnectionString);
    }

    @BeforeEach
    void setup() {
        webClient = WebClient.create(String.format("http://localhost:%d", localServerPort));
        dbInitializer.await();
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
    void shouldStreamUsersAsSseReadFullSse() {
        ParameterizedTypeReference<ServerSentEvent<User>> typeReference = new ParameterizedTypeReference<>() {};
        Flux<ServerSentEvent<User>> usersSse = webClient.get()
                .uri("/streaming/users/sse")
                .retrieve()
                .bodyToFlux(typeReference);

        assertUsers(usersSse.map(ServerSentEvent::data));
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
