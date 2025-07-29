package com.example.demo.controller;

import com.example.demo.model.Role;
import com.example.demo.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/roles")
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class RoleController {

    private final RoleService roleService;

    @Autowired
    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @GetMapping
    public String listRoles(Model model) {
        model.addAttribute("roles", roleService.getAllRoles());
        return "admin/roles/list";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("role", new Role());
        return "admin/roles/create";
    }

    @PostMapping("/create")
    public String createRole(@ModelAttribute Role role, RedirectAttributes redirectAttributes) {
        try {
            roleService.createRole(role);
            redirectAttributes.addFlashAttribute("success", "Role created successfully");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/admin/roles/create";
        }
        return "redirect:/admin/roles";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Role role = roleService.getRoleById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid role Id:" + id));
            model.addAttribute("role", role);
            return "admin/roles/edit";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/admin/roles";
        }
    }

    @PostMapping("/edit/{id}")
    public String updateRole(@PathVariable Long id, @ModelAttribute Role role, 
                           RedirectAttributes redirectAttributes) {
        try {
            roleService.updateRole(id, role);
            redirectAttributes.addFlashAttribute("success", "Role updated successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/admin/roles/edit/" + id;
        }
        return "redirect:/admin/roles";
    }

    @PostMapping("/delete/{id}")
    public String deleteRole(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            roleService.deleteRole(id);
            redirectAttributes.addFlashAttribute("success", "Role deleted successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/roles";
    }
}
