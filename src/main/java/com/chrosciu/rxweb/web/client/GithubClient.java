package com.chrosciu.rxweb.web.client;

import com.chrosciu.rxweb.model.GithubRepo;
import com.chrosciu.rxweb.model.GithubUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeFilterFunctions;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class GithubClient {
    private final WebClient webClient;

    public GithubClient(@Value("${github.url}") String githubUrl,
                        @Value("${github.username:#{null}}") String githubUsername,
                        @Value("${github.password:#{null}}") String githubPassword) {
        webClient = WebClient.builder()
                .baseUrl(githubUrl)
                .filter(buildAuthenticationFilter(githubUsername, githubPassword))
                .build();
    }

    private ExchangeFilterFunction buildAuthenticationFilter(String githubUsername, String githubPassword) {
        if (areCredentialsProvided(githubUsername, githubPassword)) {
            log.info("Created WebClient authentication filter with credentials(username: {})", githubUsername);
            return (request, next) -> {
                next.filter(ExchangeFilterFunctions.basicAuthentication(githubUsername, githubPassword));
                return next.exchange(request);
            };
        } else {
            log.info("Created WebClient authentication filter without credentials");
            return (request, next) -> next.exchange(request);
        }
    }

    private boolean areCredentialsProvided(String githubUsername, String githubPassword) {
        return githubUsername != null && !githubUsername.isEmpty() && githubPassword != null && !githubPassword.isEmpty();
    }

    //TODO Return all repos for given GitHub user
    //Use following GitHub endpoint: /users/{user}/repos
    public Flux<GithubRepo> getUserRepos(String user) {
        return null;
    }

    //TODO Return number of all not protected branches in all repos for given GitHub user
    //Use following GitHub endpoints: /users/{user}/repos, /repos/{user}/{repo}/branches and GithubBranch class
    public Mono<Long> getUserNotProtectedBranchesCount(String user) {
        return null;
    }

    //TODO Return all GitHub users with id in range from sinceId (exclusively) to toId (inclusively)
    //Use following GitHub endpoint: /users?since={sinceId}
    public Flux<GithubUser> getUsersInRange(long sinceId, long toId) {
        return null;
    }
}
