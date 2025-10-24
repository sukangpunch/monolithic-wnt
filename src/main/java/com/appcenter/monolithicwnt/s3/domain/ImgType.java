package com.appcenter.monolithicwnt.s3.domain;

import lombok.Getter;

@Getter
public enum ImgType {
    MENU("menu")
    ;

    private final String type;

    ImgType(final String type) {
        this.type = type;
    }

}
