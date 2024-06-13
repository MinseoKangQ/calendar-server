package com.server.calendar.todo.controller;

import com.server.calendar.todo.dto.ChangeTitleDto;
import com.server.calendar.todo.dto.CreateTodoDto;
import com.server.calendar.todo.service.TodoService;
import com.server.calendar.util.response.CustomApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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

    @GetMapping("/oneDay/{date}")
    public ResponseEntity<CustomApiResponse<?>> getOneDayTodoList(@PathVariable LocalDate date, HttpServletRequest request) {
        return todoService.getOneDayTodoList(date, request);
    }

    @GetMapping("oneMonth/{date}")
    public ResponseEntity<CustomApiResponse<?>> getOneMonth(@PathVariable String date, HttpServletRequest request) {
        return todoService.getOneMonth(date, request);

    }

    @PutMapping("/checking/{todoId}")
    public ResponseEntity<CustomApiResponse<?>> changeCheckState(@PathVariable Long todoId, HttpServletRequest request) {
        return todoService.changeCheckState(todoId, request);
    }

    @PutMapping("/title/{todoId}")
    public ResponseEntity<CustomApiResponse<?>> changeTitle(@PathVariable Long todoId, @RequestBody ChangeTitleDto dto, HttpServletRequest request) {
        return todoService.changeTitle(dto, todoId, request);
    }

    @DeleteMapping("/{todoId}")
    public ResponseEntity<CustomApiResponse<?>> deleteTodo(@PathVariable Long todoId, HttpServletRequest request) {
        return todoService.deleteTodo(todoId, request);
    }

}
