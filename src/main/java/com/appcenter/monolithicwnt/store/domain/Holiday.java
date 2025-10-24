package com.appcenter.monolithicwnt.store.domain;

import com.appcenter.monolithicwnt.global.exception.BusinessException;
import com.appcenter.monolithicwnt.global.exception.ErrorCode;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity(name = "special_days")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Holiday {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Store store;

    @Column(nullable = false)
    private LocalDate date;

    public Holiday(Store store, LocalDate date) {
        validate(store, date);
        this.store = store;
        this.date = date;
    }

    private void validate(Store store, LocalDate date) {
        if (store == null || date == null) {
            throw new BusinessException(ErrorCode.STORE_HOLIDAY_INVALID);
        }
    }
}
