package com.appcenter.monolithicwnt.store.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity(name = "store_holidays")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StoreHoliday {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate holidayDate;

    @ManyToOne(fetch = FetchType.LAZY)
    private Store store;

    public StoreHoliday(Store store, LocalDate holidayDate) {
        this.holidayDate = holidayDate;
        this.store = store;
    }
}
