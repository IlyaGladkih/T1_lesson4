package ru.test.SpringSecurityApplication.exception;

import java.util.function.Supplier;

public class NoSuchUserException extends RuntimeException{
    public NoSuchUserException(String message) {
        super(message);
    }
}
