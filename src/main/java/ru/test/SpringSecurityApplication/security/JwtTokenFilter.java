package ru.test.SpringSecurityApplication.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.test.SpringSecurityApplication.model.entity.Person;
import ru.test.SpringSecurityApplication.service.RefreshTokenService;
import ru.test.SpringSecurityApplication.service.TokenService;
import ru.test.SpringSecurityApplication.service.UserService;

import java.io.IOException;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {

    private final TokenService tokenService;

    private final UserService userService;

    private static final String BEARER_PREFIX = "Bearer ";

    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authorization = request.getHeader("AUTHORIZATION");
        if (authorization != null) {
            if (authorization.startsWith(BEARER_PREFIX)){
                String token = authorization.substring(BEARER_PREFIX.length());

                handleAuthentication(token);

            }
        }
        filterChain.doFilter(request,response);
    }


    private void handleAuthentication(String token) {
        if(tokenService.validateJwtToken(token)){
            Authentication authentication = tokenService.toAuthentication(token);
            Optional<Person> userByName = userService.getUserByName(authentication.getName());
            if(userByName.isPresent()){
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
    }

}
