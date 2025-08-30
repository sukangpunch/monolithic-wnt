package com.appcenter.monolithicwnt.store.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Entity(name = "special_days")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SpecialDay {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Store store;

    @Column(nullable = false)
    private LocalDate date;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SpecialType type;

    @OneToMany(mappedBy = "specialDay", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SpecialDay> specialDays;
}
