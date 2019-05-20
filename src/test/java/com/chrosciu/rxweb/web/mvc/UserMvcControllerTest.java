package com.chrosciu.rxweb.web.mvc;

import com.chrosciu.rxweb.model.User;
import com.chrosciu.rxweb.repository.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static com.chrosciu.rxweb.util.Users.JANUSZ;
import static com.chrosciu.rxweb.util.Users.MIREK;
import static org.mockito.Mockito.when;

public class UserMvcControllerTest {
    private UserRepository userRepository;
    private UserMvcController userMvcController;
    private WebTestClient webTestClient;

    @Before
    public void setup() {
        userRepository = Mockito.mock(UserRepository.class);
        userMvcController = new UserMvcController(userRepository);
        webTestClient = WebTestClient.bindToController(userMvcController).build();
    }

    @Test
    public void testGetAllUsers() {
        when(userRepository.findAll()).thenReturn(Flux.just(JANUSZ, MIREK));
        webTestClient
                .get()
                .uri("/mvc/users")
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
                .uri("/mvc/users/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.firstName").isEqualTo("Janusz");
    }

    @Test
    public void testAddUser() {
        when(userRepository.save(JANUSZ)).thenReturn(Mono.just(JANUSZ));
        webTestClient
                .post()
                .uri("/mvc/users")
                .body(Mono.just(JANUSZ), User.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.firstName").isEqualTo("Janusz");
    }

    @Test
    public void testDeleteUser() {
        Mockito.when(userRepository.deleteById("1")).thenReturn(Mono.empty());
        webTestClient
                .delete()
                .uri("/mvc/users/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody().isEmpty();
    }
}
