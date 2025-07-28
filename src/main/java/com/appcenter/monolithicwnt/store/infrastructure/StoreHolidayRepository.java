package com.appcenter.monolithicwnt.store.infrastructure;

import com.appcenter.monolithicwnt.store.domain.StoreHoliday;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoreHolidayRepository extends JpaRepository<StoreHoliday, Long> {
}
