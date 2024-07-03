package ru.test.SpringSecurityApplication.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.hibernate.mapping.Collection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.test.util.ReflectionTestUtils;
import ru.test.SpringSecurityApplication.model.AppUserPrincipal;
import ru.test.SpringSecurityApplication.model.Roles;
import ru.test.SpringSecurityApplication.model.entity.Person;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class TokenServiceTest {

    private String secret = "ksjflghskjdfvkjcbmbxvapojvisdfvhjdfvmbxfvnbdsfnvldsfhkvlsfdnvmsmdbfvjkbdfvber";

    private Duration tokenExpiration = Duration.ofMillis(10000);


    private Duration refreshTokenExpiration = Duration.ofMillis(10000000);

    private TokenService tokenService;

    private SecretKey secretKey;

    @BeforeEach
    public void setup(){
        tokenService = new TokenService();
        ReflectionTestUtils.setField(tokenService,"secret", secret);
        ReflectionTestUtils.setField(tokenService,"tokenExpiration", tokenExpiration);
        ReflectionTestUtils.setField(tokenService,"refreshTokenExpiration", refreshTokenExpiration);
        secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    @Test
    void validateJwtTokenTrue() {
        Person person = Person.builder().id(1).name("test").roles(Set.of(Roles.ROLE_USER)).build();

        String token = Jwts.builder().subject(person.getName()).issuedAt(new Date())
                .expiration(new Date((new Date().getTime() + tokenExpiration.toMillis())))
                .claim("roles", person.getRoles().stream().map(Enum::name).collect(Collectors.toList()))
                .claim("id", person.getId())
                .signWith(secretKey, Jwts.SIG.HS512)
                .compact();

        boolean result = tokenService.validateJwtToken(token);

        assertTrue(result);
    }

    @Test
    void validateJwtTokenFalse() {
        Person person = Person.builder().id(1).name("test").roles(Set.of(Roles.ROLE_USER)).build();

        String token = Jwts.builder().subject(person.getName()).issuedAt(new Date())
                .expiration(new Date((new Date().getTime() + tokenExpiration.toMillis())))
                .claim("roles", person.getRoles().stream().map(Enum::name).collect(Collectors.toList()))
                .claim("id", person.getId())
                .signWith(secretKey, Jwts.SIG.HS512)
                .compact();

        boolean result = tokenService.validateJwtToken(token.substring(token.length()/2));

        assertFalse(result);
    }

    @Test
    void generateRefreshTokenTest() {
        Person person = Person.builder().id(1).name("test").roles(Set.of(Roles.ROLE_USER)).build();

        String refreshToken = tokenService.generateRefreshToken(person.getName(),
                String.valueOf(person.getId()),
                person.getRoles().stream().map(Enum::name).collect(Collectors.toList()));


        Jws<Claims> claimsJwe = Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(refreshToken);
        Claims payload = claimsJwe.getPayload();
        List<String> roles = payload.get("roles", List.class);
        Person result = Person.builder()
                .id(Long.parseLong(payload.get("id", String.class)))
                .name(payload.getSubject())
                .roles(roles.stream().map(Roles::valueOf).collect(Collectors.toSet()))
                .build();

        assertEquals(person, result);
    }

    @Test
    void generateAccessTokenTest() {
        Person person = Person.builder().id(1).name("test").roles(Set.of(Roles.ROLE_USER)).build();

        String refreshToken = tokenService.generateAccessToken(person.getName(),
                String.valueOf(person.getId()),
                person.getRoles().stream().map(Enum::name).collect(Collectors.toList()));


        Jws<Claims> claimsJwe = Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(refreshToken);
        Claims payload = claimsJwe.getPayload();
        List<String> roles = payload.get("roles", List.class);
        Person result = Person.builder()
                .id(Long.parseLong(payload.get("id", String.class)))
                .name(payload.getSubject())
                .roles(roles.stream().map(Roles::valueOf).collect(Collectors.toSet()))
                .build();

        assertEquals(person, result);
    }

    @Test
    void getPersonIdTest() {
        Person person = Person.builder().id(1L).name("test").roles(Set.of(Roles.ROLE_USER)).build();

        String token = Jwts.builder().subject(person.getName()).issuedAt(new Date())
                .expiration(new Date((new Date().getTime() + tokenExpiration.toMillis())))
                .claim("roles", person.getRoles().stream().map(Enum::name).collect(Collectors.toList()))
                .claim("id", String.valueOf(person.getId()))
                .signWith(secretKey, Jwts.SIG.HS512)
                .compact();

        Long personId = tokenService.getPersonId(token);

        assertEquals(person.getId(),personId);
    }

    @Test
    void toAuthenticationTest() {
        Person person = Person.builder().id(1L).name("test").roles(Set.of(Roles.ROLE_USER)).build();

        String token = Jwts.builder().subject(person.getName()).issuedAt(new Date())
                .expiration(new Date((new Date().getTime() + tokenExpiration.toMillis())))
                .claim("roles", person.getRoles().stream().map(Enum::name).collect(Collectors.toList()))
                .claim("id", String.valueOf(person.getId()))
                .signWith(secretKey, Jwts.SIG.HS512)
                .compact();

        Authentication authentication = tokenService.toAuthentication(token);
        AppUserPrincipal principal = (AppUserPrincipal) authentication.getPrincipal();

        assertEquals(String.valueOf(person.getId()), principal.getId());
        assertEquals(person.getName(), principal.getName());
        Set<Roles> collect = principal.getRole().stream().map(Roles::valueOf).collect(Collectors.toSet());
        assertEquals(person.getRoles(), collect);
    }


}