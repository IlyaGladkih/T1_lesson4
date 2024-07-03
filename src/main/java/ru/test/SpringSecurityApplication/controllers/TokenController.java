package ru.test.SpringSecurityApplication.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.test.SpringSecurityApplication.model.dto.RefreshTokenDto;
import ru.test.SpringSecurityApplication.model.dto.TokenRequestDto;
import ru.test.SpringSecurityApplication.model.dto.TokenResponseDto;
import ru.test.SpringSecurityApplication.service.SecurityService;

@RestController
@RequestMapping("/api/v1/public/token")
@RequiredArgsConstructor
public class TokenController {

    private final SecurityService securityService;

    @PostMapping("/generate")
    public ResponseEntity<TokenResponseDto> generate(@RequestBody TokenRequestDto request){
        TokenResponseDto tokenDto = securityService.generate(
                request.getName(),
                request.getPassword()
        );
        return ResponseEntity.ok(tokenDto);
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenResponseDto> refresh(@RequestBody RefreshTokenDto request){
        return ResponseEntity.ok(securityService.refresh(request.getRefreshToken()));
    }
}
