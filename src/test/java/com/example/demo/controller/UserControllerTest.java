package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import com.example.demo.config.TestWebConfig;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = UserController.class)
@Import(TestWebConfig.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testListUsers() throws Exception {
        User user1 = new User();
        user1.setId(1L);
        user1.setUsername("user1");
        user1.setEmail("user1@example.com");
        user1.setRole("ROLE_PUBLISHER");
        user1.setActive(true);

        User user2 = new User();
        user2.setId(2L);
        user2.setUsername("user2");
        user2.setEmail("user2@example.com");
        user2.setRole("ROLE_REDACTOR");
        user2.setActive(true);

        when(userService.getAllUsers()).thenReturn(Arrays.asList(user1, user2));

        mockMvc.perform(get("/admin/users"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/users/list"))
                .andExpect(model().attributeExists("users"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testCreateUserForm() throws Exception {
        mockMvc.perform(get("/admin/users/create"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/users/create"))
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attributeExists("roles"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testCreateUser() throws Exception {
        User newUser = new User();
        newUser.setUsername("newuser");
        newUser.setPassword("password");
        newUser.setEmail("newuser@example.com");
        newUser.setRole("ROLE_PUBLISHER");
        newUser.setActive(true);

        when(userService.createUser(any(User.class))).thenReturn(newUser);

        mockMvc.perform(post("/admin/users/create")
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .param("username", "newuser")
                .param("password", "password")
                .param("email", "newuser@example.com")
                .param("role", "ROLE_PUBLISHER")
                .param("active", "true"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/users"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testEditUserForm() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setUsername("user1");
        user.setEmail("user1@example.com");
        user.setRole("ROLE_PUBLISHER");
        user.setActive(true);

        when(userService.getUserById(1L)).thenReturn(Optional.of(user));

        mockMvc.perform(get("/admin/users/edit/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/users/edit"))
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attributeExists("roles"));
    }
}
