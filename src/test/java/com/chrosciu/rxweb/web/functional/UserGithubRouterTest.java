package com.chrosciu.rxweb.web.functional;

import com.chrosciu.rxweb.model.GithubRepo;
import com.chrosciu.rxweb.model.User;
import com.chrosciu.rxweb.repository.UserRepository;
import com.chrosciu.rxweb.web.client.GithubClient;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import static com.chrosciu.rxweb.util.Users.JANUSZ;
import static com.chrosciu.rxweb.util.Users.MIREK;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserGithubRouterTest {
    private UserRepository userRepository;
    private GithubClient githubClient;
    private UserGithubRouter userGithubRouter;
    private WebTestClient webTestClient;

    @Before
    public void setup() {
        userRepository = mock(UserRepository.class);
        githubClient = mock(GithubClient.class);
        userGithubRouter = new UserGithubRouter(userRepository, githubClient);
        webTestClient = WebTestClient.bindToRouterFunction(userGithubRouter.userGithubRoutes()).build();
    }

    @Test
    public void testGetAllUsersRepos() {
        List<User> users = Arrays.asList(JANUSZ, MIREK);
        when(userRepository.findAll()).thenReturn(Flux.fromIterable(users));
        users.forEach(u -> {
            when(githubClient.getUserRepos(u.getGithubName()))
                    .thenReturn(Flux.fromIterable(Arrays.asList(
                            GithubRepo.builder().name(u.getGithubName() + "-first").build(),
                            GithubRepo.builder().name(u.getGithubName() + "-second").build()
                    )));
        });
        webTestClient
                .get()
                .uri("/functional/users/repos")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.length()").isEqualTo(4)
                .jsonPath("$[*].name").value((Consumer<List<String>>) s -> assertThat(s).containsExactlyInAnyOrder(
                        "chrosciu-first", "chrosciu-second", "octocat-first", "octocat-second"));

    }
}
