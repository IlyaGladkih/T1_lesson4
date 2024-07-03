package ru.test.SpringSecurityApplication.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import ru.test.SpringSecurityApplication.advice.MyControllerAdvice;
import ru.test.SpringSecurityApplication.config.SecurityConfig;
import ru.test.SpringSecurityApplication.controllers.PublicUserController;
import ru.test.SpringSecurityApplication.controllers.UserController;
import ru.test.SpringSecurityApplication.exception.UserAlreadyExistException;
import ru.test.SpringSecurityApplication.model.Roles;
import ru.test.SpringSecurityApplication.model.dto.UserDto;
import ru.test.SpringSecurityApplication.model.entity.Person;
import ru.test.SpringSecurityApplication.security.JwtTokenFilter;
import ru.test.SpringSecurityApplication.service.TokenService;
import ru.test.SpringSecurityApplication.service.UserService;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class PublicUserControllerTest {

    public MockMvc mockMvc;

    public UserService service;

    @BeforeEach
    public void setup(){
        service = Mockito.mock(UserService.class);
        mockMvc = MockMvcBuilders.standaloneSetup(new PublicUserController(service))
                .setControllerAdvice(MyControllerAdvice.class)
                .build();
    }

    @Test
    public void testCreateUserAndExpectReturn201() throws Exception {
        UserDto userDto = new UserDto("user", "password", "email", List.of("ROLE_USER"));
        ObjectMapper mapper = new ObjectMapper();
        String body = mapper.writeValueAsString(userDto);

        mockMvc.perform(post("/api/v1/public/user")
                .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isOk());

        Mockito.verify(service, times(1)).createUser(Mockito.any(Person.class));
    }

    @Test
    public void testCreateUserAndExpectReturnException() throws Exception {
        UserDto userDto = new UserDto("user", "password", "email", List.of("ROLE_USER"));
        ObjectMapper mapper = new ObjectMapper();
        String body = mapper.writeValueAsString(userDto);

        Mockito.when(service.createUser(Mockito.any(Person.class))).thenThrow(new UserAlreadyExistException("Already exists"));

        mockMvc.perform(post("/api/v1/public/user")
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest());

        Mockito.verify(service, times(1)).createUser(Mockito.any(Person.class));
    }
}