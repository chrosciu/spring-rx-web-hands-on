package eu.chrost.rxweb.web.sse;

import eu.chrost.rxweb.repository.UserRepository;
import eu.chrost.rxweb.data.TestUsers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.codec.ServerSentEvent;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.function.Supplier;

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
        when(userRepository.findAll()).thenReturn(Flux.just(TestUsers.CHROSCIU, TestUsers.OCTOCAT));
        //when
        Supplier<Flux<ServerSentEvent<?>>> eventsSupplier = () -> userSseController.getAllUsersSseEvents();
        //then
        StepVerifier.withVirtualTime(eventsSupplier)
                .expectSubscription()
                .expectNextMatches(sse -> "pre".equals(sse.event()) && "START".equals(sse.data()))
                .expectNoEvent(Duration.ofSeconds(2))
                .expectNextMatches(sse -> null == sse.event() && TestUsers.CHROSCIU.equals(sse.data()))
                .expectNoEvent(Duration.ofSeconds(2))
                .expectNextMatches(sse -> null == sse.event() && TestUsers.OCTOCAT.equals(sse.data()))
                .expectNextMatches(sse -> "post".equals(sse.event()) && "STOP".equals(sse.data()))
                .verifyComplete();
    }
}
