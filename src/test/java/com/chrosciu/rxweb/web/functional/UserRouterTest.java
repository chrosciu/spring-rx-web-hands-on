package com.chrosciu.rxweb.web.functional;

import com.chrosciu.rxweb.model.User;
import com.chrosciu.rxweb.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static com.chrosciu.rxweb.util.Users.JANUSZ;
import static com.chrosciu.rxweb.util.Users.JANUSZ_UNSAVED;
import static com.chrosciu.rxweb.util.Users.MIREK;
import static org.mockito.Mockito.when;

public class UserRouterTest {
    private UserRepository userRepository;
    private UserRouter userRouter;
    private WebTestClient webTestClient;

    @BeforeEach
    public void setup() {
        userRepository = Mockito.mock(UserRepository.class);
        userRouter = new UserRouter(userRepository);
        webTestClient = WebTestClient.bindToRouterFunction(userRouter.userRoutes()).build();
    }

    @Test
    public void testGetAllUsers() {
        when(userRepository.findAll()).thenReturn(Flux.just(JANUSZ, MIREK));
        webTestClient
                .get()
                .uri("/functional/users")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.length()").isEqualTo(2)
                .jsonPath("$[0].firstName").isEqualTo("Janusz")
                .jsonPath("$[1].firstName").isEqualTo("Mirek");
    }

    @Test
    public void testGetUser() {
        when(userRepository.findById("1")).thenReturn(Mono.just(JANUSZ));
        webTestClient
                .get()
                .uri("/functional/users/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.firstName").isEqualTo("Janusz");
    }

    @Test
    public void testAddUser() {
        when(userRepository.save(JANUSZ_UNSAVED)).thenReturn(Mono.just(JANUSZ));
        webTestClient
                .post()
                .uri("/functional/users")
                .body(Mono.just(JANUSZ_UNSAVED), User.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(1)
                .jsonPath("$.firstName").isEqualTo("Janusz");
    }

    @Test
    public void testDeleteUser() {
        Mockito.when(userRepository.deleteById("1")).thenReturn(Mono.empty());
        webTestClient
                .delete()
                .uri("/functional/users/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody().isEmpty();

    }
}
