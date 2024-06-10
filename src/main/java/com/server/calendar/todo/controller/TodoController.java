package com.server.calendar.todo.controller;

import com.server.calendar.todo.dto.CreateTodoDto;
import com.server.calendar.todo.service.TodoService;
import com.server.calendar.util.response.CustomApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/todo")
@RequiredArgsConstructor
public class TodoController {

    private final TodoService todoService;

    @PostMapping
    public ResponseEntity<CustomApiResponse<?>> createdTodo(@RequestBody CreateTodoDto dto, HttpServletRequest request) {
        return todoService.createTodo(dto, request);
    }


}
