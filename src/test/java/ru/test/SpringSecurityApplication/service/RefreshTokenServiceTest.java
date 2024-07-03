package ru.test.SpringSecurityApplication.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import ru.test.SpringSecurityApplication.exception.InvalidTokenException;
import ru.test.SpringSecurityApplication.exception.NoSuchRefreshTokenException;
import ru.test.SpringSecurityApplication.exception.NoSuchUserException;
import ru.test.SpringSecurityApplication.model.Roles;
import ru.test.SpringSecurityApplication.model.entity.Person;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class RefreshTokenServiceTest {

    private TokenService tokenService;

    private UserService userService;

    private RefreshTokenService refreshTokenService;

    private Map<Person, String> tokenCashe;

    @BeforeEach
    public void setup(){
        tokenService = Mockito.mock(TokenService.class);
        userService = Mockito.mock(UserService.class);
        tokenCashe = new ConcurrentHashMap<>();
        refreshTokenService = new RefreshTokenService(tokenService, userService, tokenCashe);
    }

    @Test
    public void generateRefreshTokenTest(){
        String token = "token";
        Person build = Person.builder().id(1).name("test").password("pass").roles(Set.of(Roles.ROLE_USER)).build();

        Mockito.when(tokenService.generateRefreshToken(any(),any(),any())).thenReturn(token);

        String refreshToken = refreshTokenService.generateRefreshToken(build);

        assertEquals(token, refreshToken);
    }

    @Test
    public void refreshTest(){
        String token = "token";
        String token2 = "refreshToken";
        Person build = Person.builder().id(1).name("test").password("pass").roles(Set.of(Roles.ROLE_USER)).build();

        tokenCashe.put(build, token);
        Mockito.when(tokenService.validateJwtToken(token)).thenReturn(true);
        Mockito.when(tokenService.getPersonId(anyString())).thenReturn(1L);
        Mockito.when(userService.getUserById(1L)).thenReturn(Optional.ofNullable(build));
        Mockito.when(tokenService.generateAccessToken(any(),any(),any())).thenReturn(token2);

        String refreshToken = refreshTokenService.refresh(token);

        verify(tokenService,times(1)).validateJwtToken(token);
        verify(tokenService,times(1)).getPersonId(token);
        verify(userService,times(1)).getUserById(1L);
        verify(tokenService,times(1)).generateAccessToken(any(),any(),anyList());
        assertEquals(token2, refreshToken);
    }

    @Test
    public void refreshTestAndThrowInvalidTokenException(){
        String token = "token";

        Mockito.when(tokenService.validateJwtToken(token)).thenReturn(false);

        assertThrows(InvalidTokenException.class,()->refreshTokenService.refresh(token));

    }

    @Test
    public void refreshTestAndThrowNoSuchUserException(){
        String token = "token";
        Person build = Person.builder().id(1).name("test").password("pass").roles(Set.of(Roles.ROLE_USER)).build();

        tokenCashe.put(build, token);
        Mockito.when(tokenService.validateJwtToken(token)).thenReturn(true);
        Mockito.when(tokenService.getPersonId(anyString())).thenReturn(1L);
        Mockito.when(userService.getUserById(1L)).thenReturn(Optional.ofNullable(null));

        assertThrows(NoSuchUserException.class,()->refreshTokenService.refresh(token));

        verify(tokenService,times(1)).validateJwtToken(token);
        verify(tokenService,times(1)).getPersonId(token);
        verify(userService,times(1)).getUserById(1L);

    }

    @Test
    public void refreshTestAndThrowNoSuchRefreshTokenException(){
        String token = "token";
        Person build = Person.builder().id(1).name("test").password("pass").roles(Set.of(Roles.ROLE_USER)).build();

        Mockito.when(tokenService.validateJwtToken(token)).thenReturn(true);
        Mockito.when(tokenService.getPersonId(anyString())).thenReturn(1L);
        Mockito.when(userService.getUserById(1L)).thenReturn(Optional.ofNullable(build));

        assertThrows(NoSuchRefreshTokenException.class,()->refreshTokenService.refresh(token));

        verify(tokenService,times(1)).validateJwtToken(token);
        verify(tokenService,times(1)).getPersonId(token);
        verify(userService,times(1)).getUserById(1L);
    }
}