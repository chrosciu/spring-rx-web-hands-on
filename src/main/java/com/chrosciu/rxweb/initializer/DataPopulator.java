package com.chrosciu.rxweb.initializer;

import com.chrosciu.rxweb.model.User;
import com.chrosciu.rxweb.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataPopulator implements CommandLineRunner {
    private final UserRepository userRepository;

    @Override
    public void run(String... args) {
        User janusz = User.builder().firstName("Janusz").lastName("Cebulak").githubName("chrosciu").build();
        User mirek = User.builder().firstName("Mirek").lastName("Handlarz").githubName("octocat").build();
        userRepository.save(janusz).then(userRepository.save(mirek)).block();
        userRepository.findAll().subscribe(u -> System.out.println(u));
    }
}
