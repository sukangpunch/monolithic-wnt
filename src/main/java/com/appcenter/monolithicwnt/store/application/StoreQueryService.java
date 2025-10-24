package com.appcenter.monolithicwnt.store.application;

import com.appcenter.monolithicwnt.global.exception.BusinessException;
import com.appcenter.monolithicwnt.global.exception.ErrorCode;
import com.appcenter.monolithicwnt.store.domain.BusinessHour;
import com.appcenter.monolithicwnt.store.domain.Store;
import com.appcenter.monolithicwnt.store.dto.response.StoreHeaderResponse;
import com.appcenter.monolithicwnt.store.dto.response.StoreResponse;
import com.appcenter.monolithicwnt.store.infrastructure.BusinessHourRepository;
import com.appcenter.monolithicwnt.store.infrastructure.HolidayRepository;
import com.appcenter.monolithicwnt.store.infrastructure.StoreRepository;
import com.appcenter.monolithicwnt.store.util.TimeSegments;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

import static com.appcenter.monolithicwnt.store.util.TimeSegments.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class StoreQueryService {
    private final StoreRepository storeRepository;
    private final BusinessHourRepository businessHourRepository;
    private final HolidayRepository holidayRepository;
    private final TimeSegments timeSegments;

    private static final ZoneId DEFAULT_ZONE = ZoneId.of("Asia/Seoul");
    private static final DateTimeFormatter HHMM = DateTimeFormatter.ofPattern("HH:mm");

    public StoreResponse getStore(Long storeId) {
        Store store = storeRepository.findById(storeId).orElseThrow(() -> new BusinessException(ErrorCode.STORE_NOT_FOUND));
        return StoreResponse.from(store);
    }

    public StoreHeaderResponse getStoreHeader(Long storeId){
        ZonedDateTime now = ZonedDateTime.now(DEFAULT_ZONE);

        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new BusinessException(ErrorCode.STORE_NOT_FOUND));

        // 오늘 날짜, 요일을 구해놓는다.
        ZonedDateTime nowK = now.withZoneSameInstant(DEFAULT_ZONE);
        LocalDate today = nowK.toLocalDate();

        // 만약 오늘이 휴무일이라면 로직 실행 없이 바로 리턴
        if(holidayRepository.existsByStore_IdAndDate(storeId, today)){
            return new StoreHeaderResponse(
                    store.getId(),
                    store.getName(),
                    store.getAddress().getFullAddress(),
                    store.getPhone(),
                    store.getInstagram(),
                    false,
                    "오늘 휴무"
            );
        }

        // 1) 주간 영업만 조회
        List<BusinessHour> businessHours = businessHourRepository.findByStore_IdOrderByDayOfWeekAsc(storeId);

        // 2) 영업 시간을 분리해서 map 으로 저장
        EnumMap<DayOfWeek, List<Segment>> openPerDay = getMapOfDayByDayWithResultOfSplitBusinessHour(businessHours);

        // 3) 영업 상태 계산
        LocalTime nowLt = nowK.toLocalTime();
        DayOfWeek dow = nowK.getDayOfWeek();
        OpenStatus openStatus = computeOpenStatus(storeId, openPerDay, today, dow, nowLt);

        // 오픈 시간, 마감시간, 오늘 요일, 영업 상태 를 기반으로 영업 상태 텍스트를 만듭니다.
        String statusText = makeStatusText(openStatus.openNow(), openStatus.openAt(), openStatus.closeAt(), today);

        return new StoreHeaderResponse(
                store.getId(),
                store.getName(),
                store.getAddress().getFullAddress(),
                store.getPhone(),
                store.getInstagram(),
                openStatus.openNow(),
                statusText
        );
    }

    private EnumMap<DayOfWeek, List<Segment>> getMapOfDayByDayWithResultOfSplitBusinessHour(List<BusinessHour> businessHours) {
        //분할 후 요일별 그룹을 만들기 위한 EnumMap
        EnumMap<DayOfWeek, List<Segment>> openPerDay = new EnumMap<>(DayOfWeek.class);

        // BusinessHour 을 Input dto 로 만든 다음 split 한 결과를 Segment 로 받아서 map 에 저장 
        for (BusinessHour bh : businessHours) {
            var input = new Input(bh.getDayOfWeek(), bh.getOpenTime(), bh.getCloseTime(), bh.isNextDayClose());
            for (Segment seg : timeSegments.split(input)) {
                openPerDay.computeIfAbsent(seg.dow(), k -> new ArrayList<>()).add(seg);
            }
        }
        // 각 요일의 영업 시간 segment 들을 SEG_ORDER 에 따라 정렬
        openPerDay.values().forEach(list -> list.sort(SEG_ORDER));
        return openPerDay;
    }

    private OpenStatus computeOpenStatus(
            Long storeId,
            EnumMap<DayOfWeek, List<Segment>> openPerDay,
            LocalDate today,
            DayOfWeek dow,
            LocalTime nowLt
    ) {
        // 1) 오늘 구간 만 사용
        // 구간이 없거나(등록x), 구긴이 1개 있거나(10:00 ~ 19:00),
        // 구간이 2개 있을 수 있다.(22:00 ~ 05:00) -> (22:00 ~ 00:00, 00:00 ~ 05:00)
        List<TimeRange> todayRanges = toRanges(openPerDay.getOrDefault(dow, List.of()));

        // 2) 상태 계산
        boolean openNow = false;
        LocalDateTime closesAt = null;
        LocalDateTime opensAt = null;

        // 현재 요일(월~일) 로 쪼갠 TimeRange 기반으로 영업 상태, 오픈일, 마감일을 체크한다.
        for (TimeRange tr : todayRanges) {
            // 현재 시간과, 가게 영업 시간을 비교해서, 영업중인지, 영업종료인지 판단합니다.
            boolean isOpenNow = !nowLt.isBefore(tr.start()) && nowLt.isBefore(endAs24(tr.end()));
            if (isOpenNow) {
                openNow = true;
                closesAt = LocalDateTime.of(today, displayEnd(tr.end())); // 영업중이라면, 마감 시간을 그대로 활용합니다.
                break;
            }
            // TimeRange 는 자정 기준으로 나뉘어져 있어서, 이전날의 영업일이
            // 예) 일요일 22:00 ~ 05:00 이고, 오늘이 월요일이라면, 오늘 날짜에 00:00 ~ 05:00 이 유효하다.
            // 만약 현재 운영중이 아니고 오픈 시각보다 이전이라면 오픈 시간을 입력받는다.
            boolean isNowBeforeStart = nowLt.isBefore(tr.start());
            boolean nextOpeningNotDecided = (opensAt == null);
            boolean shouldRecordNextOpening = isNowBeforeStart && nextOpeningNotDecided;
            if (shouldRecordNextOpening) {
                opensAt = LocalDateTime.of(today, tr.start());
            }
        }

        // (선택) 자정 연속 보정: 오늘이 24:00에 ‘끊겨’ 보이면 내일 00:00부터 이어지면 끝까지 확장
        // 이전까진 자정 에 마감이라고 처리됨 22:00 ~ 00:00
        if (openNow && closesAt.toLocalTime().equals(LocalTime.MIDNIGHT)) { // closesAt이 00시이면, 연속을 체크한다
            LocalDate tmr = today.plusDays(1);
            DayOfWeek tmrDow = dow.plus(1);

            if (!holidayRepository.existsByStore_IdAndDate(storeId, tmr)) {
                List<TimeRange> tmrRanges = toRanges(openPerDay.getOrDefault(tmrDow, List.of()));

                if (!tmrRanges.isEmpty() && tmrRanges.get(0).start().equals(LocalTime.MIDNIGHT)) {
                    closesAt = LocalDateTime.of(tmr, displayEnd(tmrRanges.get(0).end()));
                }
            }
        }

        return new OpenStatus(openNow,opensAt,closesAt);
    }

    private String makeStatusText(boolean openNow, LocalDateTime opensAt, LocalDateTime closesAt, LocalDate today) {
        // 영업 중이라면 closeAt 으로 영업 종료 시간 표현
        if (openNow) {
            String closeHour = HHMM.format(closesAt.toLocalTime());
            return "영업 중 · " + closeHour + " 영업 종료";
        }
        // 영업 중이지 않은데 openAt 이 존재한다면
        if (opensAt != null) {
            String openHour = HHMM.format(opensAt.toLocalTime());
            return opensAt.toLocalDate().isEqual(today)
                    ? "영업 종료 · " + openHour + " 오픈"
                    : "영업 종료 · 내일 " + openHour + " 오픈";
        }
        // 영업 중이 아닌데, 오픈 시간이 null 이라면
        return "영업 종료";
    }

    private record OpenStatus(
            boolean openNow,
            LocalDateTime openAt,
            LocalDateTime closeAt
    ){}

}
