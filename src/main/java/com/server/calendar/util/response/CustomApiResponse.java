package com.server.calendar.util.response;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CustomApiResponse<T>{

    private static final int OK = 200;
    private static final int CREATED = 201;
    private static final int ACCEPTED = 202;
    private static final int BAD_REQUEST = 400;
    private static final int NOT_FOUND = 404;
    private static final int SERVER_ERROR = 500;
    private int status;
    private T data;
    private String message;

    public static <T> CustomApiResponse<T> createSuccess(int status, T data, String message) {
        return new CustomApiResponse<>(status, data, message);
    }

    public static <T> CustomApiResponse<T> createFailWithoutData(int status, String message) {
        return new CustomApiResponse<>(status, null, message);
    }

    public CustomApiResponse(int status, T data, String message) {
        this.status = status;
        this.data = data;
        this.message = message;
    }

}
