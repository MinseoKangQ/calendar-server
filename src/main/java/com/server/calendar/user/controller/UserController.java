package com.server.calendar.user.controller;

import com.server.calendar.user.dto.LoginDto;
import com.server.calendar.user.dto.SignupDto;
import com.server.calendar.user.service.UserService;
import com.server.calendar.util.response.CustomApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@Validated
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // 회원가입
    @PostMapping("/signup")
    public ResponseEntity<CustomApiResponse<?>> createUser(@RequestBody SignupDto dto) {
        return userService.createUser(dto);
    }

    // 이메일 확인
    @GetMapping("/email/{email}")
    public ResponseEntity<CustomApiResponse<?>> checkEmailDuplicated(
            @PathVariable @Email(message = "이메일 형식이 올바르지 않습니다.") @NotEmpty(message = "이메일은 비워둘 수 없습니다.") String email) {
        return userService.checkEmailExists(email);
    }

    // 아이디 확인
    @GetMapping("/userId/{userId}")
    public ResponseEntity<CustomApiResponse<?>> checkUserIdDuplicated(
            @PathVariable @Pattern(regexp = "^[a-z0-9]{7,}$", message = "아이디는 영소문자와 숫자로 구성된 7자 이상이어야 합니다.")
            @NotEmpty(message = "아이디는 비워둘 수 없습니다.") String userId) {
        return userService.checkUserIdExists(userId);
    }

    @PostMapping("/signin")
    public ResponseEntity<CustomApiResponse<?>> login(@RequestBody LoginDto dto) {
        return userService.login(dto);
    }

    @DeleteMapping
    public ResponseEntity<CustomApiResponse<?>> deleteUser(HttpServletRequest request) {
        return userService.deleteUser(request);
    }

}
