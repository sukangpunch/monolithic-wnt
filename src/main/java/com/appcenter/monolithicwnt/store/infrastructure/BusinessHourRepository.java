package com.appcenter.monolithicwnt.store.infrastructure;

import com.appcenter.monolithicwnt.store.domain.BusinessHour;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BusinessHourRepository extends JpaRepository<BusinessHour, Long> {
}
