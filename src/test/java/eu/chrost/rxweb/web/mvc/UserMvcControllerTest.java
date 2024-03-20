package eu.chrost.rxweb.web.mvc;

import eu.chrost.rxweb.model.User;
import eu.chrost.rxweb.repository.UserRepository;

import eu.chrost.rxweb.data.TestUsers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.mockito.Mockito.when;

public class UserMvcControllerTest {
    private UserRepository userRepository;
    private UserMvcController userMvcController;
    private WebTestClient webTestClient;

    @BeforeEach
    public void setup() {
        userRepository = Mockito.mock(UserRepository.class);
        userMvcController = new UserMvcController(userRepository);
        webTestClient = WebTestClient.bindToController(userMvcController).build();
    }

    @Test
    public void testGetAllUsers() {
        when(userRepository.findAll()).thenReturn(Flux.just(TestUsers.CHROSCIU, TestUsers.OCTOCAT));
        webTestClient
                .get()
                .uri("/mvc/users")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.length()").isEqualTo(2)
                .jsonPath("$[0].login").isEqualTo(TestUsers.CHROSCIU.getLogin())
                .jsonPath("$[1].login").isEqualTo(TestUsers.OCTOCAT.getLogin());
    }

    @Test
    public void testGetUser() {
        when(userRepository.findById(TestUsers.CHROSCIU.getId())).thenReturn(Mono.just(TestUsers.CHROSCIU));
        webTestClient
                .get()
                .uri(String.format("/mvc/users/%s", TestUsers.CHROSCIU.getId()))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.login").isEqualTo(TestUsers.CHROSCIU.getLogin());
    }

    @Test
    public void testAddUser() {
        when(userRepository.save(TestUsers.CHROSCIU_UNSAVED)).thenReturn(Mono.just(TestUsers.CHROSCIU));
        webTestClient
                .post()
                .uri("/mvc/users")
                .body(Mono.just(TestUsers.CHROSCIU_UNSAVED), User.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(TestUsers.CHROSCIU.getId())
                .jsonPath("$.login").isEqualTo(TestUsers.CHROSCIU.getLogin());
    }

    @Test
    public void testDeleteUser() {
        Mockito.when(userRepository.deleteById(TestUsers.CHROSCIU.getId())).thenReturn(Mono.empty());
        webTestClient
                .delete()
                .uri(String.format("/mvc/users/%s", TestUsers.CHROSCIU.getId()))
                .exchange()
                .expectStatus().isOk()
                .expectBody().isEmpty();
    }
}
