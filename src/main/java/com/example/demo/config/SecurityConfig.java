package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, org.springframework.web.servlet.handler.HandlerMappingIntrospector introspector) throws Exception {
        org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher.Builder mvcMatcherBuilder = 
            new org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher.Builder(introspector);

        http
            .authorizeHttpRequests(auth -> auth
                // Public resources
                .requestMatchers(mvcMatcherBuilder.pattern("/css/**")).permitAll()
                .requestMatchers(mvcMatcherBuilder.pattern("/js/**")).permitAll()
                .requestMatchers(mvcMatcherBuilder.pattern("/images/**")).permitAll()
                .requestMatchers(mvcMatcherBuilder.pattern("/favicon.ico")).permitAll()
                .requestMatchers(mvcMatcherBuilder.pattern("/")).permitAll()
                .requestMatchers(mvcMatcherBuilder.pattern("/home")).permitAll()
                .requestMatchers(mvcMatcherBuilder.pattern("/articles")).permitAll()
                .requestMatchers(mvcMatcherBuilder.pattern("/articles/view/**")).permitAll()
                .requestMatchers(mvcMatcherBuilder.pattern("/register")).permitAll()
                .requestMatchers(mvcMatcherBuilder.pattern("/login")).permitAll()
                .requestMatchers(mvcMatcherBuilder.pattern("/error")).permitAll()
                .requestMatchers(org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher("/h2-console/**")).permitAll()

                // Admin dashboard and functionalities
                .requestMatchers(mvcMatcherBuilder.pattern("/admin/**")).hasRole("ADMIN")
                .requestMatchers(mvcMatcherBuilder.pattern("/users/**")).hasRole("ADMIN")
                .requestMatchers(mvcMatcherBuilder.pattern("/roles/**")).hasRole("ADMIN")

                // Publisher dashboard and functionalities
                .requestMatchers(mvcMatcherBuilder.pattern("/publisher/**")).hasRole("PUBLISHER")
                .requestMatchers(mvcMatcherBuilder.pattern("/articles/publish/**")).hasAuthority("PUBLISH_ARTICLE")
                .requestMatchers(mvcMatcherBuilder.pattern("/articles/review/**")).hasAuthority("PUBLISH_ARTICLE")

                // Redactor dashboard and functionalities
                .requestMatchers(mvcMatcherBuilder.pattern("/redactor/**")).hasRole("REDACTOR")
                .requestMatchers(mvcMatcherBuilder.pattern("/articles/create")).hasAuthority("CREATE_ARTICLE")
                .requestMatchers(mvcMatcherBuilder.pattern("/articles/edit/**")).hasAuthority("CREATE_ARTICLE")

                // Subscriber dashboard and functionalities
                .requestMatchers(mvcMatcherBuilder.pattern("/subscriber/**")).hasRole("SUBSCRIBER")
                .requestMatchers(mvcMatcherBuilder.pattern("/articles/read/**")).hasAuthority("VIEW_ARTICLE")

                // Require authentication for all other requests
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/dashboard", true)
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            )
            .sessionManagement(session -> session
                .maximumSessions(1)
                .expiredUrl("/login?expired")
            )
            .csrf(csrf -> csrf
                .ignoringRequestMatchers(org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher("/h2-console/**"))
            )
            .headers(headers -> headers
                .frameOptions(frameOptions -> frameOptions.disable())
            );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
