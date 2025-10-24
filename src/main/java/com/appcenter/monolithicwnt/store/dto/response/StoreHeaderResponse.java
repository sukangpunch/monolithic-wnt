package com.appcenter.monolithicwnt.store.dto.response;

public record StoreHeaderResponse(
        Long storeId,
        String name,
        String address,
        String phone,
        String instagram,
        boolean openNow,
        String statusText
){
}
