package eu.chrost.rxweb.web.functional;

import eu.chrost.rxweb.model.GithubRepo;
import eu.chrost.rxweb.model.User;
import eu.chrost.rxweb.repository.UserRepository;
import eu.chrost.rxweb.web.client.GithubClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import static eu.chrost.rxweb.data.TestUsers.CHROSCIU;
import static eu.chrost.rxweb.data.TestUsers.OCTOCAT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GithubRouterTest {
    private UserRepository userRepository;
    private GithubClient githubClient;
    private GithubRouter githubRouter;
    private WebTestClient webTestClient;

    @BeforeEach
    public void setup() {
        userRepository = mock(UserRepository.class);
        githubClient = mock(GithubClient.class);
        githubRouter = new GithubRouter(userRepository, githubClient);
        webTestClient = WebTestClient.bindToRouterFunction(githubRouter.githubRoutes()).build();
    }

    @Test
    public void testGetAllUsersGithubRepos() {
        List<User> users = Arrays.asList(CHROSCIU, OCTOCAT);
        when(userRepository.findAll()).thenReturn(Flux.fromIterable(users));
        users.forEach(u -> {
            when(githubClient.getUserRepos(u.getLogin()))
                    .thenReturn(Flux.fromIterable(Arrays.asList(
                            GithubRepo.builder().name(u.getLogin() + "-first").build(),
                            GithubRepo.builder().name(u.getLogin() + "-second").build()
                    )));
        });
        webTestClient
                .get()
                .uri("/functional/github/users/repos")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.length()").isEqualTo(4)
                .jsonPath("$[*].name").value((Consumer<List<String>>) s -> assertThat(s).containsExactlyInAnyOrder(
                        "chrosciu-first", "chrosciu-second", "octocat-first", "octocat-second"));

    }

    @Test
    public void testGetUserGithubRepos() {
        when(userRepository.findById(CHROSCIU.getId())).thenReturn(Mono.just(CHROSCIU));
        when(githubClient.getUserRepos(CHROSCIU.getLogin()))
                .thenReturn(Flux.fromIterable(Arrays.asList(
                        GithubRepo.builder().name(CHROSCIU.getLogin() + "-first").build(),
                        GithubRepo.builder().name(CHROSCIU.getLogin() + "-second").build()
                )));
        webTestClient
                .get()
                .uri(String.format("/functional/github/users/%s/repos", CHROSCIU.getId()))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.length()").isEqualTo(2)
                .jsonPath("$[*].name").value((Consumer<List<String>>) s -> assertThat(s).containsExactlyInAnyOrder(
                "chrosciu-first", "chrosciu-second"));
    }

    @Test
    public void testGetUserGithubReposWithUserWithoutRepos() {
        when(userRepository.findById(CHROSCIU.getId())).thenReturn(Mono.just(CHROSCIU));
        when(githubClient.getUserRepos(CHROSCIU.getLogin())).thenReturn(Flux.empty());
        webTestClient
                .get()
                .uri(String.format("/functional/github/users/%s/repos", CHROSCIU.getId()))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.length()").isEqualTo(0);
    }

    @Test
    public void testGetUserGithubReposWithNonExistingUser() {
        when(userRepository.findById(CHROSCIU.getId())).thenReturn(Mono.empty());
        webTestClient
                .get()
                .uri(String.format("/functional/github/users/%s/repos", CHROSCIU.getId()))
                .exchange()
                .expectStatus().isNotFound();

    }
}
