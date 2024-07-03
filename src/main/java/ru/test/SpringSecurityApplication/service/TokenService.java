package ru.test.SpringSecurityApplication.service;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import ru.test.SpringSecurityApplication.model.AppUserPrincipal;
import ru.test.SpringSecurityApplication.model.entity.Person;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class TokenService {

    private static final String ROLE_CLAIM = "roles";

    private static final String ID_CLAIM = "id";

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.tokenExpiration}")
    private Duration tokenExpiration;

    @Value("${jwt.refreshTokenExpiration}")
    private Duration refreshTokenExpiration;

    public String generateRefreshToken(String username, String id, List<String> roles){
        return generateToken(username,id,roles,refreshTokenExpiration);
    }

    public String generateAccessToken(String username, String id, List<String> roles){
        return generateToken(username,id,roles,tokenExpiration);
    }

    private String generateToken(String username, String id, List<String> roles, Duration expiration) {
        SecretKey secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        return Jwts.builder().subject(username).issuedAt(new Date())
                .expiration(new Date((new Date().getTime() + expiration.toMillis())))
                .claim(ROLE_CLAIM, roles)
                .claim(ID_CLAIM, id)
                .signWith(secretKey, Jwts.SIG.HS512)
                .compact();
    }

    public Authentication toAuthentication(String token){
        SecretKey secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        Jws<Claims> claimsJwe = Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token);
        Claims payload = claimsJwe.getPayload();
        List<String> roles = payload.get(ROLE_CLAIM, List.class);
        AppUserPrincipal principal = AppUserPrincipal.builder()
                .id(payload.get(ID_CLAIM, String.class))
                .name(payload.getSubject())
                .role(roles)
                .build();

        return new UsernamePasswordAuthenticationToken(
                    principal,
                    null,
                    roles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList())
        );

    }

    public Long getPersonId(String token){
        SecretKey secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        Jws<Claims> claimsJwe = Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token);
        Claims payload = claimsJwe.getPayload();
        return Long.parseLong(payload.get(ID_CLAIM, String.class));
    }

    public boolean validateJwtToken(String token) {
        SecretKey secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        try {
            Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token);
            return true;
        } catch (MalformedJwtException e) {
            log.error("Invalid token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.error("token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("claims string is empty: {}", e.getMessage());
        }

        return false;
    }


}
