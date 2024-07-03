package ru.test.SpringSecurityApplication.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import ru.test.SpringSecurityApplication.advice.MyControllerAdvice;
import ru.test.SpringSecurityApplication.controllers.UserController;
import ru.test.SpringSecurityApplication.security.JwtTokenFilter;
import ru.test.SpringSecurityApplication.service.TokenService;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureDataJpa
@ExtendWith(SpringExtension.class)
@EnableMethodSecurity
@Import(MyControllerAdvice.class)
@WebMvcTest(controllers = UserController.class)
public class UserControllerTest {
    @Autowired
    public WebApplicationContext webApplicationContext;

    @Autowired
    public MockMvc mockMvc;

    @MockBean
    public JwtTokenFilter filter;

    @MockBean
    public TokenService tokenService;

    @MockBean
    public AuthenticationManager manager;

    @BeforeEach
    public void setup(){
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testMethodWithAdminRole() throws Exception {
        mockMvc.perform(get("/user"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "USER")
    public void testMethodWithUserRole() throws Exception {
        mockMvc.perform(get("/user"))
                .andExpect(status().isOk());

    }

    @Test
    @WithAnonymousUser
    public void testMethodWithAnonymousUser() throws Exception {
        mockMvc.perform(get("/user"))
                .andExpect(status().isUnauthorized());

    }

}
