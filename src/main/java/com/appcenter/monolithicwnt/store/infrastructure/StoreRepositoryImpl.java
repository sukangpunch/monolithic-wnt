package com.appcenter.monolithicwnt.store.infrastructure;

import com.appcenter.monolithicwnt.store.domain.Store;
import com.appcenter.monolithicwnt.store.domain.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class StoreRepositoryImpl implements StoreRepository {
    private final StoreJpaRepository jpaRepository;


    @Override
    public void save(Store store) {
        jpaRepository.save(store);
    }

    @Override
    public Optional<Store> findById(Long id) {
        return jpaRepository.findById(id);
    }

    @Override
    public boolean existsByName(String name) {
        return jpaRepository.existsByName(name);
    }
}
