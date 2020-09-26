package com.chrosciu.rxweb.init;

import com.chrosciu.rxweb.config.UsersConfig;
import com.chrosciu.rxweb.model.User;
import com.chrosciu.rxweb.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

@Component
@RequiredArgsConstructor
@Slf4j
public class DBInitializer implements CommandLineRunner {
    private final UsersConfig usersConfig;
    private final UserRepository userRepository;

    @Override
    public void run(String... args) {
        Flux.fromIterable(usersConfig.getLogins())
                .map(login -> User.builder().login(login).build())
                .flatMap(userRepository::save)
                .doOnSubscribe(s -> log.info("Database initialization - inserting users:"))
                .doOnComplete(() -> log.info("Database initialized - users inserted"))
        .subscribe(u -> log.info("{}", u));
    }
}
