package com.server.calendar.todo.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class GetOneMonthDto {
    private int doneCount;
    private int notDoneCount;

    @Builder
    public GetOneMonthDto(int doneCount, int notDoneCount) {
        this.doneCount = doneCount;
        this.notDoneCount = notDoneCount;
    }
}
