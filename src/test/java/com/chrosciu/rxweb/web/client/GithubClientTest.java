package com.chrosciu.rxweb.web.client;

import com.chrosciu.rxweb.model.GithubBranch;
import com.chrosciu.rxweb.model.GithubRepo;
import com.chrosciu.rxweb.model.GithubUser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;

public class GithubClientTest {
    private WireMockServer wireMockServer;
    private GithubClient githubClient;

    @BeforeEach
    public void setup() {
        wireMockServer = new WireMockServer(options().dynamicPort().dynamicHttpsPort());
        wireMockServer.start();
        githubClient = new GithubClient(
                String.format("http://localhost:%d", wireMockServer.port()), null, true);
    }

    @AfterEach
    public void cleanup() {
        wireMockServer.stop();
        wireMockServer.resetAll();
        wireMockServer = null;
    }

    @Test
    public void testGetRepos() {
        //given
        String user = "johndoe";
        GithubRepo repo1 = GithubRepo.builder().name("test").build();
        GithubRepo repo2 = GithubRepo.builder().name("foobar").build();
        List<GithubRepo> repos = Arrays.asList(repo1, repo2);
        String reposJson = writeJsonValue(repos);
        wireMockServer.stubFor(get(urlEqualTo(String.format("/users/%s/repos", user)))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(reposJson)));
        //when
        Flux<GithubRepo> reposResult = githubClient.getUserRepos(user);
        //then
        StepVerifier.create(reposResult)
                .expectNext(repo1, repo2)
                .verifyComplete();
    }

    @Test
    public void testGetUserNotProtectedBranches() {
        //given
        String user = "johndoe";
        GithubRepo repo1 = GithubRepo.builder().name("test").build();
        GithubRepo repo2 = GithubRepo.builder().name("foobar").build();
        List<GithubRepo> githubRepos = Arrays.asList(repo1, repo2);
        String githubReposJson = writeJsonValue(githubRepos);
        wireMockServer.stubFor(get(urlEqualTo(String.format("/users/%s/repos", user)))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(githubReposJson)));
        GithubBranch branch1 = GithubBranch.builder().name("master").protect(true).build();
        GithubBranch branch2 = GithubBranch.builder().name("develop").protect(true).build();
        GithubBranch branch3 = GithubBranch.builder().name("feature").protect(false).build();
        GithubBranch branch4 = GithubBranch.builder().name("master").protect(true).build();
        GithubBranch branch5 = GithubBranch.builder().name("develop").protect(true).build();
        GithubBranch branch6 = GithubBranch.builder().name("hotfix").protect(false).build();
        List<GithubBranch> branchesForRepo1 = Arrays.asList(branch1, branch2, branch3);
        List<GithubBranch> branchesForRepo2 = Arrays.asList(branch4, branch5, branch6);
        String branchesForRepo1Json = writeJsonValue(branchesForRepo1);
        String branchesForRepo2Json = writeJsonValue(branchesForRepo2);
        wireMockServer.stubFor(get(urlEqualTo(String.format("/repos/%s/%s/branches", user, repo1.getName())))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(branchesForRepo1Json)));
        wireMockServer.stubFor(get(urlEqualTo(String.format("/repos/%s/%s/branches", user, repo2.getName())))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(branchesForRepo2Json)));
        //when
        Mono<Long> branchesCount = githubClient.getUserNotProtectedBranchesCount(user);
        //then
        StepVerifier.create(branchesCount).expectNext(2L).verifyComplete();
    }


    @Test
    public void testGetUsersInRange() {
        //given
        List<GithubUser> users = LongStream
                .rangeClosed(1, 10)
                .mapToObj(i -> GithubUser.builder().id(i).login("user-" + i).build())
                .collect(Collectors.toList());
        IntStream.rangeClosed(0, 1)
                .map(i -> i * 5)
                .forEach(i -> {
                    wireMockServer.stubFor(get(urlEqualTo(String.format("/users?since=%d", i)))
                            .willReturn(aResponse()
                                    .withStatus(200)
                                    .withHeader("Content-Type", "application/json")
                                    .withBody(writeJsonValue(users.subList(i, i + 5)))));
                });
        //when
        Flux<GithubUser> result = githubClient.getUsersInRange(0, 7);
        //then
        StepVerifier.Step<GithubUser> verifier = StepVerifier.create(result);
        for (long i = 1; i <=7; ++i) {
            verifier = verifier.expectNext(GithubUser.builder().id(i).login("user-" + i).build());
        }
        verifier.verifyComplete();
    }

    private String writeJsonValue(Object object) {
        try {
            return new ObjectMapper().writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

}
