package com.example.demo.service;

import com.example.demo.model.User;
import com.example.demo.model.Role;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

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

        Role userRole = user.getRole();
        if (userRole == null) {
            System.out.println("DEBUG: No role found for user");
            throw new UsernameNotFoundException("No role assigned to user: " + username);
        }

        String role = userRole.name();
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

    // User Management Methods
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public User createUser(User user) {
        // Encode password before saving
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public User updateUser(Long id, User userDetails) {
        return userRepository.findById(id).map(user -> {
            user.setEmail(userDetails.getEmail());
            user.setFirstName(userDetails.getFirstName());
            user.setLastName(userDetails.getLastName());
            user.setRole(userDetails.getRole());
            user.setActive(userDetails.isActive());
            
            // Only update password if it's provided
            if (userDetails.getPassword() != null && !userDetails.getPassword().isEmpty()) {
                user.setPassword(passwordEncoder.encode(userDetails.getPassword()));
            }
            
            return userRepository.save(user);
        }).orElseThrow(() -> new RuntimeException("User not found"));
    }

    public void deleteUser(Long id) {
        userRepository.findById(id).ifPresent(user -> {
            user.setActive(false);
            userRepository.save(user);
        });
    }

    public boolean changePassword(Long id, String oldPassword, String newPassword) {
        return userRepository.findById(id).map(user -> {
            if (passwordEncoder.matches(oldPassword, user.getPassword())) {
                user.setPassword(passwordEncoder.encode(newPassword));
                userRepository.save(user);
                return true;
            }
            return false;
        }).orElse(false);
    }

    // Permission checking method
    public boolean hasPermission(User user, String permission) {
        if (!user.isActive()) {
            return false;
        }

    switch (user.getRole().name()) {
        case "ROLE_ADMIN":
                return true; // Admin has all permissions
        case "ROLE_PUBLISHER":
                return permission.equals("PUBLISH_ARTICLE") || 
                       permission.equals("VIEW_ARTICLE") ||
                       permission.equals("REVIEW_ARTICLE");
        case "ROLE_REDACTOR":
                return permission.equals("CREATE_ARTICLE") || 
                       permission.equals("EDIT_ARTICLE") ||
                       permission.equals("VIEW_ARTICLE") ||
                       permission.equals("PUBLISH_OWN_ARTICLE");
        case "ROLE_SUBSCRIBER":
                return permission.equals("VIEW_ARTICLE");
            default:
                return false;
        }
    }
}
