package com.appcenter.monolithicwnt.store.util;

import com.appcenter.monolithicwnt.global.exception.BusinessException;
import com.appcenter.monolithicwnt.global.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.swing.text.Segment;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;

@Component
@Slf4j
public class TimeSegments {

    /** 주간 영업(원본)**/
    public record Input(DayOfWeek dow, LocalTime start, LocalTime end, boolean nextDay) {}

    /** 분할/정규화 이후 세그먼트 **/
    public record Segment(DayOfWeek dow, LocalTime start, LocalTime end) {}

    /** ── 보조: Segment → 오늘용 TimeRange 변환만 남김 (휴게 차감 로직 삭제)**/
    public record TimeRange(LocalTime start, LocalTime end) {}

    /** --- 시작 시간을 수치로 계산 --- **/
    public static int startMin(LocalTime t) { return t.getHour() * 60 + t.getMinute(); }
    /** --- 끝 시간을 수치로 계산: 자정은 00:00 시 이므로, 24*60으로 자정을 표현, 자정이 아니라면 startMin 로직과 동일 **/
    public static int endMin(LocalTime t)   { return t.equals(LocalTime.MIDNIGHT) ? 24 * 60 : startMin(t); }
    /** --- 영업 상태를(영업중, 영업마감) 판단하기 위해, 자정 시간을 00:00 이 아닌 23:59:59 .... 으로 나타냄 : */
    public static LocalTime endAs24(LocalTime end) {
        return end.equals(LocalTime.MIDNIGHT) ? LocalTime.of(23,59,59, 999_999_999) : end;
    }
    public static LocalTime displayEnd(LocalTime end) { return end; }
    /** 같은 날 기준 시작 -> 종류 순 정렬(00:00 종료는 24:00 취급)**/
    public static final Comparator<TimeSegments.Segment> SEG_ORDER =
            Comparator.comparing((TimeSegments.Segment s) -> startMin(s.start()))
                    .thenComparing(s -> endMin(s.end()));

    /** nextDay=false 라면 -> start < end, nextDay=true 라면 -> start > end */
    public void guard(Input in) {
        if (!in.nextDay && !in.start.isBefore(in.end)) throw new BusinessException(ErrorCode.STORE_OPEN_CLOSE_INVALID);
        if ( in.nextDay && !in.start.isAfter(in.end))  throw new BusinessException(ErrorCode.STORE_OPEN_CLOSE_INVALID);
    }

    /** BusinessHour 입력 1건을 자정 기준으로 1~2 개의 세그먼트로 분할 **/
    public List<Segment> split(Input in) {
        LocalTime s = in.start;
        LocalTime e = in.end;
        DayOfWeek dow = in.dow;
        boolean nextDay = in.nextDay;

        // start 와 end 시간이 같을 때
        if (s.equals(e)) return List.of(); // 0분 금지

        // nextDay 가 false 이고, e가 s보다 크다면 -> false(자정 나눌 필요 없음)
        boolean crosses = nextDay || e.isBefore(s);
        if (!crosses) return List.of(new Segment(dow, s, e));
        
        // 자정을 나눠야 할 때 활용할 리스트
        List<Segment> out = new ArrayList<>(2);
        // 자정으로 나눈 시간, 당일 - 오픈 - 자정
        if (!s.equals(LocalTime.MIDNIGHT)) out.add(new Segment(dow, s, LocalTime.MIDNIGHT));
        // 자정으로 나눠 다음날 범위를 나타내기 위함
        DayOfWeek next = dow.plus(1);
        // 자정으로 나눈 시간 내일 - 자정 - 마감
        if (!e.equals(LocalTime.MIDNIGHT)) out.add(new Segment(next, LocalTime.MIDNIGHT, e));
        return out;
    }

    /** Segments 를 TimeRange 로 변환 및 정렬 해서 리턴합니다. **/
    public static List<TimeRange> toRanges(List<TimeSegments.Segment> segs){
        if(segs.isEmpty()) return List.of();

        List<TimeRange> res = new ArrayList<>(segs.size());

        for (var seg : segs) res.add(new TimeRange(seg.start(), seg.end()));

        res.sort(Comparator
                .comparing((TimeRange r) -> startMin(r.start()))
                .thenComparing(r -> endMin(r.end())));

        return res;
    }

    /** 요일별로 분해 후 겹침 금지(맞닿음 허용) **/
    public void assertNoOverlapByDow(List<Input> inputs) {
        EnumMap<DayOfWeek, List<Segment>> perDay = new EnumMap<>(DayOfWeek.class);
        for (Input in : inputs) {
            guard(in);
            // 교차 자정이면 [당일 start ~ 24:00 ) + [익일 00:00 ~ end) 로 분할
            List<Segment> pieces = split(in);
            for (Segment Segment : pieces) {
                log.info("input 에 대한 Segment :{}", Segment.toString());
                perDay.computeIfAbsent(Segment.dow, k -> new ArrayList<>()).add(Segment);
            }
        }
        perDay.values().forEach(this::assertNoOverlapWithinDay);
    }

    /** --- 겹침 검사, 자정 고려 분리한 시간대 중 겹치는 내용을 제거합니다  **/
    private void assertNoOverlapWithinDay(List<Segment> Segments) {
        // 시작 시간 오름차순 -> 종료 시각 오름차순으로 안정 정렬
        Segments.sort(Comparator
                .comparing((Segment s) -> startMin(s.start))
                .thenComparing(s -> endMin(s.end)));

        // 직전 구간의 종료 분 을 기억(-1은 아직 없다는 뜻)
        int prevEnd = -1;

        for (Segment Segment : Segments) {
            int s = startMin(Segment.start);
            int e = endMin(Segment.end);

            // 겹침(overlap) 판정: 현재 시작 s 가 이전 종료 preEnd 보다 작으면
            // [s,e) 가 이전 구간과 겹친다 (맞닿음 s == prevEnd 는 허용)
            if (prevEnd >= 0 && s < prevEnd)
                throw new BusinessException(ErrorCode.STORE_OPEN_CLOSE_INVALID);

            // 종료 최댓값 갱신 (이후 구간과의 비교 기준)
            if (e > prevEnd)
                prevEnd = e;
        }
    }


}
