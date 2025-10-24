package com.appcenter.monolithicwnt.store.dto.request;

public record StoreCreateRequest(
        String name,
        String phone,
        String instagram,
        String fullAddress,
        int slotIntervalTimes,
        double latitude,
        double longitude
) {
}
