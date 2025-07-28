package com.appcenter.monolithicwnt.store.domain;

import java.util.Optional;

public interface StoreRepository {
    void save(Store store);
    Optional<Store> findById(Long id);
    boolean existsByName(String name);
}
