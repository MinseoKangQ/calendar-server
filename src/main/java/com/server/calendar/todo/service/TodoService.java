package com.server.calendar.todo.service;

import com.server.calendar.todo.dto.CreateTodoDto;
import com.server.calendar.util.response.CustomApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import org.springframework.http.ResponseEntity;

public interface TodoService {

    ResponseEntity<CustomApiResponse<?>> createTodo(CreateTodoDto dto, HttpServletRequest request);
    ResponseEntity<CustomApiResponse<?>> getOneDayTodoList(LocalDate date, HttpServletRequest request);
}