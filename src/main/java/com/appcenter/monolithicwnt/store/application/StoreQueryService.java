package com.appcenter.monolithicwnt.store.application;

import com.appcenter.monolithicwnt.global.exception.BusinessException;
import com.appcenter.monolithicwnt.global.exception.ErrorCode;
import com.appcenter.monolithicwnt.store.domain.Store;
import com.appcenter.monolithicwnt.store.domain.StoreRepository;
import com.appcenter.monolithicwnt.store.dto.response.StoreResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class StoreQueryService {
    private final StoreRepository storeRepository;

    public StoreResponse getStore(Long storeId) {
        Store store = storeRepository.findById(storeId).orElseThrow(() -> new BusinessException(ErrorCode.STORE_NOT_FOUND));
        return StoreResponse.from(store);
    }
}
