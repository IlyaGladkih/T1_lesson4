package ru.test.SpringSecurityApplication.service;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import ru.test.SpringSecurityApplication.exception.InvalidTokenException;
import ru.test.SpringSecurityApplication.exception.NoSuchRefreshTokenException;
import ru.test.SpringSecurityApplication.exception.NoSuchUserException;
import ru.test.SpringSecurityApplication.model.entity.Person;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final TokenService tokenService;

    private final UserService userService;

    private  final Map<Person, String> tokenCache;

    public String generateRefreshToken(Person person){
        String refreshToken = tokenService.generateRefreshToken(person.getName(),
                String.valueOf(person.getId()),
                person.getRoles().stream().map(Enum::name).collect(Collectors.toList()));
        tokenCache.put(person, refreshToken);
        return refreshToken;
    }

    public String refresh(String token){
        if(tokenService.validateJwtToken(token)){

            Long id = tokenService.getPersonId(token);
            Optional<Person> userById = userService.getUserById(id);
            Person user = userById.orElseThrow(() -> new NoSuchUserException("Такого пользователя не существует"));

            if(token.equals(tokenCache.get(user))){
                return tokenService.generateAccessToken(user.getName(),
                        String.valueOf(user.getId()),
                        user.getRoles().stream().map(Enum::name).collect(Collectors.toList()));
            }else throw new NoSuchRefreshTokenException("Несуществующий токен");
        }
        throw new InvalidTokenException("Токен не прошел валидацию");
    }


}
