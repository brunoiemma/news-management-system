package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;


@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, HandlerMappingIntrospector introspector) throws Exception {
        MvcRequestMatcher.Builder mvcMatcherBuilder = new MvcRequestMatcher.Builder(introspector);

        http
            .csrf((csrf) -> csrf
                .ignoringRequestMatchers(new AntPathRequestMatcher("/h2-console/**"))
                .ignoringRequestMatchers(new AntPathRequestMatcher("/admin/**"))) // Disable CSRF for admin endpoints temporarily
            .headers((headers) -> headers
                .frameOptions((frame) -> frame.sameOrigin()))
            .authorizeHttpRequests((requests) -> requests
                .requestMatchers(mvcMatcherBuilder.pattern("/")).permitAll()
                .requestMatchers(mvcMatcherBuilder.pattern("/home")).permitAll()
                .requestMatchers(mvcMatcherBuilder.pattern("/login")).permitAll()
                .requestMatchers(mvcMatcherBuilder.pattern("/css/**")).permitAll()
                .requestMatchers(mvcMatcherBuilder.pattern("/js/**")).permitAll()
                .requestMatchers(mvcMatcherBuilder.pattern("/images/**")).permitAll()
                .requestMatchers(mvcMatcherBuilder.pattern("/generateHash")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/h2-console/**")).permitAll()
                // Admin routes
                .requestMatchers(mvcMatcherBuilder.pattern("/admin/users/**")).hasRole("ADMIN")
                .requestMatchers(mvcMatcherBuilder.pattern("/admin/dashboard")).hasAnyRole("ADMIN", "PUBLISHER", "REDACTOR")
                // Article management routes
                .requestMatchers(mvcMatcherBuilder.pattern("/admin/articles/create")).hasAnyRole("ADMIN", "REDACTOR")
                .requestMatchers(mvcMatcherBuilder.pattern("/admin/articles/edit/**")).hasAnyRole("ADMIN", "REDACTOR")
                .requestMatchers(mvcMatcherBuilder.pattern("/admin/articles/delete/**")).hasRole("ADMIN")
                .requestMatchers(mvcMatcherBuilder.pattern("/admin/articles")).hasAnyRole("ADMIN", "PUBLISHER", "REDACTOR")
                // Public routes
                .requestMatchers(mvcMatcherBuilder.pattern("/articles/**")).hasAnyRole("ADMIN", "PUBLISHER", "REDACTOR", "SUBSCRIBER")
                .anyRequest().authenticated()
            )
            .formLogin((form) -> form
                .loginPage("/login")
                .defaultSuccessUrl("/admin/dashboard", true)
                .permitAll()
            )
            .logout((logout) -> logout
                .logoutSuccessUrl("/login?logout")
                .permitAll()
            );

        return http.build();
    }

    @Bean 
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
}
