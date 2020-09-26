package com.chrosciu.rxweb.web.sse;

import com.chrosciu.rxweb.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.codec.ServerSentEvent;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import static com.chrosciu.rxweb.data.TestUsers.CHROSCIU;
import static com.chrosciu.rxweb.data.TestUsers.OCTOCAT;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserSseControllerTest {
    private UserRepository userRepository;
    private UserSseController userSseController;

    @BeforeEach
    public void setup() {
        userRepository = mock(UserRepository.class);
        userSseController = new UserSseController(userRepository);
    }

    @Test
    public void testGetAllUsersSseEvents() {
        //given
        when(userRepository.findAll()).thenReturn(Flux.just(CHROSCIU, OCTOCAT));
        //when
        Flux<ServerSentEvent> events = userSseController.getAllUsersSseEvents();
        //then
        StepVerifier.create(events)
                .expectNextMatches(sse -> "pre".equals(sse.event()) && "START".equals(sse.data()))
                .expectNextMatches(sse -> null == sse.event() && CHROSCIU.equals(sse.data()))
                .expectNextMatches(sse -> null == sse.event() && OCTOCAT.equals(sse.data()))
                .expectNextMatches(sse -> "post".equals(sse.event()) && "STOP".equals(sse.data()))
                .verifyComplete();
    }
}
