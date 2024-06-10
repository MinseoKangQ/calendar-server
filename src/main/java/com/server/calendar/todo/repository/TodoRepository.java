package com.server.calendar.todo.repository;

import com.server.calendar.doamin.TodoList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TodoRepository extends JpaRepository<TodoList, Long> {
}
