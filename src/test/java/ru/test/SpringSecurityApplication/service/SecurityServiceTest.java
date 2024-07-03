package ru.test.SpringSecurityApplication.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.test.SpringSecurityApplication.exception.AuthException;
import ru.test.SpringSecurityApplication.exception.NoSuchUserException;
import ru.test.SpringSecurityApplication.model.Roles;
import ru.test.SpringSecurityApplication.model.dto.TokenResponseDto;
import ru.test.SpringSecurityApplication.model.entity.Person;
import ru.test.SpringSecurityApplication.repository.PersonRepository;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class SecurityServiceTest {

    private PasswordEncoder passwordEncoder;
    private TokenService tokenService;
    private RefreshTokenService refreshTokenService;
    private UserService userService;

    private SecurityService securityService;

    @BeforeEach
    public void setup(){
        tokenService = Mockito.mock(TokenService.class);
        refreshTokenService = Mockito.mock(RefreshTokenService.class);
        userService = Mockito.mock(UserService.class);
        passwordEncoder = new BCryptPasswordEncoder();
        securityService = new SecurityService(passwordEncoder, tokenService, refreshTokenService, userService);
    }

    @Test
    void generate() {
        Person person = Person.builder().name("test").password(passwordEncoder.encode("pass"))
                .roles(Set.of(Roles.ROLE_USER)).build();

        Mockito.when(userService.getUserByName(Mockito.any())).thenReturn(Optional.ofNullable(person));
        Mockito.when(tokenService.generateAccessToken(Mockito.anyString(),Mockito.anyString(),Mockito.anyList()))
                .thenReturn("token1");
        Mockito.when(refreshTokenService.generateRefreshToken(person))
                .thenReturn("token2");

        TokenResponseDto generate = securityService.generate("user", "pass");

        assertEquals("token1",generate.getToken());
        assertEquals("token2",generate.getRefreshToken());
    }

    @Test
    void generateAndThrowNoSuchUserException() {
        Person person = Person.builder().name("test").password(passwordEncoder.encode("pass"))
                .roles(Set.of(Roles.ROLE_USER)).build();

        Mockito.when(userService.getUserByName(Mockito.any())).thenReturn(Optional.ofNullable(null));

        assertThrows(NoSuchUserException.class,()->securityService.generate("user", "pass"));
    }

    @Test
    void generateAndThrowAuthException() {
        Person person = Person.builder().name("test").password(passwordEncoder.encode("pass"))
                .roles(Set.of(Roles.ROLE_USER)).build();

        Mockito.when(userService.getUserByName(Mockito.any())).thenReturn(Optional.ofNullable(person));

        assertThrows(AuthException.class,()->securityService.generate("user", "password"));
    }



    @Test
    void refresh() {
        String token = "token";
        Mockito.when(refreshTokenService.refresh(token)).thenReturn("token2");

        TokenResponseDto refresh = securityService.refresh(token);

        assertEquals(token,refresh.getRefreshToken());
        assertEquals("token2",refresh.getToken());
    }
}