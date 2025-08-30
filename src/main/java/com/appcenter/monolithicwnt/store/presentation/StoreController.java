package com.appcenter.monolithicwnt.store.presentation;

import com.appcenter.monolithicwnt.auth.presentation.AuthenticationPrincipal;
import com.appcenter.monolithicwnt.global.dto.response.SuccessResponse;
import com.appcenter.monolithicwnt.store.application.StoreQueryService;
import com.appcenter.monolithicwnt.store.application.StoreService;
import com.appcenter.monolithicwnt.store.dto.request.StoreCreateRequest;
import com.appcenter.monolithicwnt.store.dto.response.StoreResponse;
import com.appcenter.monolithicwnt.store.presentation.docs.StoreControllerDocs;
import com.appcenter.monolithicwnt.user.dto.request.Authentication;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/stores")
@RequiredArgsConstructor
public class StoreController implements StoreControllerDocs {

    private final StoreService storeService;
    private final StoreQueryService storeQueryService;

    @PostMapping("/create")
    public ResponseEntity<SuccessResponse<?>> createStore(
            @RequestBody StoreCreateRequest request,
            @AuthenticationPrincipal Authentication authentication) {
        Long ownerId = authentication.id();
        storeService.createStore(request,ownerId);
        HttpStatus status = HttpStatus.CREATED;
        return ResponseEntity.status(status).body(SuccessResponse.of(status, null));
    }

    @GetMapping("/{storeId}")
    public ResponseEntity<SuccessResponse<StoreResponse>> getStores(
            @PathVariable("storeId") Long storeId) {
        StoreResponse response = storeQueryService.getStore(storeId);
        HttpStatus status = HttpStatus.OK;
        return ResponseEntity.status(status).body(SuccessResponse.of(status,response));
    }
}
