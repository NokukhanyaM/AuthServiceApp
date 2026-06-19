package com.example.AuthServiceApp.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private  final JwtAuthenticationFilter jwtFilter;


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Essential for POST requests in Postman

                //Tell Spring to use the Session Repository for every request
                .securityContext(context -> context
                        .securityContextRepository(securityContextRepository())
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/login","/api/users/register", "/access-denied", "/users/all").permitAll()
                        .requestMatchers("/dashboard/**").hasAnyRole("USER", "ADMIN")
                        .anyRequest().authenticated()
                )
                        .formLogin(form -> form
                                .loginPage("/api/auth/login")
                                .loginProcessingUrl("/perform_login_internal")
                                .defaultSuccessUrl("/dashboard", true)
                                .permitAll()
                        )
                        // Logout support
                        .logout(logout -> logout
                                .logoutUrl("/api/auth/logout")
                                .logoutSuccessUrl("/api/auth/login?logout")
                                .permitAll()
                        )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {

        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityContextRepository securityContextRepository() {
        // This MUST be the same type used in your AuthenticationService
        return new HttpSessionSecurityContextRepository();
    }
}
