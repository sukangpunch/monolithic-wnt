package com.appcenter.monolithicwnt.store.infrastructure;

import com.appcenter.monolithicwnt.store.domain.Holiday;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface HolidayRepository extends JpaRepository<Holiday, Long> {

    @Modifying
    int deleteByStore_Id(Long storeId);
    List<Holiday> findByStore_Id(Long storeId);
    List<Holiday> findByStore_IdOrderByDateAsc(Long storeId);
    Optional<Holiday> findByStore_IdAndDate(Long storeId, LocalDate date);
    boolean existsByStore_IdAndDate(Long storeId, LocalDate date);
}
