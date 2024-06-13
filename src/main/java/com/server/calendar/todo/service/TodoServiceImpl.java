package com.server.calendar.todo.service;

import com.server.calendar.doamin.TodoList;
import com.server.calendar.doamin.User;
import com.server.calendar.todo.dto.ChangeTitleDto;
import com.server.calendar.todo.dto.CreateTodoDto;
import com.server.calendar.todo.dto.CreateTodoDto.CreateTodoDtoBuilder;
import com.server.calendar.todo.dto.GetOneMonthDto;
import com.server.calendar.todo.dto.getOneDayTodoListDto;
import com.server.calendar.todo.repository.TodoRepository;
import com.server.calendar.user.repository.UserRepository;
import com.server.calendar.util.exception.EntityNotFoundException;
import com.server.calendar.util.exception.TokenInvalidException;
import com.server.calendar.util.jwt.JwtTokenProvider;
import com.server.calendar.util.response.CustomApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    public ResponseEntity<CustomApiResponse<?>> getOneMonth(String date, HttpServletRequest request) {
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

        // date 파싱하여 YearMonth 객체로 변환
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
        YearMonth yearMonth = YearMonth.parse(date, formatter);

        // 해당 달의 첫 번째 날과 마지막 날 계산
        LocalDate firstDayOfMonth = yearMonth.atDay(1);
        LocalDate lastDayOfMonth = yearMonth.atEndOfMonth();

        // 해당 달의 모든 TodoList 조회
        List<TodoList> todos = todoRepository.findByUserAndDateBetween(user, firstDayOfMonth, lastDayOfMonth);

        // 날짜별 할 일 통계 계산
        Map<Integer, GetOneMonthDto> resultMap = new HashMap<>();
        for (LocalDate day = firstDayOfMonth; !day.isAfter(lastDayOfMonth); day = day.plusDays(1)) {
            int doneCount = 0;
            int notDoneCount = 0;

            for (TodoList todo : todos) {
                if (todo.getDate().equals(day)) {
                    if (todo.getIsDone()) {
                        doneCount++;
                    } else {
                        notDoneCount++;
                    }
                }
            }

            resultMap.put(day.getDayOfMonth(), GetOneMonthDto.builder()
                    .doneCount(doneCount)
                    .notDoneCount(notDoneCount)
                    .build());
        }

        CustomApiResponse<Map<Integer, GetOneMonthDto>> response = CustomApiResponse.createSuccess(200, resultMap, "한 달 조회 성공");
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<CustomApiResponse<?>> getNotDoneCount(HttpServletRequest request) {
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

        // 연관된 todoList 중 isDone 상태가 false 인 엔티티들 개수 찾기
        Long count = todoRepository.countTodoListsByUserAndIsDone(user, false);

        // data 넣기
        CustomApiResponse<?> response = CustomApiResponse.createSuccess(200, count, "아직 끝나지 않은 일 개수 조회 성공");
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
