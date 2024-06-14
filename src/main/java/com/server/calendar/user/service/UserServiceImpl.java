package com.server.calendar.user.service;

import com.server.calendar.doamin.User;
import com.server.calendar.todo.repository.TodoRepository;
import com.server.calendar.user.dto.LoginDto;
import com.server.calendar.user.dto.SignupDto;
import com.server.calendar.user.repository.UserRepository;
import com.server.calendar.util.exception.EntityDuplicatedException;
import com.server.calendar.util.exception.EntityNotFoundException;
import com.server.calendar.util.exception.PasswordIncorrectException;
import com.server.calendar.util.exception.TokenInvalidException;
import com.server.calendar.util.jwt.JwtTokenProvider;
import com.server.calendar.util.response.CustomApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final TodoRepository todoRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public ResponseEntity<CustomApiResponse<?>> createUser(SignupDto dto) {

        // 한 번 더 검증
        checkEmailExists(dto.getEmail());
        checkUserIdExists(dto.getUserId());

        // 비밀번호 암호화
        String encodedPw = passwordEncoder.encode(dto.getPassword());

        // 멤버 생성
        User user = SignupDto.builder()
                .email(dto.getEmail())
                .userId(dto.getUserId())
                .password(encodedPw)
                .build().toEntity();

        // 저장
        userRepository.save(user);

        // 응답
        CustomApiResponse<Object> resultBody = CustomApiResponse.createSuccess(HttpStatus.CREATED.value(), null, "회원가입에 성공하였습니다.");
        return ResponseEntity.status(HttpStatus.CREATED).body(resultBody);
    }

    @Override
    public ResponseEntity<CustomApiResponse<?>> checkEmailExists(String email) {
        Optional<User> byEmail = userRepository.findByEmail(email);

        if (byEmail.isPresent()) {
            throw new EntityDuplicatedException("이미 사용중인 이메일입니다.");
        }

        CustomApiResponse<?> response = CustomApiResponse.createSuccess(200, null, "사용 가능한 이메일입니다.");
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<CustomApiResponse<?>> checkUserIdExists(String userId) {

        Optional<User> byUserId = userRepository.findByUserId(userId);

        if (byUserId.isPresent()) {
            throw new EntityDuplicatedException("이미 사용중인 아이디입니다.");
        }

        CustomApiResponse<?> response = CustomApiResponse.createSuccess(200, null, "사용 가능한 아이디입니다.");
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<CustomApiResponse<?>> login(LoginDto dto) {

        // 아이디가 존재하는지 확인 -> 존재하지 않으면 error
        User user = userRepository.findByUserId(dto.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 회원입니다."));

        if(!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new PasswordIncorrectException("비밀번호가 일치하지 않습니다.");
        }

        // 로그인에 성공했으므로 토큰 생성
        String token = jwtTokenProvider.createToken(user.getUserId());

        // 응답에 토큰을 포함하여 반환
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        CustomApiResponse<?> response = CustomApiResponse.createSuccess(201, null, "로그인 성공");
        return new ResponseEntity<>(response, headers, 201);
    }

    @Transactional
    @Override
    public ResponseEntity<CustomApiResponse<?>> deleteUser(HttpServletRequest request) {
        // 헤더에서 토큰 받아오기
        String token = extractToken(request);

        if (token == null || !jwtTokenProvider.validateToken(token)) {
            throw new TokenInvalidException("토큰이 유효하지 않습니다.");
        }

        // 토큰에서 userId 추출
        String userId = jwtTokenProvider.getClaimsFromToken(token).getSubject();

        // userId를 사용하여 User 엔티티 조회
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("토큰으로 사용자를 찾을 수 없습니다."));

        // 사용자의 TodoList 삭제
        todoRepository.deleteByUser(user);

        // 사용자 삭제
        userRepository.delete(user);

        CustomApiResponse<?> response = CustomApiResponse.createSuccess(200, null, "회원 탈퇴가 완료되었습니다.");
        return ResponseEntity.ok(response);
    }

    private String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

}
