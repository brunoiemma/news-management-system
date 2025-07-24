package com.example.demo.service;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("==================== LOGIN ATTEMPT ====================");
        System.out.println("DEBUG: Attempting to load user: " + username);
        
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        System.out.println("DEBUG: Found user in database:");
        System.out.println("DEBUG: ID: " + user.getId());
        System.out.println("DEBUG: Username: " + user.getUsername());
        System.out.println("DEBUG: Password hash length: " + (user.getPassword() != null ? user.getPassword().length() : "null"));
        System.out.println("DEBUG: Raw role value: " + user.getRole());
        System.out.println("DEBUG: Active status: " + user.isActive());

        if (!user.isActive()) {
            System.out.println("DEBUG: User is not active, throwing exception");
            throw new UsernameNotFoundException("User is not active: " + username);
        }

        String role = user.getRole();
        if (role == null || role.trim().isEmpty()) {
            System.out.println("DEBUG: No role found for user");
            throw new UsernameNotFoundException("No role assigned to user: " + username);
        }

        System.out.println("DEBUG: Raw password from database: [" + user.getPassword() + "]");
        System.out.println("DEBUG: Password length: " + user.getPassword().length());
        System.out.println("DEBUG: Password characters:");
        for (char c : user.getPassword().toCharArray()) {
            System.out.println("  " + c + " (" + (int)c + ")");
        }
        
        // Remove ROLE_ prefix if present
        String processedRole = role.startsWith("ROLE_") ? role.substring(5) : role;
        System.out.println("DEBUG: Processed role: " + processedRole);
            
        UserDetails userDetails = org.springframework.security.core.userdetails.User
            .withUsername(username)
            .password(user.getPassword())
            .roles(processedRole)
            .build();
            
        System.out.println("DEBUG: Created UserDetails with:");
        System.out.println("  Username: " + userDetails.getUsername());
        System.out.println("  Password: [" + userDetails.getPassword() + "]");
        System.out.println("  Authorities: " + userDetails.getAuthorities());
        System.out.println("====================================================");
            
        System.out.println("DEBUG: Created UserDetails successfully");
        System.out.println("DEBUG: Assigned authorities: " + userDetails.getAuthorities());
        return userDetails;
    }
}
