package ru.test.SpringSecurityApplication.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.test.SpringSecurityApplication.model.Roles;
import ru.test.SpringSecurityApplication.model.dto.UserDto;
import ru.test.SpringSecurityApplication.model.entity.Person;
import ru.test.SpringSecurityApplication.service.UserService;

import java.util.stream.Collectors;

@RestController
@RequestMapping("api/v1/public")
@RequiredArgsConstructor
public class PublicUserController {

    private final UserService service;

    @PostMapping("/user")
    public ResponseEntity<?> createUser(@RequestBody UserDto user){
        service.createUser(Person.builder()
                .name(user.getName())
                .password(user.getPassword())
                .email(user.getEmail())
                .roles(user.getRoles().stream().map(Roles::valueOf).collect(Collectors.toSet()))
                .build());
        return ResponseEntity.ok().build();
    }

}
