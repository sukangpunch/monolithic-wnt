package com.appcenter.monolithicwnt.store.dto.request;

import java.util.List;

public record BusinessHourRequest(
        Long storeId,
        List<BusinessHourDto> businessHourDtos
) {
}
