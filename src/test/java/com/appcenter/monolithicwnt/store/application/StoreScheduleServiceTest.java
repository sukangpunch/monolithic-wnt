package com.appcenter.monolithicwnt.store.application;

import com.appcenter.monolithicwnt.global.exception.BusinessException;
import com.appcenter.monolithicwnt.store.domain.*;
import com.appcenter.monolithicwnt.store.dto.request.*;
import com.appcenter.monolithicwnt.store.infrastructure.BreakHourRepository;
import com.appcenter.monolithicwnt.store.infrastructure.BusinessHourRepository;
import com.appcenter.monolithicwnt.store.infrastructure.HolidayRepository;
import com.appcenter.monolithicwnt.store.infrastructure.StoreRepository;
import com.appcenter.monolithicwnt.user.domain.User;
import com.appcenter.monolithicwnt.user.infrastructure.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureTestDatabase
@Slf4j
class StoreScheduleServiceTest {

    @Autowired
    private StoreScheduleService storeScheduleService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private BusinessHourRepository businessHourRepository;

    @Autowired
    private BreakHourRepository breakHourRepository;

    @Autowired
    private HolidayRepository holidayRepository;

    Store testStore;
    User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User("test@gmail.com", "testUser", "test@test.com");
        userRepository.save(testUser);

