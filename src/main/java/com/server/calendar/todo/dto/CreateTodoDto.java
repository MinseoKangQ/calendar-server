package com.server.calendar.todo.dto;

import com.server.calendar.doamin.Category;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CreateTodoDto {

    private String date;

    private String title;

//    @Enumerated(EnumType.STRING)
    private Category category;

}
