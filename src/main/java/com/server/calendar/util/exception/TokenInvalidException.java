package com.server.calendar.util.exception;

public class TokenInvalidException extends RuntimeException{
    public TokenInvalidException(String message) {
        super("토큰이 유효하지 않습니다.");
    }
}
