package com.example.demo.service;

import com.example.demo.model.Role;
import com.example.demo.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class RoleService {
    private final RoleRepository roleRepository;

    @Autowired
    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Transactional(readOnly = true)
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Role> getRoleById(Long id) {
        return roleRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<Role> getRoleByName(String name) {
        return roleRepository.findByName(name);
    }

    @Transactional
    public Role createRole(Role role) {
        if (roleRepository.existsByName(role.getName())) {
            throw new IllegalArgumentException("Role with name " + role.getName() + " already exists");
        }
        return roleRepository.save(role);
    }

    @Transactional
    public Role updateRole(Long id, Role roleDetails) {
        Role role = roleRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Role not found with id: " + id));

        if (!role.getName().equals(roleDetails.getName()) && 
            roleRepository.existsByName(roleDetails.getName())) {
            throw new IllegalArgumentException("Role name " + roleDetails.getName() + " is already taken");
        }

        role.setName(roleDetails.getName());
        role.setDescription(roleDetails.getDescription());

        return roleRepository.save(role);
    }

    @Transactional
    public void deleteRole(Long id) {
        if (!roleRepository.existsById(id)) {
            throw new ResourceNotFoundException("Role not found with id: " + id);
        }
        roleRepository.deleteById(id);
    }

    @Transactional
    public void initializeDefaultRoles() {
        if (roleRepository.count() == 0) {
            createRole(new Role("ROLE_ADMIN", "Full access to everything"));
            createRole(new Role("ROLE_PUBLISHER", "Can review and publish articles"));
            createRole(new Role("ROLE_REDACTOR", "Can create and edit articles"));
            createRole(new Role("ROLE_SUBSCRIBER", "Can only read published articles"));
        }
    }
}
