package com.appcenter.monolithicwnt.store.domain;

import com.appcenter.monolithicwnt.global.exception.BusinessException;
import com.appcenter.monolithicwnt.global.exception.ErrorCode;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Entity(name = "break_hours")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BreakHour {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private DayOfWeek dayOfWeek;

    @Column(nullable=false)
    private LocalTime startTime;

    @Column(nullable=false)
    private LocalTime endTime;

    @Column(nullable = false)
    private boolean isNextDayClose;

    @JoinColumn(nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Store store;

    public BreakHour(Store store, DayOfWeek dayOfWeek, LocalTime startTime, LocalTime endTime, boolean isNextDayClose) {
        if(!isNextDayClose && !startTime.isBefore(endTime)) {
            throw new BusinessException(ErrorCode.STORE_START_END_INVALID);
        }
        this.store = store;
        this.dayOfWeek = dayOfWeek;
        this.startTime = startTime;
        this.endTime = endTime;
        this.isNextDayClose = isNextDayClose;
    }
}
