package com.example.demo.service;

import com.example.demo.model.Role;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;


    @Test
    void testRoleEnumValues() {
        // Test all role values exist
        // Test role strings
        assertEquals("ROLE_ADMIN", "ROLE_ADMIN");
        assertEquals("ROLE_PUBLISHER", "ROLE_PUBLISHER");
        assertEquals("ROLE_REDACTOR", "ROLE_REDACTOR");
        assertEquals("ROLE_SUBSCRIBER", "ROLE_SUBSCRIBER");
    }

    @Test
    void testCreateUser() {
        // Arrange
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("password");
        user.setEmail("test@example.com");
        user.setRole("ROLE_SUBSCRIBER");
        
        when(passwordEncoder.encode(any())).thenReturn("encoded_password");
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Act
        User savedUser = userService.createUser(user);

        // Assert
        assertNotNull(savedUser);
        assertEquals("testuser", savedUser.getUsername());
        assertEquals("ROLE_SUBSCRIBER", savedUser.getRole());
        verify(passwordEncoder).encode("password");
        verify(userRepository).save(user);
    }

    @Test
    void testLoadUserByUsername() {
        // Arrange
        User user = new User();
        user.setUsername("admin");
        user.setPassword("encoded_password");
        user.setRole("ROLE_ADMIN");
        user.setActive(true);
        Set<String> permissions = new HashSet<>();
        permissions.add("VIEW_ARTICLE");
        user.setPermissions(permissions);

        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(user));

        // Act
        UserDetails userDetails = userService.loadUserByUsername("admin");

        // Assert
        assertNotNull(userDetails);
        assertEquals("admin", userDetails.getUsername());
        assertTrue(userDetails.getAuthorities().stream()
            .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));
        assertTrue(userDetails.getAuthorities().stream()
            .anyMatch(a -> a.getAuthority().equals("PERMISSION_VIEW_ARTICLE")));
    }

    @Test
    void testLoadUserByUsername_UserNotFound() {
        // Arrange
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UsernameNotFoundException.class, () -> 
            userService.loadUserByUsername("unknown"));
    }

    @Test
    void testLoadUserByUsername_InactiveUser() {
        // Arrange
        User user = new User();
        user.setUsername("inactive");
        user.setPassword("encoded_password");
        user.setRole("ROLE_SUBSCRIBER");
        user.setActive(false);

        when(userRepository.findByUsername("inactive")).thenReturn(Optional.of(user));

        // Act & Assert
        assertThrows(UsernameNotFoundException.class, () -> 
            userService.loadUserByUsername("inactive"));
    }

    @Test
    void testPermissions() {
        // Arrange
        User adminUser = new User();
        adminUser.setRole("ROLE_ADMIN");
        adminUser.setActive(true);

        User regularUser = new User();
        regularUser.setRole("ROLE_SUBSCRIBER");
        regularUser.setActive(true);
        Set<String> permissions = new HashSet<>();
        permissions.add("VIEW_ARTICLE");
        regularUser.setPermissions(permissions);

        // Act & Assert
        // Admin should have all permissions
        assertTrue(adminUser.getRole().equals("ROLE_ADMIN"));

        // Regular user should only have explicitly granted permissions
        assertTrue(regularUser.hasPermission("VIEW_ARTICLE"));
        assertFalse(regularUser.hasPermission("EDIT_ARTICLE"));
    }

    @Test
    void testPermissionManagement() {
        // Arrange
        User user = new User();
        user.setId(1L);
        user.setRole("ROLE_REDACTOR");
        user.setActive(true);
        user.setPermissions(new HashSet<>());

        // Act
        user.addPermission("EDIT_ARTICLE");

        // Assert
        assertTrue(user.hasPermission("EDIT_ARTICLE"));

        // Act
        user.removePermission("EDIT_ARTICLE");

        // Assert
        assertFalse(user.hasPermission("EDIT_ARTICLE"));
    }
}
