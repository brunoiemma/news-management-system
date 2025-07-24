package com.example.demo.controller;

import com.example.demo.model.Role;
import com.example.demo.model.User;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

@Controller
@RequestMapping("/admin/users")
@PreAuthorize("hasRole('ADMIN')")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public String listUsers(Model model) {
        model.addAttribute("users", userService.getAllUsers());
        return "admin/users/list";
    }

    @GetMapping("/create")
    public String createUserForm(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("roles", Role.values());
        return "admin/users/create";
    }

    @PostMapping("/create")
    public String createUser(@ModelAttribute User user) {
        userService.createUser(user);
        return "redirect:/admin/users";
    }

    @GetMapping("/edit/{id}")
    public String editUserForm(@PathVariable Long id, Model model) {
        userService.getUserById(id).ifPresent(user -> {
            model.addAttribute("user", user);
            model.addAttribute("roles", Role.values());
        });
        return "admin/users/edit";
    }

    @PostMapping("/edit/{id}")
    public String updateUser(@PathVariable Long id, @ModelAttribute User user) {
        userService.updateUser(id, user);
        return "redirect:/admin/users";
    }

    @PostMapping("/delete/{id}")
    public String deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return "redirect:/admin/users";
    }

    @GetMapping("/change-password/{id}")
    public String changePasswordForm(@PathVariable Long id, Model model) {
        model.addAttribute("userId", id);
        return "admin/users/change-password";
    }

    @PostMapping("/change-password/{id}")
    public String changePassword(@PathVariable Long id,
                               @RequestParam String oldPassword,
                               @RequestParam String newPassword) {
        if (userService.changePassword(id, oldPassword, newPassword)) {
            return "redirect:/admin/users";
        } else {
            return "redirect:/admin/users/change-password/" + id + "?error";
        }
    }
}
