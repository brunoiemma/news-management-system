package com.example.demo.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.example.demo.service.UserService;

@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public CustomAuthenticationProvider(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();
        
        System.out.println("DEBUG: CustomAuthenticationProvider - Attempting authentication for user: " + username);
        System.out.println("DEBUG: CustomAuthenticationProvider - Raw password received: [" + password + "]");
        
        UserDetails user = userService.loadUserByUsername(username);
        
        System.out.println("DEBUG: CustomAuthenticationProvider - Stored password hash: [" + user.getPassword() + "]");
        System.out.println("DEBUG: CustomAuthenticationProvider - About to compare passwords...");
        
        boolean matches = passwordEncoder.matches(password, user.getPassword());
        System.out.println("DEBUG: Password match result: " + matches);
        
        if (matches) {
            System.out.println("DEBUG: CustomAuthenticationProvider - Password match successful!");
            return new UsernamePasswordAuthenticationToken(user, password, user.getAuthorities());
        }
        
        System.out.println("DEBUG: CustomAuthenticationProvider - Password match failed!");
        throw new BadCredentialsException("Invalid username or password");
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
