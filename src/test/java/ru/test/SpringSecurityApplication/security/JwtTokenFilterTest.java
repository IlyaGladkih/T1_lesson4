package ru.test.SpringSecurityApplication.security;

import jakarta.servlet.ServletException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import ru.test.SpringSecurityApplication.model.AppUserPrincipal;
import ru.test.SpringSecurityApplication.model.entity.Person;
import ru.test.SpringSecurityApplication.service.RefreshTokenService;
import ru.test.SpringSecurityApplication.service.SecurityService;
import ru.test.SpringSecurityApplication.service.TokenService;
import ru.test.SpringSecurityApplication.service.UserService;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class JwtTokenFilterTest {

    private TokenService tokenService;

    private UserService userService;

    private JwtTokenFilter filter;

    private static final String BEARER_PREFIX = "Bearer ";

    @BeforeEach
    public void setup(){
        tokenService = mock(TokenService.class);
        userService = mock(UserService.class);
        filter = new JwtTokenFilter(tokenService, userService);
    }

    @Test
    public void doFilterInternalTest() throws ServletException, IOException {
        String token = "token";
        String username = "test";
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("AUTHORIZATION", BEARER_PREFIX + token);
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain filterChain = new MockFilterChain();

        when(tokenService.validateJwtToken(anyString())).thenReturn(true);
        when(tokenService.toAuthentication(anyString()))
                .thenReturn(new UsernamePasswordAuthenticationToken(AppUserPrincipal.builder().name(username).build(), null,null));
        when(userService.getUserByName(anyString()))
                .thenReturn(Optional.ofNullable(Person.builder().id(1).build()));


        filter.doFilterInternal(request,response,filterChain);


        verify(tokenService, times(1)).validateJwtToken(token);
        verify(tokenService, times(1)).toAuthentication(token);
        verify(userService, times(1)).getUserByName(username);
    }

}