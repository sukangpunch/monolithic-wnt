package com.appcenter.monolithicwnt.store.domain;

import lombok.Getter;

@Getter
public enum StoreStatus {
    OPEN("영업 중"),
    CLOSED("영업 종료"),
    HOLIDAY("휴무"),
    SUSPENDED("일시 중단");

    private final String description;

    StoreStatus(String description) {
        this.description = description;
    }
}
