-- src/test/resources/data.sql
-- 깨끗하게 시작 (자식 → 부모 순서로 삭제)
DELETE
FROM break_hours;
DELETE
FROM business_hours;
DELETE
FROM special_days;
DELETE
FROM stores;
DELETE
FROM users;

-- =========================
-- users
-- =========================
INSERT INTO users (id, email, password, nickname, created_at)
VALUES (1, 'owner@example.com', 'test-password', '사장님',CURRENT_TIMESTAMP);

-- =========================
-- stores (Address 임베디드 + enum 컬럼)
-- status: SUSPENDED / slot_interval_times: THIRTY_MINUTES|SIXTY_MINUTES
-- =========================
INSERT INTO stores (id, name, phone, instagram,
                    full_address, latitude, longitude,
                    status, slot_interval_times, owner_id)
VALUES (1,
        '모노바버 숍',
        '01012345678',
        'monoshop_official',
        '서울특별시 중구 세종대로 110',
        37.5665, 126.9780,
        'SUSPENDED', 'SIXTY_MINUTES',
        1);

INSERT INTO stores (id, name, phone, instagram,
                    full_address, latitude, longitude,
                    status, slot_interval_times, owner_id)
VALUES (2,
        '완벽한 네일아트',
        '01012345679',
        'what_nail_to_do_official',
        '서울특별시 강남구 레전드 110',
        37.5665, 111.1111,
        'SUSPENDED', 'SIXTY_MINUTES',
        1);

-- =========================
-- business_hours: 월~일 22:00 → 익일 05:00 (next_day_close = TRUE)
-- =========================
INSERT INTO business_hours (id, store_id, day_of_week, open_time, close_time, next_day_close)
VALUES (1, 1, 'MONDAY', '22:00:00', '05:00:00', false),
       (2, 1, 'TUESDAY', '22:00:00', '05:00:00', false),
       (3, 1, 'WEDNESDAY', '22:00:00', '05:00:00', false),
       (4, 1, 'THURSDAY', '22:00:00', '05:00:00', false),
       (5, 1, 'FRIDAY', '22:00:00', '05:00:00', false),
       (6, 1, 'SATURDAY', '22:00:00', '05:00:00', false),
       (7, 1, 'SUNDAY', '22:00:00', '05:00:00', false);

INSERT INTO business_hours (id, store_id, day_of_week, open_time, close_time, next_day_close)
VALUES (8, 2, 'MONDAY', '10:00:00', '19:00:00', false),
       (9, 2, 'TUESDAY', '10:00:00', '19:00:00', false),
       (10, 2, 'WEDNESDAY', '10:00:00', '19:00:00', false),
       (11, 2, 'THURSDAY', '10:00:00', '19:00:00', false),
       (12, 2, 'FRIDAY', '10:00:00', '19:00:00', false),
       (13, 2, 'SATURDAY', '10:00:00', '19:00:00', false),
       (14, 2, 'SUNDAY', '10:00:00', '19:00:00', false);

-- =========================
-- break_hours: 매일 01:00~01:30 (자정 안넘김, next_day_close = FALSE)
--   * 새벽 휴게는 그 요일의 01:00 기준으로 저장 (예: 화요일 01:00)
-- =========================
INSERT INTO break_hours (id, store_id, day_of_week, start_time, end_time, next_day_close)
VALUES (1, 1, 'MONDAY', '01:00:00', '01:30:00', FALSE),
       (2, 1, 'TUESDAY', '01:00:00', '01:30:00', FALSE),
       (3, 1, 'WEDNESDAY', '01:00:00', '01:30:00', FALSE),
       (4, 1, 'THURSDAY', '01:00:00', '01:30:00', FALSE),
       (5, 1, 'FRIDAY', '01:00:00', '01:30:00', FALSE),
       (6, 1, 'SATURDAY', '01:00:00', '01:30:00', FALSE),
       (7, 1, 'SUNDAY', '01:00:00', '01:30:00', FALSE);

INSERT INTO break_hours (id, store_id, day_of_week, start_time, end_time, next_day_close)
VALUES (8, 2, 'MONDAY', '12:00:00', '13:00:00', FALSE),
       (9, 2, 'TUESDAY', '12:00:00', '13:00:00', FALSE),
       (10, 2, 'WEDNESDAY', '12:00:00', '13:00:00', FALSE),
       (11, 2, 'THURSDAY', '12:00:00', '13:00:00', FALSE),
       (12, 2, 'FRIDAY', '12:00:00', '13:00:00', FALSE),
       (13, 2, 'SATURDAY', '12:00:00', '13:00:00', FALSE),
       (14, 2, 'SUNDAY', '12:00:00', '13:00:00', FALSE);

-- =========================
-- special_days(휴무일) 예시
-- =========================
INSERT INTO special_days (id, store_id, date)
VALUES (1, 1, '2025-09-02'),
       (2, 1, '2025-10-29'),
       (3, 1, '2025-11-30'),
       (4, 1, '2025-12-24');

INSERT INTO special_days (id, store_id, date)
VALUES (5, 2, '2025-09-30'),
       (6, 2, '2025-10-29'),
       (7, 2, '2025-11-30'),
       (8, 2, '2025-12-24');