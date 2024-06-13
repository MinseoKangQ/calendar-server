package com.server.calendar.todo.service;

import com.server.calendar.doamin.TodoList;
import com.server.calendar.doamin.User;
import com.server.calendar.todo.dto.ChangeTitleDto;
import com.server.calendar.todo.dto.CreateTodoDto;
import com.server.calendar.todo.dto.CreateTodoDto.CreateTodoDtoBuilder;
import com.server.calendar.todo.dto.getOneDayTodoListDto;
import com.server.calendar.todo.repository.TodoRepository;
import com.server.calendar.user.repository.UserRepository;
import com.server.calendar.util.exception.EntityNotFoundException;
import com.server.calendar.util.exception.TokenInvalidException;
import com.server.calendar.util.jwt.JwtTokenProvider;
import com.server.calendar.util.response.CustomApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
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
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate parsedDate = LocalDate.parse(dto.getDate(), formatter);

        TodoList todoList = TodoList.builder()
                .title(dto.getTitle())
                .date(parsedDate)
                .category(dto.getCategory())
                .user(user)
                .build();

        // TodoList 엔티티 저장
        todoRepository.save(todoList);

        CustomApiResponse<?> response = CustomApiResponse.createSuccess(201, null, "할 일 생성 성공");
        return ResponseEntity.status(201).body(response);
    }

    @Override
    public ResponseEntity<CustomApiResponse<?>> getOneDayTodoList(LocalDate date, HttpServletRequest request) {

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

        // 특정 날짜의 할 일 목록 조회
        List<TodoList> todos = todoRepository.findByUserAndDate(user, date);

        // DTO로 변환
        List<getOneDayTodoListDto> todoListDtos = todos.stream()
                .map(todo -> getOneDayTodoListDto.builder()
                        .todoId(todo.getId())
                        .title(todo.getTitle())
                        .category(todo.getCategory())
                        .isDone(todo.getIsDone())
                        .build())
                .collect(Collectors.toList());

        CustomApiResponse<List<getOneDayTodoListDto>> response = CustomApiResponse.createSuccess(200, todoListDtos, "할 일 목록 조회 성공");
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<CustomApiResponse<?>> changeCheckState(Long todoId, HttpServletRequest request) {

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

        TodoList todoList = todoRepository.findTodoListById(todoId).orElseThrow(
                () -> new EntityNotFoundException("찾을 수 없는 할 일 입니다.")
        );

        todoList.changeDoneStatus();
        todoRepository.save(todoList);

        CustomApiResponse<?> response = CustomApiResponse.createSuccess(200, null, "체크 표시 변경 성공");
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<CustomApiResponse<?>> changeTitle(ChangeTitleDto dto, Long todoId, HttpServletRequest request) {
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

        TodoList todoList = todoRepository.findTodoListById(todoId).orElseThrow(
                () -> new EntityNotFoundException("찾을 수 없는 할 일 입니다.")
        );

        todoList.changeTitle(dto);
        todoRepository.save(todoList);

        CustomApiResponse<?> response = CustomApiResponse.createSuccess(200, null, "수정 성공");
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<CustomApiResponse<?>> deleteTodo(Long todoId, HttpServletRequest request) {
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

        TodoList todoList = todoRepository.findTodoListById(todoId).orElseThrow(
                () -> new EntityNotFoundException("찾을 수 없는 할 일 입니다.")
        );

        todoRepository.delete(todoList);

        CustomApiResponse<?> response = CustomApiResponse.createSuccess(200, null, "삭제 성공");
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
