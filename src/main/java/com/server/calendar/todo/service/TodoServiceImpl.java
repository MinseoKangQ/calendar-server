package com.server.calendar.todo.service;

import com.server.calendar.doamin.TodoList;
import com.server.calendar.doamin.User;
import com.server.calendar.todo.dto.CreateTodoDto;
import com.server.calendar.todo.dto.CreateTodoDto.CreateTodoDtoBuilder;
import com.server.calendar.todo.repository.TodoRepository;
import com.server.calendar.user.repository.UserRepository;
import com.server.calendar.util.exception.EntityNotFoundException;
import com.server.calendar.util.exception.TokenInvalidException;
import com.server.calendar.util.jwt.JwtTokenProvider;
import com.server.calendar.util.response.CustomApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TodoServiceImpl implements TodoService{

    private final TodoRepository todoRepository;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public ResponseEntity<CustomApiResponse<?>> createTodo(CreateTodoDto dto, HttpServletRequest request) {

        // 헤더에서 토큰 받아오기
        String token = extractToken(request);

        if (token == null || !jwtTokenProvider.validateToken(token)) {
            throw new TokenInvalidException("토큰이 유효하지 않습니다.");
        }

        // 토큰에서 userId 추출
        String userId = jwtTokenProvider.getClaimsFromToken(token).getSubject();

        // userId를 사용하여 User 엔티티 조회
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));


        // DTO를 TodoList 엔티티로 변환하고 User 엔티티와 연결
        TodoList todoList = TodoList.builder()
                .title(dto.getTitle())
                .date(dto.getDate())
                .category(dto.getCategory())
                .user(user)
                .build();

        // TodoList 엔티티 저장
        todoRepository.save(todoList);

        CustomApiResponse<?> response = CustomApiResponse.createSuccess(201, null, "할 일 생성 성공");
        return ResponseEntity.status(201).body(response);
    }

    private String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
