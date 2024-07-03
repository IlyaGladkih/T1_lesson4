package ru.test.SpringSecurityApplication.advice;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.test.SpringSecurityApplication.exception.*;
import ru.test.SpringSecurityApplication.model.dto.ExceptionDto;

@org.springframework.web.bind.annotation.ControllerAdvice
@RequiredArgsConstructor
public class MyControllerAdvice {


    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ExceptionDto> handleDenied(AccessDeniedException e){
        return ResponseEntity.status(403).body(ExceptionDto.builder().errorMessage(e.getMessage()).build());
    }

    @ExceptionHandler(UserAlreadyExistException.class)
    public ResponseEntity<ExceptionDto> handleAlreadyExists(UserAlreadyExistException e){
        return ResponseEntity.badRequest().body(ExceptionDto.builder().errorMessage(e.getMessage()).build());
    }

    @ExceptionHandler(AuthException.class)
    public ResponseEntity<ExceptionDto> handleAuthException(AuthException e){
        return ResponseEntity.badRequest().body(ExceptionDto.builder().errorMessage(e.getMessage()).build());
    }

    @ExceptionHandler(NoSuchUserException.class)
    public ResponseEntity<ExceptionDto> handleNoSuchUserException(NoSuchUserException e){
        return ResponseEntity.badRequest().body(ExceptionDto.builder().errorMessage(e.getMessage()).build());
    }

    @ExceptionHandler(NoSuchRefreshTokenException.class)
    public ResponseEntity<ExceptionDto> handleNoSuchRefreshTokenException(NoSuchRefreshTokenException e){
        return ResponseEntity.badRequest().body(ExceptionDto.builder().errorMessage(e.getMessage()).build());
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<ExceptionDto> handleInvalidTokenException(InvalidTokenException e){
        return ResponseEntity.badRequest().body(ExceptionDto.builder().errorMessage(e.getMessage()).build());
    }
}
