package com.appcenter.monolithicwnt.store.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.DayOfWeek;
import java.time.LocalTime;

public record BusinessHourDto(
        DayOfWeek dayOfWeek,

        @JsonFormat(pattern = "HH:mm")
        @Schema(examples = "09:00")
        LocalTime openTime,

        @JsonFormat(pattern = "HH:mm")
        @Schema(examples = "09:00")
        LocalTime closeTime,

        @Schema(examples = "false")
        boolean nextDay
) {
}
