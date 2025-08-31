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
    private boolean nextDayClose;

    @JoinColumn(nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Store store;

    public BreakHour(Store store, DayOfWeek dayOfWeek, LocalTime startTime, LocalTime endTime, boolean nextDayClose) {
        isStartBeforeEnd(startTime,endTime,nextDayClose);
        this.store = store;
        this.dayOfWeek = dayOfWeek;
        this.startTime = startTime;
        this.endTime = endTime;
        this.nextDayClose = nextDayClose;
    }

    private void isStartBeforeEnd(LocalTime startTime, LocalTime endTime, boolean nextDayClose) {
        if(!nextDayClose && !startTime.isBefore(endTime)) {
            throw new BusinessException(ErrorCode.STORE_BREAK_START_END_INVALID);
        }
    }
}
