package com.example.demo.controller;

import com.example.demo.model.Role;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/roles")
public class RoleController {

    @GetMapping
    public String listRoles(Model model) {
        model.addAttribute("roles", Role.values());
        return "admin/roles/list";
    }

    @GetMapping("/help")
    public String roleHelp(Model model) {
        model.addAttribute("roles", Role.values());
        return "admin/roles/help";
    }
}
