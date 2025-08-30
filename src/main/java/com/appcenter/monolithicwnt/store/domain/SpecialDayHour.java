package com.appcenter.monolithicwnt.store.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SpecialDayHour {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private SpecialDay specialDay;

    @Column(nullable=false)
    private LocalTime startTime;

    @Column(nullable=false)
    private LocalTime endTime;

    @Column(nullable=false)
    private boolean nextDayClose;
}
