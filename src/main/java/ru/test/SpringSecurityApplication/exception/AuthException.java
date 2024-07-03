package ru.test.SpringSecurityApplication.exception;

public class AuthException extends RuntimeException{

    public AuthException(String message) {
        super(message);
    }
}
