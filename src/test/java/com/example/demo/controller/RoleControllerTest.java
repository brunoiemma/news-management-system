package com.example.demo.controller;

import com.example.demo.config.TestSecurityConfig;
import com.example.demo.model.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@WebMvcTest(controllers = RoleController.class)
@Import(TestSecurityConfig.class)
public class RoleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void listRoles_ShouldReturnRolesList() throws Exception {
        mockMvc.perform(get("/admin/roles").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/roles/list"))
                .andExpect(model().attributeExists("roles"))
                .andExpect(model().attribute("roles", Role.values()));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void roleHelp_ShouldReturnHelpPage() throws Exception {
        mockMvc.perform(get("/admin/roles/help").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/roles/help"))
                .andExpect(model().attributeExists("roles"))
                .andExpect(model().attribute("roles", Role.values()));
    }

    @Test
    @WithMockUser(username = "redactor", roles = "REDACTOR")
    public void listRoles_WithNonAdminUser_ShouldReturnForbidden() throws Exception {
        mockMvc.perform(get("/admin/roles").with(csrf()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "redactor", roles = "REDACTOR")
    public void roleHelp_WithNonAdminUser_ShouldReturnForbidden() throws Exception {
        mockMvc.perform(get("/admin/roles/help").with(csrf()))
                .andExpect(status().isForbidden());
    }

    @Test
    public void listRoles_WithoutAuthentication_ShouldRedirectToLogin() throws Exception {
        mockMvc.perform(get("/admin/roles").with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    public void roleHelp_WithoutAuthentication_ShouldRedirectToLogin() throws Exception {
        mockMvc.perform(get("/admin/roles/help").with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }
}
