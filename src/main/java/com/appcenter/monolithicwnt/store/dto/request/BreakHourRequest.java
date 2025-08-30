package com.appcenter.monolithicwnt.store.dto.request;

import java.util.List;

public record BreakHourRequest(
        List<BreakHourDto> breakHourDtos
) {
}
