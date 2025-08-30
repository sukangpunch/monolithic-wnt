package com.appcenter.monolithicwnt.store.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;
import java.util.List;

public record HolidayRequest(
        Long storeId,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
        List<LocalDate> dates
) {
}
