package ru.test.SpringSecurityApplication.exception;

public class NoSuchRefreshTokenException extends RuntimeException{
    public NoSuchRefreshTokenException(String message) {
        super(message);
    }
}
