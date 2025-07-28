package com.appcenter.monolithicwnt.store.domain;

import com.appcenter.monolithicwnt.global.exception.BusinessException;
import com.appcenter.monolithicwnt.global.exception.ErrorCode;
import com.appcenter.monolithicwnt.user.domain.User;
import jakarta.persistence.*;
import lombok.*;

@Entity(name = "stores")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@ToString
public class Store {

    private static final String PHONE_REGEX = "^(010|011|016|017|018|019)\\d{7,8}$";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String phone;

    @Column(nullable = false)
    private String instagram;

    @Column(nullable = false)
    @Embedded
    private Address address;

    @Enumerated(EnumType.STRING)
    private StoreStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    private User ownerId;

    @ManyToOne(fetch = FetchType.LAZY)
    private BusinessHour businessHour;

    @ManyToOne(fetch = FetchType.LAZY)
    private StoreHoliday storeHoliday;

    public Store(String name, String phone, String instagram,
                 Address address, StoreStatus status,
                 User ownerId, BusinessHour businessHour, StoreHoliday storeHoliday) {
        validate(name, phone, instagram);
        this.name = name;
        this.phone = phone;
        this.instagram = instagram;
        this.address = address;
        this.status = status;
        this.ownerId = ownerId;
        this.businessHour = businessHour;
        this.storeHoliday = storeHoliday;
    }

    private void validate(String name, String phone, String instagram) {
        validateName(name);
        validatePhone(phone);
        validateInstagram(instagram);
    }

    private void validateName(String name) {
        if(name == null || name.isBlank()){
            throw new BusinessException(ErrorCode.STORE_NAME_INVALID);
        }
    }

    private void validatePhone(String phone) {
        if(phone == null || phone.isBlank() || !phone.matches(PHONE_REGEX)){
            throw new BusinessException(ErrorCode.STORE_PHONE_INVALID);
        }
    }

    private void validateInstagram(String instagram) {
        if(instagram == null || instagram.isBlank()){
            throw new BusinessException(ErrorCode.STORE_INSTAGRAM_INVALID);
        }
    }

}
