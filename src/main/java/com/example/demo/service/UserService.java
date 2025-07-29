package com.example.demo.service;

import com.example.demo.model.User;
import com.example.demo.model.Role;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleService roleService;

    @Autowired
    public UserService(UserRepository userRepository, 
                      PasswordEncoder passwordEncoder,
                      RoleService roleService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleService = roleService;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("==================== LOGIN ATTEMPT ====================");
        System.out.println("DEBUG: Attempting to load user: " + username);
        
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        System.out.println("DEBUG: Found user in database:");
        System.out.println("DEBUG: ID: " + user.getId());
        System.out.println("DEBUG: Username: " + user.getUsername());
        System.out.println("DEBUG: Password hash length: " + (user.getPassword() != null ? user.getPassword().length() : "null"));
        System.out.println("DEBUG: Role: " + user.getRole().getName());
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

        // Create authorities from role and permissions
        List<SimpleGrantedAuthority> authorities = user.getPermissions().stream()
            .map(permission -> new SimpleGrantedAuthority("PERMISSION_" + permission))
            .collect(Collectors.toList());
        
        // Add role as an authority
        authorities.add(new SimpleGrantedAuthority(userRole.getName()));
            
        UserDetails userDetails = org.springframework.security.core.userdetails.User
            .withUsername(username)
            .password(user.getPassword())
            .authorities(authorities)
            .build();
            
        System.out.println("DEBUG: Created UserDetails with:");
        System.out.println("  Username: " + userDetails.getUsername());
        System.out.println("  Password: [" + userDetails.getPassword() + "]");
        System.out.println("  Authorities: " + userDetails.getAuthorities());
        System.out.println("====================================================");
            
        return userDetails;
    }

    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Transactional
    public User createUser(User user) {
        // Encode password before saving
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        
        // Validate role exists
        Role role = roleService.getRoleById(user.getRole().getId())
            .orElseThrow(() -> new IllegalArgumentException("Invalid role specified"));
        user.setRole(role);
        
        return userRepository.save(user);
    }

    @Transactional
    public User updateUser(Long id, User userDetails) {
        return userRepository.findById(id).map(user -> {
            user.setEmail(userDetails.getEmail());
            user.setFirstName(userDetails.getFirstName());
            user.setLastName(userDetails.getLastName());
            
            // Validate and set role if changed
            if (userDetails.getRole() != null && 
                !user.getRole().getId().equals(userDetails.getRole().getId())) {
                Role role = roleService.getRoleById(userDetails.getRole().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid role specified"));
                user.setRole(role);
            }
            
            user.setActive(userDetails.isActive());
            
            // Update permissions if provided
            if (userDetails.getPermissions() != null) {
                user.setPermissions(userDetails.getPermissions());
            }
            
            // Only update password if it's provided
            if (userDetails.getPassword() != null && !userDetails.getPassword().isEmpty()) {
                user.setPassword(passwordEncoder.encode(userDetails.getPassword()));
            }
            
            return userRepository.save(user);
        }).orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

    @Transactional
    public void deleteUser(Long id) {
        userRepository.findById(id).ifPresent(user -> {
            user.setActive(false);
            userRepository.save(user);
        });
    }

    @Transactional
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

    @Transactional(readOnly = true)
    public boolean hasPermission(User user, String permission) {
        if (!user.isActive()) {
            return false;
        }

        // Admin role has all permissions
        if (user.getRole().getName().equals("ROLE_ADMIN")) {
            return true;
        }

        // Check user's explicit permissions
        return user.getPermissions().contains(permission);
    }

    @Transactional
    public void addPermission(Long userId, String permission) {
        userRepository.findById(userId).ifPresent(user -> {
            user.addPermission(permission);
            userRepository.save(user);
        });
    }

    @Transactional
    public void removePermission(Long userId, String permission) {
        userRepository.findById(userId).ifPresent(user -> {
            user.removePermission(permission);
            userRepository.save(user);
        });
    }
}
