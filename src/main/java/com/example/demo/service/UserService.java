package com.example.demo.service;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.HashSet;

@Service
@Transactional
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // User statistics methods
    public long countAllUsers() {
        return userRepository.count();
    }

    public long countActiveUsers() {
        return userRepository.countByActiveTrue();
    }

    public long countUsersByRole(String role) {
        return userRepository.countByRole(role);
    }

    // User CRUD operations
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

    public User updateUser(Long id, User user) {
        return userRepository.findById(id).map(existingUser -> {
            existingUser.setUsername(user.getUsername());
            existingUser.setEmail(user.getEmail());
            existingUser.setFirstName(user.getFirstName());
            existingUser.setLastName(user.getLastName());
            existingUser.setRole(user.getRole());
            existingUser.setActive(user.isActive());
            // Only update password if it's provided and not already encoded
            if (user.getPassword() != null && !user.getPassword().startsWith("$2a$")) {
                existingUser.setPassword(passwordEncoder.encode(user.getPassword()));
            }
            return userRepository.save(existingUser);
        }).orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    // User status management
    public void activateUser(Long id) {
        userRepository.findById(id).ifPresent(user -> {
            user.setActive(true);
            userRepository.save(user);
        });
    }

    public void deactivateUser(Long id) {
        userRepository.findById(id).ifPresent(user -> {
            user.setActive(false);
            userRepository.save(user);
        });
    }

    // Role management
    public List<User> getUsersByRole(String role) {
        return userRepository.findByRole(role);
    }

    public void updateUserRole(Long id, String newRole) {
        userRepository.findById(id).ifPresent(user -> {
            user.setRole(newRole);
            userRepository.save(user);
        });
    }

    // Password management
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

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (!user.isActive()) {
            throw new UsernameNotFoundException("User is inactive");
        }

        Set<String> authorities = new HashSet<>();
        // Add role as authority
        authorities.add(user.getRole());
        // Add permissions as authorities
        user.getPermissions().forEach(permission -> 
            authorities.add("PERMISSION_" + permission));
        
        return org.springframework.security.core.userdetails.User.builder()
            .username(user.getUsername())
            .password(user.getPassword())
            .authorities(authorities.toArray(new String[0]))
            .build();
    }

    public boolean validatePassword(String username, String password) {
        Optional<User> user = userRepository.findByUsername(username);
        return user.map(u -> passwordEncoder.matches(password, u.getPassword()))
                  .orElse(false);
    }
}
