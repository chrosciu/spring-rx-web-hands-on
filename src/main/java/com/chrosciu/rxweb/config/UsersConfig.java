package com.chrosciu.rxweb.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "users")
@Getter
@Setter
public class UsersConfig {
    private List<String> logins;
}
