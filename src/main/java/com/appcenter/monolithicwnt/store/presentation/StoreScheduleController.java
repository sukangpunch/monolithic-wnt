package com.appcenter.monolithicwnt.store.presentation;

import com.appcenter.monolithicwnt.global.dto.response.SuccessResponse;
import com.appcenter.monolithicwnt.store.application.StoreScheduleService;
import com.appcenter.monolithicwnt.store.dto.request.BreakHourRequest;
import com.appcenter.monolithicwnt.store.dto.request.BusinessHourRequest;
import com.appcenter.monolithicwnt.store.dto.request.HolidayRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/stores/{storeId}/schedule")
@RequiredArgsConstructor
public class StoreScheduleController {

    private final StoreScheduleService storeScheduleService;

    @PutMapping("/business-hours")
    public ResponseEntity<SuccessResponse<Void>> upsertBusinessHours(
            @PathVariable("storeId") Long storeId,
            @RequestBody BusinessHourRequest request){
        storeScheduleService.upsertBusinessHours(storeId, request);
        HttpStatus status = HttpStatus.OK;
        return ResponseEntity.status(status).body(SuccessResponse.of(status, null));
    }

    @PutMapping("/break-hours")
    public ResponseEntity<SuccessResponse<Void>> upsertBreakHours(
            @PathVariable("storeId") Long storeId,
            @RequestBody BreakHourRequest request){
        storeScheduleService.upsertBreakHours(storeId, request);
        HttpStatus status = HttpStatus.OK;
        return ResponseEntity.status(status).body(SuccessResponse.of(status, null));
    }

    @PutMapping("/holidays")
    public ResponseEntity<SuccessResponse<Void>> upsertHolidays(
            @PathVariable("storeId") Long storeId,
            @RequestBody HolidayRequest request){
        storeScheduleService.upsertHolidays(storeId, request);
        HttpStatus status = HttpStatus.OK;
        return ResponseEntity.status(status).body(SuccessResponse.of(status, null));
    }
}
