package com.example.demo.repository;

import com.example.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // Basic queries
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);

    // Count queries
    long countByActiveTrue();
    long countByRole(String role);
    long countByRoleAndActiveTrue(String role);

    // Role-based queries
    List<User> findByRole(String role);
    List<User> findByRoleAndActiveTrue(String role);

    // Status-based queries
    List<User> findByActiveTrue();
    List<User> findByActiveFalse();

    // Combined queries
    List<User> findByRoleAndActiveTrueOrderByCreatedAtDesc(String role);
    List<User> findByRoleInAndActiveTrue(List<String> roles);
}