        testStore = new Store("test", "01012345678", "qwdasw", 60, new Address("test", 0.1, 0.1), testUser);
        storeRepository.save(testStore);
    }


    @Test
    void 비즈니스_시간대를_입력받는다() {
        // given
        Long storeId = testStore.getId();
        List<BusinessHourDto> businessHourDtos = List.of(
                new BusinessHourDto(DayOfWeek.MONDAY, LocalTime.parse("09:00"), LocalTime.parse("18:00"), false),
                new BusinessHourDto(DayOfWeek.TUESDAY, LocalTime.parse("09:00"), LocalTime.parse("18:00"), false),
                new BusinessHourDto(DayOfWeek.WEDNESDAY, LocalTime.parse("09:00"), LocalTime.parse("18:00"), false),
                new BusinessHourDto(DayOfWeek.THURSDAY, LocalTime.parse("09:00"), LocalTime.parse("18:00"), false),
                new BusinessHourDto(DayOfWeek.FRIDAY, LocalTime.parse("09:00"), LocalTime.parse("18:00"), false),
                new BusinessHourDto(DayOfWeek.SATURDAY, LocalTime.parse("09:00"), LocalTime.parse("18:00"), false),
                new BusinessHourDto(DayOfWeek.SUNDAY, LocalTime.parse("09:00"), LocalTime.parse("18:00"), false)
        );
        BusinessHourRequest businessHourRequest = new BusinessHourRequest(businessHourDtos);

        // when
        storeScheduleService.upsertBusinessHours(storeId, businessHourRequest);

        // then
        List<BusinessHour> storeBusinessHour = businessHourRepository.findByStore_IdOrderByDayOfWeekAsc(storeId);
        for (int i = 0; i < storeBusinessHour.size(); i++) {
            BusinessHour businessHour = storeBusinessHour.get(i);
            log.info("businessHour{}: {}", i, businessHour.getDayOfWeek());
            assertEquals(DayOfWeek.of(i + 1), businessHour.getDayOfWeek());
            assertEquals(LocalTime.of(9, 0), businessHour.getOpenTime());
        }
    }

    @Test
    void 비즈니스_시간대를_입력_받을_떄_새벽시간이면_next_day가_true여야한다() {
        // given
        Long storeId = testStore.getId();
        List<BusinessHourDto> businessHourDtos = List.of(
                new BusinessHourDto(DayOfWeek.MONDAY, LocalTime.parse("22:00"), LocalTime.parse("05:00"), true),
                new BusinessHourDto(DayOfWeek.TUESDAY, LocalTime.parse("22:00"), LocalTime.parse("05:00"), true),
                new BusinessHourDto(DayOfWeek.WEDNESDAY, LocalTime.parse("22:00"), LocalTime.parse("05:00"), true),
                new BusinessHourDto(DayOfWeek.THURSDAY, LocalTime.parse("22:00"), LocalTime.parse("05:00"), true),
                new BusinessHourDto(DayOfWeek.FRIDAY, LocalTime.parse("22:00"), LocalTime.parse("05:00"), true),
                new BusinessHourDto(DayOfWeek.SATURDAY, LocalTime.parse("22:00"), LocalTime.parse("05:00"), true),
                new BusinessHourDto(DayOfWeek.SUNDAY, LocalTime.parse("22:00"), LocalTime.parse("05:00"), true)
        );
        BusinessHourRequest businessHourRequest = new BusinessHourRequest(businessHourDtos);

        // when
        storeScheduleService.upsertBusinessHours(storeId, businessHourRequest);

        // then
        List<BusinessHour> storeBusinessHour = businessHourRepository.findByStore_IdOrderByDayOfWeekAsc(storeId);
        for (int i = 0; i < storeBusinessHour.size(); i++) {
            BusinessHour businessHour = storeBusinessHour.get(i);
            log.info("businessHour {} week: {}", i, businessHour.getDayOfWeek());
            log.info("businessHour {} openTime: {}", i, businessHour.getOpenTime());
            log.info("businessHour {} closeTime: {}", i, businessHour.getCloseTime());

            assertEquals(DayOfWeek.of(i + 1), businessHour.getDayOfWeek());
        }
    }

    @Test
    void 비즈니스_시간대를_입력_받을_떄_새벽시간일_때_next_day가_false면_예외를_발생한다() {
        // given
        Long storeId = testStore.getId();
        List<BusinessHourDto> businessHourDtos = List.of(
                new BusinessHourDto(DayOfWeek.MONDAY, LocalTime.parse("22:00"), LocalTime.parse("05:00"), false),
                new BusinessHourDto(DayOfWeek.TUESDAY, LocalTime.parse("22:00"), LocalTime.parse("05:00"), false),
                new BusinessHourDto(DayOfWeek.WEDNESDAY, LocalTime.parse("22:00"), LocalTime.parse("05:00"), false),
                new BusinessHourDto(DayOfWeek.THURSDAY, LocalTime.parse("22:00"), LocalTime.parse("05:00"), false),
                new BusinessHourDto(DayOfWeek.FRIDAY, LocalTime.parse("22:00"), LocalTime.parse("05:00"), false),
                new BusinessHourDto(DayOfWeek.SATURDAY, LocalTime.parse("22:00"), LocalTime.parse("05:00"), false),
                new BusinessHourDto(DayOfWeek.SUNDAY, LocalTime.parse("22:00"), LocalTime.parse("05:00"), false)
        );
        BusinessHourRequest businessHourRequest = new BusinessHourRequest(businessHourDtos);

        // when
        // then
        assertThrows(BusinessException.class, () -> storeScheduleService.upsertBusinessHours(storeId, businessHourRequest));
    }

    @Test
    void 브레이크_시간대를_입력_받는다() {
        // given
        Long storeId = testStore.getId();
        List<BreakHourDto> breakHourDtos = List.of(
                new BreakHourDto(DayOfWeek.MONDAY, LocalTime.parse("09:00"), LocalTime.parse("18:00"), false),
                new BreakHourDto(DayOfWeek.TUESDAY, LocalTime.parse("09:00"), LocalTime.parse("18:00"), false),
                new BreakHourDto(DayOfWeek.WEDNESDAY, LocalTime.parse("09:00"), LocalTime.parse("18:00"), false),
                new BreakHourDto(DayOfWeek.THURSDAY, LocalTime.parse("09:00"), LocalTime.parse("18:00"), false),
                new BreakHourDto(DayOfWeek.FRIDAY, LocalTime.parse("09:00"), LocalTime.parse("18:00"), false),
                new BreakHourDto(DayOfWeek.SATURDAY, LocalTime.parse("09:00"), LocalTime.parse("18:00"), false),
                new BreakHourDto(DayOfWeek.SUNDAY, LocalTime.parse("09:00"), LocalTime.parse("18:00"), false)
        );
        BreakHourRequest breakHourRequest = new BreakHourRequest(breakHourDtos);

        // when
        storeScheduleService.upsertBreakHours(storeId, breakHourRequest);

        // then
        List<BreakHour> breakHours = breakHourRepository.findByStore_IdOrderByDayOfWeekAsc(storeId);

        for (int i = 0; i < breakHours.size(); i++) {
            BreakHour breakHour = breakHours.get(i);
            log.info("breakHour {}: day : {}, startTime : {}, endTime : {}, storeId : {}", i, breakHour.getDayOfWeek(), breakHour.getStartTime(), breakHour.getEndTime(), breakHour.getStore().getId());
            assertEquals(DayOfWeek.of(i + 1), breakHour.getDayOfWeek());
            assertEquals(LocalTime.of(9, 0), breakHour.getStartTime());
        }

    }

    @Test
    void 브레이크_시간대가_새벽이라면_nextDay_가_true여야한다() {
        // given
        Long storeId = testStore.getId();
        List<BreakHourDto> breakHourDtos = List.of(
                new BreakHourDto(DayOfWeek.MONDAY, LocalTime.parse("22:00"), LocalTime.parse("05:00"), true),
                new BreakHourDto(DayOfWeek.TUESDAY, LocalTime.parse("22:00"), LocalTime.parse("05:00"), true),
                new BreakHourDto(DayOfWeek.WEDNESDAY, LocalTime.parse("22:00"), LocalTime.parse("05:00"), true),
                new BreakHourDto(DayOfWeek.THURSDAY, LocalTime.parse("22:00"), LocalTime.parse("05:00"), true),
                new BreakHourDto(DayOfWeek.FRIDAY, LocalTime.parse("22:00"), LocalTime.parse("05:00"), true),
                new BreakHourDto(DayOfWeek.SATURDAY, LocalTime.parse("22:00"), LocalTime.parse("05:00"), true),
                new BreakHourDto(DayOfWeek.SUNDAY, LocalTime.parse("22:00"), LocalTime.parse("05:00"), true)
        );
        BreakHourRequest breakHourRequest = new BreakHourRequest(breakHourDtos);

        // when
        storeScheduleService.upsertBreakHours(storeId, breakHourRequest);

        // then
        List<BreakHour> breakHours = breakHourRepository.findByStore_IdOrderByDayOfWeekAsc(storeId);

        for (int i = 0; i < breakHours.size(); i++) {
            BreakHour breakHour = breakHours.get(i);
            log.info("breakHour {}: day : {}, startTime : {}, endTime : {}, storeId : {}", i, breakHour.getDayOfWeek(), breakHour.getStartTime(), breakHour.getEndTime(), breakHour.getStore().getId());
            assertEquals(DayOfWeek.of(i + 1), breakHour.getDayOfWeek());
            assertEquals(LocalTime.of(9, 0), breakHour.getStartTime());
        }
    }

    @Test
    void 브레이크_시간대가_새벽일때_nextDay_가_false면_예외가_발생합니다() {
        // given
        Long storeId = testStore.getId();
        List<BreakHourDto> breakHourDtos = List.of(
                new BreakHourDto(DayOfWeek.MONDAY, LocalTime.parse("22:00"), LocalTime.parse("05:00"), false),
                new BreakHourDto(DayOfWeek.TUESDAY, LocalTime.parse("22:00"), LocalTime.parse("05:00"), false),
                new BreakHourDto(DayOfWeek.WEDNESDAY, LocalTime.parse("22:00"), LocalTime.parse("05:00"), false),
                new BreakHourDto(DayOfWeek.THURSDAY, LocalTime.parse("22:00"), LocalTime.parse("05:00"), false),
                new BreakHourDto(DayOfWeek.FRIDAY, LocalTime.parse("22:00"), LocalTime.parse("05:00"), false),
                new BreakHourDto(DayOfWeek.SATURDAY, LocalTime.parse("22:00"), LocalTime.parse("05:00"), false),
                new BreakHourDto(DayOfWeek.SUNDAY, LocalTime.parse("22:00"), LocalTime.parse("05:00"), false)
        );
        BreakHourRequest breakHourRequest = new BreakHourRequest(breakHourDtos);

        // when
        // then
        assertThrows(BusinessException.class, () -> storeScheduleService.upsertBreakHours(storeId, breakHourRequest));
    }

    @Test
    void 휴일을_입력받습니다(){
        // given
        Long storeId = testStore.getId();
        HolidayRequest holidayRequest = new HolidayRequest(List.of(
                LocalDate.of(2025, 9,30),
                LocalDate.of(2025, 10, 29),
                LocalDate.of(2025, 12, 24),
                LocalDate.of(2025, 11, 30)
        ));

        // when
        storeScheduleService.upsertHolidays(storeId, holidayRequest);

        // then
        List<Holiday> holidays = holidayRepository.findByStore_IdOrderByDateAsc(storeId);
        List<LocalDate> dates = holidays.stream().map(Holiday::getDate).toList();
        assertThat(dates).isSorted();

        for(int i=0; i<holidays.size(); i++){
            Holiday holiday = holidays.get(i);
            log.info("holiday {}: date : {}, storeId : {}", i, holiday.getDate(), holiday.getStore().getId());
        }
    }

    @Test
    void 만약_겹치는_business_시간이_있다면_예외를_던집니다(){
        // given
        Long storeId = testStore.getId();
        List<BusinessHourDto> businessHourDtos = List.of(
                new BusinessHourDto(DayOfWeek.MONDAY, LocalTime.parse("09:00"), LocalTime.parse("18:00"), false),
                new BusinessHourDto(DayOfWeek.MONDAY, LocalTime.parse("15:00"), LocalTime.parse("23:00"), false)
        );
        BusinessHourRequest businessHourRequest = new BusinessHourRequest(businessHourDtos);

        // when
        // then
        assertThrows(BusinessException.class, () -> storeScheduleService.upsertBusinessHours(storeId, businessHourRequest));
    }

    @Test
    void 만약_입력_요일이_겹치지_않더라도_새벽시간대에_겹치면_예외가_발생합니다(){
        // given
        Long storeId = testStore.getId();
        List<BusinessHourDto> businessHourDtos = List.of(
                new BusinessHourDto(DayOfWeek.MONDAY, LocalTime.parse("18:00"), LocalTime.parse("05:00"), true),
                new BusinessHourDto(DayOfWeek.TUESDAY, LocalTime.parse("04:00"), LocalTime.parse("10:00"), false)
        );
        BusinessHourRequest businessHourRequest = new BusinessHourRequest(businessHourDtos);

        // when
        // then
        assertThrows(BusinessException.class, () -> storeScheduleService.upsertBusinessHours(storeId, businessHourRequest));
    }

}
