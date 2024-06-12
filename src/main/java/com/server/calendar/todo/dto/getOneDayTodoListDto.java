package com.server.calendar.todo.dto;

import com.server.calendar.doamin.Category;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class getOneDayTodoListDto {
    private Long todoId;
    private String title;
    private Category category;
    private Boolean isDone;
}
