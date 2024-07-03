package ru.test.SpringSecurityApplication.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.test.SpringSecurityApplication.advice.MyControllerAdvice;
import ru.test.SpringSecurityApplication.controllers.TokenController;
import ru.test.SpringSecurityApplication.exception.AuthException;
import ru.test.SpringSecurityApplication.exception.InvalidTokenException;
import ru.test.SpringSecurityApplication.exception.NoSuchRefreshTokenException;
import ru.test.SpringSecurityApplication.exception.NoSuchUserException;
import ru.test.SpringSecurityApplication.model.dto.RefreshTokenDto;
import ru.test.SpringSecurityApplication.model.dto.TokenRequestDto;
import ru.test.SpringSecurityApplication.model.dto.TokenResponseDto;
import ru.test.SpringSecurityApplication.service.SecurityService;
import ru.test.SpringSecurityApplication.service.UserService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class TokenControllerTest {

    public MockMvc mockMvc;

    public SecurityService service;

    @BeforeEach
    public void setup(){
        service = Mockito.mock(SecurityService.class);
        mockMvc = MockMvcBuilders.standaloneSetup(new TokenController(service))
                .setControllerAdvice(MyControllerAdvice.class)
                .build();
    }

    @Test
    void generateAndReturnTokens() throws Exception {
        TokenRequestDto request = TokenRequestDto.builder().name("user").password("pass").build();
        ObjectMapper mapper = new ObjectMapper();
        String body = mapper.writeValueAsString(request);
        TokenResponseDto tokenResponseDto = TokenResponseDto.builder().token("token1").refreshToken("token2").build();
        String response = mapper.writeValueAsString(tokenResponseDto);
        Mockito.when(service.generate(Mockito.anyString(),Mockito.anyString())).thenReturn(tokenResponseDto);

        String contentAsString = mockMvc.perform(post("/api/v1/public/token/generate")
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(service, times(1)).generate("user","pass");
        Assertions.assertEquals(response,contentAsString);
    }

    @Test
    void generateAndThrowAuthException() throws Exception {
        TokenRequestDto request = TokenRequestDto.builder().name("user").password("pass").build();
        ObjectMapper mapper = new ObjectMapper();
        String body = mapper.writeValueAsString(request);

        Mockito.when(service.generate(Mockito.anyString(),Mockito.anyString()))
                .thenThrow(new AuthException("Invalid password"));

        mockMvc.perform(post("/api/v1/public/token/generate")
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest());

        verify(service, times(1)).generate("user","pass");
    }

    @Test
    void generateAndThrowNoSuchUserException() throws Exception {
        TokenRequestDto request = TokenRequestDto.builder().name("user").password("pass").build();
        ObjectMapper mapper = new ObjectMapper();
        String body = mapper.writeValueAsString(request);

        Mockito.when(service.generate(Mockito.anyString(),Mockito.anyString()))
                .thenThrow(new NoSuchUserException("No user with name user"));

        mockMvc.perform(post("/api/v1/public/token/generate")
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest());

        verify(service, times(1)).generate("user","pass");
    }

    @Test
    void refreshAndReturnAccessTokens() throws Exception {
        RefreshTokenDto token = RefreshTokenDto.builder().refreshToken("token").build();
        ObjectMapper mapper = new ObjectMapper();
        String body = mapper.writeValueAsString(token);
        TokenResponseDto tokenResponseDto = TokenResponseDto.builder()
                .token("AccessToken").refreshToken("token").build();
        String response = mapper.writeValueAsString(tokenResponseDto);

        Mockito.when(service.refresh(Mockito.anyString()))
                .thenReturn(tokenResponseDto);


        String contentAsString = mockMvc.perform(post("/api/v1/public/token/refresh")
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();


        verify(service, times(1)).refresh("token");
        assertEquals(response,contentAsString);
    }

    @Test
    void refreshAndThrowNoSuchRefreshTokenException() throws Exception {
        RefreshTokenDto token = RefreshTokenDto.builder().refreshToken("token").build();
        ObjectMapper mapper = new ObjectMapper();
        String body = mapper.writeValueAsString(token);


        Mockito.when(service.refresh(Mockito.anyString()))
                .thenThrow(new NoSuchRefreshTokenException("No Such refresh token"));


        mockMvc.perform(post("/api/v1/public/token/refresh")
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest());


        verify(service, times(1)).refresh("token");

    }

    @Test
    void refreshAndThrownInvalidTokenException() throws Exception {
        RefreshTokenDto token = RefreshTokenDto.builder().refreshToken("token").build();
        ObjectMapper mapper = new ObjectMapper();
        String body = mapper.writeValueAsString(token);


        Mockito.when(service.refresh(Mockito.anyString()))
                .thenThrow(new InvalidTokenException("Токен не прошел валидацию"));


        mockMvc.perform(post("/api/v1/public/token/refresh")
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest());


        verify(service, times(1)).refresh("token");

    }
}