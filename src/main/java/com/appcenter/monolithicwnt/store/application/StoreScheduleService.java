package com.appcenter.monolithicwnt.store.application;

import com.appcenter.monolithicwnt.global.exception.BusinessException;
import com.appcenter.monolithicwnt.global.exception.ErrorCode;
import com.appcenter.monolithicwnt.store.domain.*;
import com.appcenter.monolithicwnt.store.dto.request.BreakHourRequest;
import com.appcenter.monolithicwnt.store.dto.request.BusinessHourRequest;
import com.appcenter.monolithicwnt.store.dto.request.HolidayRequest;
import com.appcenter.monolithicwnt.store.infrastructure.BreakHourRepository;
import com.appcenter.monolithicwnt.store.infrastructure.BusinessHourRepository;
import com.appcenter.monolithicwnt.store.infrastructure.HolidayRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class StoreScheduleService {

    private final StoreRepository storeRepository;
    private final BusinessHourRepository businessHourRepository;
    private final BreakHourRepository breakHourRepository;
    private final HolidayRepository holidayRepository;

    @Transactional
    public void upsertBusinessHours(Long storeId, BusinessHourRequest request) {

        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new BusinessException(ErrorCode.STORE_NOT_FOUND));

        // 입력 파싱 → 내부 모델
        List<BH> inputs = request.businessHourDtos().stream()
                .map(d -> new BH(d.dayOfWeek(), d.openTime(), d.closeTime(), d.nextDay()))
                .toList();

        // 불변식 & 겹침 검증(요일 기준, 자정분할 이후)
        validateNoOverlapByDow(inputs);

        // 전량 교체
        businessHourRepository.deleteByStore_Id(storeId);
        List<BusinessHour> businessHours = new ArrayList<>(inputs.size());
        for (BH in : inputs) {
            businessHours.add(new BusinessHour(
                    store, in.dow, in.open, in.close, in.nextDay
            ));
        }
        businessHourRepository.saveAll(businessHours);
    }

    // ================== 휴게시간 업서트 ==================
    @Transactional
    public void upsertBreakHours(Long storeId, BreakHourRequest request) {

        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new BusinessException(ErrorCode.STORE_NOT_FOUND));

        List<BH> inputs = request.breakHourDtos().stream()
                .map(d -> new BH(d.dayOfWeek(), d.startTime(), d.endTime(), d.nextDay()))
                .toList();

        validateNoOverlapByDow(inputs);

        breakHourRepository.deleteByStore_Id(storeId);
        List<BreakHour> breakHours = new ArrayList<>(inputs.size());
        for (BH in : inputs) {
            breakHours.add(new BreakHour(
                    store, in.dow, in.open, in.close, in.nextDay
            ));
        }
        breakHourRepository.saveAll(breakHours);
    }

    // ================== 휴일 업서트(전량 교체) ==================
    @Transactional
    public void upsertHolidays(Long storeId, HolidayRequest request) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new BusinessException(ErrorCode.STORE_NOT_FOUND));

        // 전량 교체(멱등)
        holidayRepository.deleteByStore_Id(storeId);

        if (request.dates() == null || request.dates().isEmpty()) return;

        // 중복 제거
        var uniq = new LinkedHashSet<>(request.dates());
        List<Holiday> list = uniq.stream()
                .map(d -> new Holiday(store, d))
                .toList();

        holidayRepository.saveAll(list);
    }

    private record BH(DayOfWeek dow, LocalTime open, LocalTime close, boolean nextDay) {}
    private record Seg(DayOfWeek dow, LocalTime start, LocalTime end) {}

    /** nextDay=false → s<e / nextDay=true → s>e (진짜 자정 넘김만 허용) */
    private void guardInvariants(BH in) {
        if (!in.nextDay && !in.open.isBefore(in.close)) {
            throw new BusinessException(ErrorCode.STORE_OPEN_CLOSE_INVALID);
        }
        if (in.nextDay && !in.open.isAfter(in.close)) {
            throw new BusinessException(ErrorCode.STORE_OPEN_CLOSE_INVALID);
        }
    }

    /** 22:00~02:00 → [22:00,24:00) + [00:00,02:00) */
    private List<Seg> splitByMidnight(DayOfWeek dow, LocalTime s, LocalTime e, boolean nextDay) {
        if(s.equals(e))
            return List.of();

        boolean crossesMidnight = nextDay || e.isBefore(s);

        // 자정 넘김 여부 판단
        if(!crossesMidnight)
            return List.of(new Seg(dow, s, e));

        List<Seg> out = new ArrayList<>(2);

        if (!s.equals(LocalTime.MIDNIGHT))
            out.add(new Seg(dow, s, LocalTime.MIDNIGHT));

        DayOfWeek next = dow.plus(1);
        if (!e.equals(LocalTime.MIDNIGHT))
            out.add(new Seg(next, LocalTime.MIDNIGHT, e));

        return out;
    }

    /** 요일별로 분해해 겹침 금지(맞닿음 허용) */
    private void validateNoOverlapByDow(List<BH> inputs) {
        var perDay = new EnumMap<DayOfWeek, List<Seg>>(DayOfWeek.class);

        for (BH in : inputs) {
            guardInvariants(in);
            for (Seg seg : splitByMidnight(in.dow, in.open, in.close, in.nextDay)) {
                perDay.computeIfAbsent(seg.dow, k -> new ArrayList<>()).add(seg);
            }
        }

        for (var entry : perDay.entrySet()) {
            var segs = entry.getValue();
            segs.sort(Comparator.comparing(Seg::start));
            LocalTime prevEnd = null;
            for (Seg seg : segs) {
                log.info("Checking seg: day : {}, start : {}, end : {}", seg.dow, seg.start, seg.end);
                // 겹침만 금지, 맞닿음(==)은 허용: s < prevEnd 이면 겹침
                if (prevEnd != null && seg.start.isBefore(prevEnd)) {
                    log.info("prevEnd : {} , nowStart : {}", prevEnd, seg.start);
                    throw new BusinessException(ErrorCode.STORE_OPEN_CLOSE_INVALID);
                }
                if (prevEnd == null || seg.end.isAfter(prevEnd)) prevEnd = seg.end;
            }
        }
    }

}
