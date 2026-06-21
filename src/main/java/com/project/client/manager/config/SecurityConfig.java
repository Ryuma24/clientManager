package com.project.client.manager.config;

import com.project.client.manager.model.Role;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.authorizeHttpRequests(auth -> auth
                .requestMatchers("/user/**").hasRole(Role.USER.name())
                .requestMatchers("/client/**").hasRole(Role.CLIENT.name())
                .requestMatchers("/admin/**").hasRole(Role.ADMIN.name()))
                .formLogin(form -> form.loginPage("/auth/login"));

        return http.build();
    }

}

