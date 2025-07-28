package com.appcenter.monolithicwnt.store.dto.response;

import com.appcenter.monolithicwnt.store.domain.Address;
import com.appcenter.monolithicwnt.store.domain.Store;
import com.appcenter.monolithicwnt.store.domain.StoreStatus;
import lombok.Builder;

@Builder
public record StoreResponse(
        Long userId,
        StoreStatus status,
        String name,
        String phone,
        String instagram,
        String fullAddress,
        double latitude,
        double longitude
) {
    public static StoreResponse from(Store store) {
        Address address = store.getAddress();
        return StoreResponse.builder()
                .userId(store.getOwner().getId())
                .status(store.getStatus())
                .name(store.getName())
                .phone(store.getPhone())
                .instagram(store.getInstagram())
                .fullAddress(address.getFullAddress())
                .latitude(address.getLatitude())
                .longitude(address.getLongitude())
                .build();
    }
}
