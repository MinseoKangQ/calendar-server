package com.server.calendar.util.exception;

public class EntityDuplicatedException extends RuntimeException{
    public EntityDuplicatedException(String message) {
        super(message);
    }
}
