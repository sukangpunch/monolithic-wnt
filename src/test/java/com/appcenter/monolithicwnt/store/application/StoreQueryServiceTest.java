package com.appcenter.monolithicwnt.store.application;

import com.appcenter.monolithicwnt.store.dto.response.StoreHeaderResponse;
import com.appcenter.monolithicwnt.store.infrastructure.BreakHourRepository;
import com.appcenter.monolithicwnt.store.infrastructure.BusinessHourRepository;
import com.appcenter.monolithicwnt.store.infrastructure.HolidayRepository;
import com.appcenter.monolithicwnt.store.infrastructure.StoreRepository;
import com.appcenter.monolithicwnt.user.infrastructure.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureTestDatabase
@Slf4j
@Sql(scripts = "classpath:sql/store_data.sql")
class StoreQueryServiceTest {

    @Autowired
    private StoreQueryService storeQueryService;

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

    @Test
    void 스토어_페이지의_헤더를_조회합니다_야간(){
        // given
        Long storeId = 1L;

        // when
        StoreHeaderResponse storeHeaderResponse = storeQueryService.getStoreHeader(storeId);
        log.info("statusText 결과 : {}",storeHeaderResponse.statusText());

        // then
        assertThat(storeHeaderResponse).isNotNull();
        assertThat(storeHeaderResponse.name()).isEqualTo("모노바버 숍");
        assertThat(storeHeaderResponse.phone()).isEqualTo("01012345678");
        assertThat(storeHeaderResponse.instagram()).isEqualTo("monoshop_official");
        assertThat(storeHeaderResponse.address()).isEqualTo("서울특별시 중구 세종대로 110");
        assertThat(storeHeaderResponse.openNow()).isTrue();
    }

    @Test
    void 스토어_페이지의_헤더를_조회합니다_주간(){
        // given
        Long storeId = 2L;

        // when
        StoreHeaderResponse storeHeaderResponse = storeQueryService.getStoreHeader(storeId);

        // then
        assertThat(storeHeaderResponse).isNotNull();
        assertThat(storeHeaderResponse.name()).isEqualTo("완벽한 네일아트");
        assertThat(storeHeaderResponse.phone()).isEqualTo("01012345679");
        assertThat(storeHeaderResponse.instagram()).isEqualTo("what_nail_to_do_official");
        assertThat(storeHeaderResponse.address()).isEqualTo("서울특별시 강남구 레전드 110");
        assertThat(storeHeaderResponse.openNow()).isTrue();
    }



}