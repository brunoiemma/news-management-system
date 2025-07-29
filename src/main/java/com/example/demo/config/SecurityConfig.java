package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
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
@EnableMethodSecurity(
    securedEnabled = true,
    jsr250Enabled = true,
    prePostEnabled = true
)
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
                // Public routes
                .requestMatchers(mvcMatcherBuilder.pattern("/")).permitAll()
                .requestMatchers(mvcMatcherBuilder.pattern("/home")).permitAll()
                .requestMatchers(mvcMatcherBuilder.pattern("/login")).permitAll()
                .requestMatchers(mvcMatcherBuilder.pattern("/css/**")).permitAll()
                .requestMatchers(mvcMatcherBuilder.pattern("/js/**")).permitAll()
                .requestMatchers(mvcMatcherBuilder.pattern("/images/**")).permitAll()
                .requestMatchers(mvcMatcherBuilder.pattern("/generateHash")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/h2-console/**")).permitAll()
                
                // Admin routes - Role Management
                .requestMatchers(mvcMatcherBuilder.pattern("/admin/roles/**")).hasRole("ADMIN")
                .requestMatchers(mvcMatcherBuilder.pattern("/admin/users/**")).hasRole("ADMIN")
                
                // Admin Dashboard
                .requestMatchers(mvcMatcherBuilder.pattern("/admin/dashboard"))
                    .hasAnyAuthority("ROLE_ADMIN", "ROLE_PUBLISHER", "ROLE_REDACTOR")
                
                // Article Management
                .requestMatchers(mvcMatcherBuilder.pattern("/admin/articles/create"))
                    .hasAnyAuthority("PERMISSION_CREATE_ARTICLE")
                .requestMatchers(mvcMatcherBuilder.pattern("/admin/articles/edit/**"))
                    .hasAnyAuthority("PERMISSION_EDIT_ARTICLE")
                .requestMatchers(mvcMatcherBuilder.pattern("/admin/articles/delete/**"))
                    .hasAnyAuthority("PERMISSION_DELETE_ARTICLE")
                .requestMatchers(mvcMatcherBuilder.pattern("/admin/articles/publish/**"))
                    .hasAnyAuthority("PERMISSION_PUBLISH_ARTICLE")
                .requestMatchers(mvcMatcherBuilder.pattern("/admin/articles"))
                    .hasAnyAuthority("PERMISSION_VIEW_ARTICLE")
                
                // Public article access
                .requestMatchers(mvcMatcherBuilder.pattern("/articles/**"))
                    .hasAnyAuthority("PERMISSION_VIEW_ARTICLE")
                
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
