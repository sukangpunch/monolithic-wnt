package com.appcenter.monolithicwnt.store.domain;

import com.appcenter.monolithicwnt.global.exception.BusinessException;
import com.appcenter.monolithicwnt.global.exception.ErrorCode;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Entity(name = "business_hours")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BusinessHour {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private DayOfWeek dayOfWeek;

    @Column(nullable = false)
    private LocalTime openTime;

    @Column(nullable = false)
    private LocalTime closeTime;

    @Column(nullable = false)
    private boolean nextDayClose;

    @JoinColumn(nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Store store;

    public BusinessHour(Store store, DayOfWeek dayOfWeek, LocalTime openTime, LocalTime closeTime
                        , boolean nextDayClose) {
        isOpenBeforeClose(openTime, closeTime, nextDayClose);
        this.dayOfWeek = dayOfWeek;
        this.openTime = openTime;
        this.closeTime = closeTime;
        this.nextDayClose = nextDayClose;
        this.store = store;
    }

    // 영업 시작 - 영업 끝에 일자가 변경된다면(예: 화(OPEN)-수(CLOSE)) OPEN 시간보다 CLOSE 가 더 작을 수 있습니다.
    private void isOpenBeforeClose(LocalTime openDateTime, LocalTime closeDateTime, boolean nextDayClose) {
        if(!nextDayClose && !openDateTime.isBefore(closeDateTime)) {
            throw new BusinessException(ErrorCode.STORE_OPEN_CLOSE_INVALID);
        }
    }
}
