package eu.chrost.rxweb.init;

import eu.chrost.rxweb.config.UsersConfig;
import eu.chrost.rxweb.model.User;
import eu.chrost.rxweb.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.concurrent.CountDownLatch;

@Component
@RequiredArgsConstructor
@Slf4j
public class DBInitializer implements CommandLineRunner {
    private final UsersConfig usersConfig;
    private final UserRepository userRepository;

    private CountDownLatch latch = new CountDownLatch(1);

    @SneakyThrows
    public void await() {
        latch.await();
    }

    @Override
    public void run(String... args) {
        userRepository
                .deleteAll()
                .thenMany(
                        Flux.fromIterable(usersConfig.getLogins())
                                .map(login -> User.builder().login(login).build())
                                .flatMap(userRepository::save)
                                .doOnSubscribe(s -> log.info("Database initialization - inserting users:"))
                                .doOnComplete(() -> log.info("Database initialized - users inserted"))
                )
                .doFinally(st -> latch.countDown())
                .subscribe(u -> log.info("{}", u));
    }
}
