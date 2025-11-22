package com.ratingsystem.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationConfig jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;

    private static final String[] SWAGGER_WHITELIST = {
            "/swagger-ui.html",
            "/swagger-ui/**",
            "/v3/api-docs",
            "/v3/api-docs/**",
            "/api-docs/**",
            "/swagger-resources/**",
            "/webjars/**"
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        return http
                .csrf(csrf -> csrf.disable())

                .sessionManagement(sm ->
                        sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                .authorizeHttpRequests(auth -> auth

                                // Swagger
                                .requestMatchers(SWAGGER_WHITELIST).permitAll()

                                // Auth
                                .requestMatchers("/api/auth/**").permitAll()

                                // Public GETs
                                .requestMatchers(HttpMethod.GET,
                                        "/api/objects/**"
                                ).permitAll()

                                .requestMatchers(HttpMethod.GET,
                                        "/api/sellers/**"
                                ).permitAll()

                                .requestMatchers(HttpMethod.GET,
                                        "/api/comments/**"
                                ).permitAll()
                                .requestMatchers(HttpMethod.POST,
                                        "/api/comments/**"
                                ).permitAll().requestMatchers(HttpMethod.DELETE,
                                        "/api/comments/**"
                                ).permitAll()

//
//                                .requestMatchers(HttpMethod.GET,
//                                        "/api/sellers/**"
//                                ).permitAll()


//                        .requestMatchers(HttpMethod.GET, "/api/objects/**").permitAll()

                                .requestMatchers(HttpMethod.POST, "/api/comments/**").permitAll()

                                // Seller
                                .requestMatchers(HttpMethod.POST, "/api/objects").hasRole("SELLER")
                                .requestMatchers(HttpMethod.PUT, "/api/objects/**").hasRole("SELLER")
                                .requestMatchers(HttpMethod.DELETE, "/api/objects/**").hasRole("SELLER")

                                // Admin
                                .requestMatchers("/api/admin/**").hasRole("ADMIN")

                                // anything else
                                .anyRequest().authenticated()
                )

                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)

                .build();
    }
}
