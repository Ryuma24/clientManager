package com.project.client.manager.config;

import com.project.client.manager.security.JwtAuthenticationFilter;
import com.project.client.manager.service.LoadUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final LoadUserDetailsService loadUserDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {



        http.authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/index.html", "/login.html", "/register.html", "/dashboard.html", "/css/**", "/js/**", "/favicon.ico").permitAll()
                .requestMatchers("/auth/**").permitAll()
                .requestMatchers("/user/**").hasRole("USER")
                .requestMatchers("/client/**").hasRole("CLIENT")
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated());

        http.csrf(csrf->csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(
                        SessionCreationPolicy.STATELESS
                ))
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    AuthenticationProvider authenticationProvider(){

        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(loadUserDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

}

