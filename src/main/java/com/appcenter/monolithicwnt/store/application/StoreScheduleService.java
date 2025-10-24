package com.appcenter.monolithicwnt.store.application;

import com.appcenter.monolithicwnt.global.exception.BusinessException;
import com.appcenter.monolithicwnt.global.exception.ErrorCode;
import com.appcenter.monolithicwnt.store.domain.BreakHour;
import com.appcenter.monolithicwnt.store.domain.BusinessHour;
import com.appcenter.monolithicwnt.store.domain.Holiday;
import com.appcenter.monolithicwnt.store.domain.Store;
import com.appcenter.monolithicwnt.store.dto.request.BreakHourRequest;
import com.appcenter.monolithicwnt.store.dto.request.BusinessHourRequest;
import com.appcenter.monolithicwnt.store.dto.request.HolidayRequest;
import com.appcenter.monolithicwnt.store.infrastructure.BreakHourRepository;
import com.appcenter.monolithicwnt.store.infrastructure.BusinessHourRepository;
import com.appcenter.monolithicwnt.store.infrastructure.HolidayRepository;
import com.appcenter.monolithicwnt.store.infrastructure.StoreRepository;
import com.appcenter.monolithicwnt.store.util.TimeSegments;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class StoreScheduleService {

    private final StoreRepository storeRepository;
    private final BusinessHourRepository businessHourRepository;
    private final BreakHourRepository breakHourRepository;
    private final HolidayRepository holidayRepository;
    private final TimeSegments timeSegments;

    @Transactional
    public void upsertBusinessHours(Long storeId, BusinessHourRequest request) {

        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new BusinessException(ErrorCode.STORE_NOT_FOUND));

        List<TimeSegments.Input> inputs = request.businessHourDtos().stream()
                .map(d -> new TimeSegments.Input(d.dayOfWeek(), d.openTime(), d.closeTime(), d.nextDay()))
                .toList();

        // 시간대 유효성 검증
        timeSegments.assertNoOverlapByDow(inputs);

        // 전량 교체
        businessHourRepository.deleteByStore_Id(storeId);
        List<BusinessHour> businessHours = new ArrayList<>(inputs.size());

        for (TimeSegments.Input in : inputs) {
            businessHours.add(new BusinessHour(
                    store, in.dow(), in.start(), in.end(), in.nextDay()
            ));
        }
        
        // 저장
        businessHourRepository.saveAll(businessHours);
    }


    // ================== 휴게시간 업서트 ==================
    @Transactional
    public void upsertBreakHours(Long storeId, BreakHourRequest request) {

        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new BusinessException(ErrorCode.STORE_NOT_FOUND));

        List<TimeSegments.Input> inputs = request.breakHourDtos().stream()
                .map(d -> new TimeSegments.Input(d.dayOfWeek(), d.startTime(), d.endTime(), d.nextDay()))
                .toList();

        // 시간대 유효성 검증
        timeSegments.assertNoOverlapByDow(inputs);

        // 전량 교체
        breakHourRepository.deleteByStore_Id(storeId);

        List<BreakHour> breakHours = new ArrayList<>(inputs.size());
        for (TimeSegments.Input in : inputs) {
            breakHours.add(new BreakHour(
                    store, in.dow(), in.start(), in.end(), in.nextDay()
            ));
        }
        
        // 저장
        breakHourRepository.saveAll(breakHours);
    }

    // ================== 휴일 업서트(전량 교체) ==================
    @Transactional
    public void upsertHolidays(Long storeId, HolidayRequest request) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new BusinessException(ErrorCode.STORE_NOT_FOUND));

        // 전량 교체(멱등)
        holidayRepository.deleteByStore_Id(storeId);

        // 만약 저장할 date 가 비어있다면
        if (request.dates() == null || request.dates().isEmpty()) return;

        // 중복 제거
        var uniq = new LinkedHashSet<>(request.dates());
        List<Holiday> list = uniq.stream()
                .map(d -> new Holiday(store, d))
                .toList();

        holidayRepository.saveAll(list);
    }

}
