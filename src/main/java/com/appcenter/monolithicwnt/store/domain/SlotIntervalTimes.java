package com.appcenter.monolithicwnt.store.domain;

import com.appcenter.monolithicwnt.global.exception.BusinessException;
import com.appcenter.monolithicwnt.global.exception.ErrorCode;
import lombok.Getter;

@Getter
public enum SlotIntervalTimes {
    THIRTY_MINUTES(30),
    SIXTY_MINUTES(60);

    private final int minutes;

    SlotIntervalTimes(int minutes) {
        this.minutes = minutes;
    }

    public static SlotIntervalTimes from(int minutes) {
        for (SlotIntervalTimes interval : SlotIntervalTimes.values()) {
            if (interval.getMinutes() == minutes) {
                return interval;
            }
        }
        throw new BusinessException(ErrorCode.STORE_SLOT_TIME_INVALID);
    }
}