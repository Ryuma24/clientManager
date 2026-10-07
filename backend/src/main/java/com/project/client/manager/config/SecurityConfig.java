package com.project.client.manager.config;

import com.project.client.manager.security.JwtAuthenticationFilter;
import com.project.client.manager.service.LoadUserDetailsService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  private final JwtAuthenticationFilter jwtAuthenticationFilter;
  private final LoadUserDetailsService loadUserDetailsService;

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

    http.cors(cors -> cors.configurationSource(corsConfigurationSource()))
        .authorizeHttpRequests(
            auth ->
                auth.requestMatchers(HttpMethod.OPTIONS, "/**")
                    .permitAll()
                    .requestMatchers(
                        "/",
                        "/index.html",
                        "/login.html",
                        "/register.html",
                        "/dashboard.html",
                        "/css/**",
                        "/js/**",
                        "/favicon.ico")
                    .permitAll()
                    .requestMatchers("/auth/**")
                    .permitAll()
                    .requestMatchers("/user/**")
                    .hasAnyRole("USER", "CLIENT")
                    .requestMatchers("/clients/**")
                    .hasRole("USER")
                    .requestMatchers(HttpMethod.POST, "/invoices/create")
                    .hasRole("USER")
                    .requestMatchers(HttpMethod.GET, "/invoices/client/**")
                    .hasAnyRole("USER", "CLIENT")
                    .requestMatchers(HttpMethod.GET, "/invoices/my")
                    .hasRole("CLIENT")
                    .requestMatchers("/payments/**")
                    .hasRole("CLIENT")
                    .requestMatchers("/admin/**")
                    .hasRole("ADMIN")
                    .anyRequest()
                    .authenticated());

    http.csrf(AbstractHttpConfigurer::disable)
        .sessionManagement(
            session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authenticationProvider(authenticationProvider())
        .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration)
      throws Exception {
    return configuration.getAuthenticationManager();
  }

  @Bean
  AuthenticationProvider authenticationProvider() {

    DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
    provider.setUserDetailsService(loadUserDetailsService);
    provider.setPasswordEncoder(passwordEncoder());
    return provider;
  }

  @Bean
  public CorsConfigurationSource corsConfigurationSource() {

    CorsConfiguration config = new CorsConfiguration();

    config.setAllowedOrigins(
        List.of(
            "http://localhost:5173",
            "http://127.0.0.1:5173",
            "https://client-manager-mg8s.vercel.app",
            "https://client-manager-mg8s-k55ojx7ai-ryuma24.vercel.app"));
    config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
    config.setAllowedHeaders(
        List.of(
            "Authorization",
            "Content-Type",
            "Accept",
            "Origin",
            "X-Requested-With",
            "Access-Control-Request-Method",
            "Access-Control-Request-Headers"));
    config.setAllowCredentials(true);
    config.setExposedHeaders(List.of("Authorization"));

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

    source.registerCorsConfiguration("/**", config);

    return source;
  }
}
