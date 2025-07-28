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
    private LocalTime openDateTime;

    @Column(nullable = false)
    private LocalTime closeDateTime;

    @Column(nullable = false)
    private boolean isNextDayClose;

    @ManyToOne(fetch = FetchType.LAZY)
    private Store store;

    public BusinessHour(DayOfWeek dayOfWeek, LocalTime openDateTime, LocalTime closeDateTime, boolean isNextDayClose, Store store) {
        isOpenBeforeClose(openDateTime, closeDateTime,isNextDayClose);
        this.dayOfWeek = dayOfWeek;
        this.openDateTime = openDateTime;
        this.closeDateTime = closeDateTime;
        this.isNextDayClose = isNextDayClose;
        this.store = store;
    }

    // 영업 시작 - 영업 끝에 일자가 변경된다면(예: 화(OPEN)-수(CLOSE)) OPEN 시간보다 CLOSE 가 더 작을 수 있습니다.
    private void isOpenBeforeClose(LocalTime openDateTime, LocalTime closeDateTime, boolean isNextDayClose) {
        if(!isNextDayClose && !openDateTime.isBefore(closeDateTime)) {
            throw new BusinessException(ErrorCode.STORE_OPEN_CLOSE_INVALID);
        }
    }

}
