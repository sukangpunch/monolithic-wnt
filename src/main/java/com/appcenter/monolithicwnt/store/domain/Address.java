package com.appcenter.monolithicwnt.store.domain;

import com.appcenter.monolithicwnt.global.exception.BusinessException;
import com.appcenter.monolithicwnt.global.exception.ErrorCode;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Embeddable
@Getter
@NoArgsConstructor
public class Address {

    @Column(nullable = false)
    private String fullAddress;

    @Column(nullable = false)
    private double latitude;

    @Column(nullable = false)
    private double longitude;

    public Address(String fullAddress, double latitude, double longitude) {
        validate(fullAddress, latitude, longitude);
        this.fullAddress = fullAddress;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    private void validate(String fullAddress, double latitude, double longitude) {
        validateFullAddress(fullAddress);
        validateLatitude(latitude);
        validateLongitude(longitude);
    }

    private void validateFullAddress(String fullAddress) {
        if(fullAddress == null || fullAddress.isBlank()){
            throw new BusinessException(ErrorCode.NICKNAME_INVALID);
        }
    }

    private void validateLatitude(double latitude) {
        if (latitude < -90.0 || latitude > 90.0) {
            throw new BusinessException(ErrorCode.STORE_LATITUDE_INVALID);
        }
    }

    private void validateLongitude(double longitude) {
        if (longitude < -180.0 || longitude > 180.0) {
            throw new BusinessException(ErrorCode.STORE_LONGITUDE_INVALID);
        }
    }
}
