package com.server.calendar.user.dto;

import com.server.calendar.doamin.User;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Getter
@Setter
@Builder
@Data
public class SignupDto {

    // 이메일 정규식
    @Email(message = "이메일 형식이 올바르지 않습니다.")
    @NotEmpty(message = "이메일은 비워둘 수 없습니다.")
    private String email;

    // 영소문자 + 숫자 조합 7자 이상
    @Pattern(regexp = "^[a-z0-9]{7,}$", message = "아이디는 영소문자와 숫자로 구성된 7자 이상이어야 합니다.")
    @NotEmpty(message = "아이디는 비워둘 수 없습니다.")
    private String userId;

    // 영대문자 + 특수문자 + 숫자 + 영소문자 조합
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$", message = "비밀번호는 영대문자, 특수문자, 숫자, 영소문자를 포함한 8자 이상이어야 합니다.")
    @NotEmpty(message = "비밀번호는 비워둘 수 없습니다.")
    private String password;

    public User toEntity() {
        return User.builder()
                .email(this.email)
                .userId(this.userId)
                .password(this.password)
                .build();
    }
}
