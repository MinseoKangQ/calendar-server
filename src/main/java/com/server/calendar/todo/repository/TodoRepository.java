package com.server.calendar.todo.repository;

import com.server.calendar.doamin.TodoList;
import com.server.calendar.doamin.User;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TodoRepository extends JpaRepository<TodoList, Long> {
    List<TodoList> findByUserAndDate(User user, LocalDate date);
    Optional<TodoList> findTodoListById(Long id);
}
