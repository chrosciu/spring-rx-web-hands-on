package com.chrosciu.rxweb.web.client;

import com.chrosciu.rxweb.model.GithubRepo;
import com.chrosciu.rxweb.model.GithubUser;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.concurrent.CountDownLatch;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@Slf4j
@Disabled("For manual run only")
public class GithubClientIT {
    @Autowired
    private GithubClient githubClient;

    private CountDownLatch latch;

    @BeforeEach
    public void setup() {
        latch = new CountDownLatch(1);
    }

    @AfterEach
    @SneakyThrows
    public void teardown() {
        latch.await();
    }

    @Test
    public void testGetUserRepos() {
        Flux<GithubRepo> repos = githubClient.getUserRepos("chrosciu");
        repos.doFinally(st -> latch.countDown()).subscribe(r -> log.info(r.getName()));
    }

    @Test
    public void testGetUserPublicBranchesCount() {
        Mono<Long> branchesCount = githubClient.getUserNotProtectedBranchesCount("chrosciu");
        branchesCount.doFinally(st -> latch.countDown()).subscribe(l -> log.info("{}", l));
    }

    @Test
    public void testGetUsersInRange() {
        Flux<GithubUser> repos = githubClient.getUsersInRange(200, 240);
        repos.doFinally(st -> latch.countDown()).subscribe(u -> log.info("{} -> {}", u.getId(), u.getLogin()));
    }
}
