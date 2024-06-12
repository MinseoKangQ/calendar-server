package com.server.calendar.doamin;

import com.server.calendar.todo.dto.CreateTodoDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "TodoLists")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TodoList {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    private LocalDate date;

    private String title;

    @Enumerated(EnumType.STRING)
    private Category category;

    @Builder.Default
    @Column(name = "is_done")
    private Boolean isDone = false;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    // 상태 바꾸기
    public void changeDoneStatus(TodoList todoList) {
        this.isDone = !this.isDone;
    }

    // 할 일 생성
    public static TodoList toEntity(CreateTodoDto dto) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate parsedDate = LocalDate.parse(dto.getDate(), formatter);

        return TodoList.builder()
                .title(dto.getTitle())
                .date(parsedDate)
                .category(dto.getCategory())
                .build();
    }
}
