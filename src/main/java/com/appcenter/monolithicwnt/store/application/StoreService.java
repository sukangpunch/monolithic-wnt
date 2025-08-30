package com.appcenter.monolithicwnt.store.application;

import com.appcenter.monolithicwnt.global.exception.BusinessException;
import com.appcenter.monolithicwnt.global.exception.ErrorCode;
import com.appcenter.monolithicwnt.store.domain.Address;
import com.appcenter.monolithicwnt.store.domain.Store;
import com.appcenter.monolithicwnt.store.domain.StoreRepository;
import com.appcenter.monolithicwnt.store.dto.request.StoreCreateRequest;
import com.appcenter.monolithicwnt.user.domain.User;
import com.appcenter.monolithicwnt.user.infrastructure.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class StoreService {
    private final StoreRepository storeRepository;
    private final UserRepository userRepository;

    @Transactional
    public void createStore(StoreCreateRequest request, Long ownerId) {
        User user = userRepository.findById(ownerId).orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        if(storeRepository.existsByName(request.name())){
            throw new BusinessException(ErrorCode.STORE_NAME_CONFLICT);
        }

        Address address = new Address(request.fullAddress(), request.latitude(), request.longitude());
        Store newStore = new Store(request.name(), request.phone(), request.instagram(), request.slotIntervalTimes(), address, user);

        storeRepository.save(newStore);
    }



}
