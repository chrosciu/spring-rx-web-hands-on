package com.chrosciu.rxweb.web.client;

import com.chrosciu.rxweb.model.GithubRepo;
import com.chrosciu.rxweb.model.GithubUser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

@Slf4j
public class GithubClientTest {
    private GithubClient githubClient;

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(wireMockConfig().dynamicPort().dynamicHttpsPort());

    @Before
    public void setup() {
        githubClient = new GithubClient(String.format("http://localhost:%d", wireMockRule.port()), null, null);
    }

    @Test
    public void testGetRepos() {
        //given
        String user = "johndoe";
        GithubRepo githubRepo1 = GithubRepo.builder().id(1L).name("test").build();
        GithubRepo githubRepo2 = GithubRepo.builder().id(2L).name("foobar").build();
        List<GithubRepo> githubRepos = Arrays.asList(githubRepo1, githubRepo2);
        String githubRepoJson = writeJsonValue(githubRepos);
        stubFor(get(urlEqualTo(String.format("/users/%s/repos", user)))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(githubRepoJson)));
        //when
        Flux<GithubRepo> repos = githubClient.getUserRepos(user);
        //then
        StepVerifier.create(repos)
                .expectNext(githubRepo1, githubRepo2)
                .verifyComplete();
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
                    stubFor(get(urlEqualTo(String.format("/users?since=%d", i)))
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
