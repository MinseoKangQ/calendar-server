package com.server.calendar.util.exception;

public class PasswordNotChangedException extends RuntimeException{
    public PasswordNotChangedException(String message) {
        super(message);
    }
}
