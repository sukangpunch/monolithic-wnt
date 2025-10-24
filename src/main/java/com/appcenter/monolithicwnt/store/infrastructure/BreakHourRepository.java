package com.appcenter.monolithicwnt.store.infrastructure;

import com.appcenter.monolithicwnt.store.domain.BreakHour;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.DayOfWeek;
import java.util.List;

public interface BreakHourRepository extends JpaRepository<BreakHour, Long> {

    @Modifying
    int deleteByStore_Id(Long storeId);
    List<BreakHour> findByStore_IdAndDayOfWeekOrderByStartTimeAsc(Long storeId, DayOfWeek dayOfWeek);

    @Query("""
            select bh from break_hours bh
            where bh.store.id = :storeId
            order by case bh.dayOfWeek
            when java.time.DayOfWeek.MONDAY then 1
            when java.time.DayOfWeek.TUESDAY then 2
            when java.time.DayOfWeek.WEDNESDAY then 3
            when java.time.DayOfWeek.THURSDAY then 4
            when java.time.DayOfWeek.FRIDAY then 5
            when java.time.DayOfWeek.SATURDAY then 6
            when java.time.DayOfWeek.SUNDAY then 7
            end
            """)
    List<BreakHour> findByStore_IdOrderByDayOfWeekAsc(Long storeId);
}
