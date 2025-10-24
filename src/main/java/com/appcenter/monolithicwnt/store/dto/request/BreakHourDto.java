package com.appcenter.monolithicwnt.store.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.DayOfWeek;
import java.time.LocalTime;

public record BreakHourDto(
        DayOfWeek dayOfWeek,

        @JsonFormat(pattern = "HH:mm")
        @Schema(examples = "11:00")
        LocalTime startTime,

        @JsonFormat(pattern = "HH:mm")
        @Schema(examples = "12:00")
        LocalTime endTime,

        @Schema(examples = "false")
        boolean nextDay  // 자정 넘김 여부
) {
}
