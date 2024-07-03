package ru.test.SpringSecurityApplication.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.test.SpringSecurityApplication.exception.AuthException;
import ru.test.SpringSecurityApplication.exception.NoSuchUserException;
import ru.test.SpringSecurityApplication.model.dto.TokenResponseDto;
import ru.test.SpringSecurityApplication.model.entity.Person;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SecurityService {

    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;
    private final RefreshTokenService refreshTokenService;
    private final UserService userService;

    public TokenResponseDto generate(String name, String pass){
        return checkUser(name, pass);
    }

    private TokenResponseDto checkUser(String name, String password){
        Person person = userService.getUserByName(name).orElseThrow(() -> new NoSuchUserException("Нет пользователя с именем " + name));

        if(!passwordEncoder.matches(password, person.getPassword())) throw new AuthException("Неверный пароль");
        else return getTokenResponseDto(name, person.getId(), person);
    }

    private TokenResponseDto getTokenResponseDto(String name, Long id, Person person) {
        return TokenResponseDto.builder()
                .token(
                        tokenService.generateAccessToken(name,
                        String.valueOf(id),
                        person.getRoles().stream().map(Enum::name).collect(Collectors.toList())))
                .refreshToken(
                        refreshTokenService.generateRefreshToken(person))
                .build();
    }

    public TokenResponseDto refresh(String token){
        return TokenResponseDto.builder()
                .token(refreshTokenService.refresh(token))
                .refreshToken(token)
                .build();
    }


}
