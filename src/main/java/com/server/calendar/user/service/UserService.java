package com.server.calendar.user.service;

import com.server.calendar.user.dto.LoginDto;
import com.server.calendar.user.dto.SignupDto;
import com.server.calendar.util.response.CustomApiResponse;
import org.springframework.http.ResponseEntity;

public interface UserService {

    ResponseEntity<CustomApiResponse<?>> createUser(SignupDto dto);
    ResponseEntity<CustomApiResponse<?>> checkEmailExists(String email);
    ResponseEntity<CustomApiResponse<?>> checkUserIdExists(String userId);
    ResponseEntity<CustomApiResponse<?>> login(LoginDto dto);
}
