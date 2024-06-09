package com.server.calendar.util.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import com.server.calendar.util.response.CustomApiResponse;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public ResponseEntity<CustomApiResponse<Object>> handleError(HttpServletRequest request) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        if (status != null) {
            int statusCode = Integer.parseInt(status.toString());

            // Not Found (404)
            if (statusCode == HttpStatus.NOT_FOUND.value()) {
                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(new CustomApiResponse<>(HttpStatus.NOT_FOUND.value(), null, "요청 경로를 찾을 수 없습니다."));
            }
            // Bad Request (400)
            else if (statusCode == HttpStatus.BAD_REQUEST.value()) {
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(new CustomApiResponse<>(HttpStatus.BAD_REQUEST.value(), null, "잘못된 요청입니다."));
            }
            // Forbidden (403)
            else if (statusCode == HttpStatus.FORBIDDEN.value()) {
                return ResponseEntity
                        .status(HttpStatus.FORBIDDEN)
                        .body(new CustomApiResponse<>(HttpStatus.FORBIDDEN.value(), null, "접근이 금지되었습니다."));
            }
            // Method Not Allowed (405)
            else if (statusCode == HttpStatus.METHOD_NOT_ALLOWED.value()) {
                return ResponseEntity
                        .status(HttpStatus.METHOD_NOT_ALLOWED)
                        .body(new CustomApiResponse<>(HttpStatus.METHOD_NOT_ALLOWED.value(), null, "허용되지 않는 메소드입니다."));
            }
            // Internal Server Error (500)
            // 기본적으로 내부 서버 오류 처리
            else {
                return ResponseEntity
                        .status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(new CustomApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), null, "내부 서버 오류가 발생했습니다."));
            }
        }

        // 기타 상황에서도 내부 서버 오류로 처리
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new CustomApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), null, "내부 서버 오류가 발생했습니다."));
    }
}

