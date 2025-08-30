package com.appcenter.monolithicwnt.store.infrastructure;

import com.appcenter.monolithicwnt.store.domain.BreakHour;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import java.time.DayOfWeek;
import java.util.List;

public interface BreakHourRepository extends JpaRepository<BreakHour, Long> {

    @Modifying
    int deleteByStore_Id(Long storeId);
    List<BreakHour> findByStore_IdAndDayOfWeekOrderByStartTimeAsc(Long storeId, DayOfWeek dayOfWeek);
}
