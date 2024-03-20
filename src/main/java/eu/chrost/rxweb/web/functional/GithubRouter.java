package eu.chrost.rxweb.web.functional;

import eu.chrost.rxweb.repository.UserRepository;
import eu.chrost.rxweb.web.client.GithubClient;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
@RequiredArgsConstructor
public class GithubRouter {
    private final UserRepository userRepository;
    private final GithubClient githubClient;

    @Bean
    RouterFunction<ServerResponse> githubRoutes() {
        return null;
    }

    //TODO Create following REST endpoints using functional endpoints
    //GET /functional/github/users/repos - should return all GitHub repositories of all users
    //GET /functional/github/users/{id}/repos - should return all GitHub repositories of user with given ID.
    //(if there is no such user - HTTP 404 status code should be returned instead)
}
