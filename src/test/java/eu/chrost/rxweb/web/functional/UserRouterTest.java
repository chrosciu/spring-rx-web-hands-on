package eu.chrost.rxweb.web.functional;

import eu.chrost.rxweb.model.User;
import eu.chrost.rxweb.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static eu.chrost.rxweb.data.TestUsers.CHROSCIU;
import static eu.chrost.rxweb.data.TestUsers.CHROSCIU_UNSAVED;
import static eu.chrost.rxweb.data.TestUsers.OCTOCAT;
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
        when(userRepository.findAll()).thenReturn(Flux.just(CHROSCIU, OCTOCAT));
        webTestClient
                .get()
                .uri("/functional/users")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.length()").isEqualTo(2)
                .jsonPath("$[0].login").isEqualTo(CHROSCIU.getLogin())
                .jsonPath("$[1].login").isEqualTo(OCTOCAT.getLogin());
    }

    @Test
    public void testGetUser() {
        when(userRepository.findById(CHROSCIU.getId())).thenReturn(Mono.just(CHROSCIU));
        webTestClient
                .get()
                .uri(String.format("/functional/users/%s", CHROSCIU.getId()))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.login").isEqualTo(CHROSCIU.getLogin());
    }

    @Test
    public void testAddUser() {
        when(userRepository.save(CHROSCIU_UNSAVED)).thenReturn(Mono.just(CHROSCIU));
        webTestClient
                .post()
                .uri("/functional/users")
                .body(Mono.just(CHROSCIU_UNSAVED), User.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(CHROSCIU.getId())
                .jsonPath("$.login").isEqualTo(CHROSCIU.getLogin());
    }

    @Test
    public void testDeleteUser() {
        Mockito.when(userRepository.deleteById(CHROSCIU.getId())).thenReturn(Mono.empty());
        webTestClient
                .delete()
                .uri(String.format("/functional/users/%s", CHROSCIU.getId()))
                .exchange()
                .expectStatus().isOk()
                .expectBody().isEmpty();
    }
}
