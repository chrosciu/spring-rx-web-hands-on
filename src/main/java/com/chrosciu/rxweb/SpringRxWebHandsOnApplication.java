package com.chrosciu.rxweb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

@SpringBootApplication
@EnableReactiveMongoRepositories
public class SpringRxWebHandsOnApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringRxWebHandsOnApplication.class, args);
	}

}
