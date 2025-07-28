CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    nickname VARCHAR(255) NOT NULL UNIQUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- CREATE TYPE store_status AS ENUM ('OPEN', 'CLOSED', 'HOLIDAY', 'SUSPENDED');

CREATE TABLE IF NOT EXISTS stores (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    phone VARCHAR(255) NOT NULL,
    instagram VARCHAR(255) NOT NULL,

    -- Embedded Address 필드
    full_address VARCHAR(255) NOT NULL,
    latitude DOUBLE PRECISION NOT NULL,
    longitude DOUBLE PRECISION NOT NULL,
    status VARCHAR(255) NOT NULL,

    -- 연관관계 (owner)
    owner_id BIGINT,
    CONSTRAINT fk_owner FOREIGN KEY (owner_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS menus (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    price INTEGER NOT NULL,
    representative BOOLEAN NOT NULL,
    store_id BIGINT,
    CONSTRAINT fk_menu_store FOREIGN KEY (store_id) REFERENCES stores(id)
);

-- menu_details 테이블 (ElementCollection)
CREATE TABLE IF NOT EXISTS menu_details (
    menu_id BIGINT NOT NULL,
    detail VARCHAR(50) NOT NULL,
    CONSTRAINT fk_menu_details_menu FOREIGN KEY (menu_id) REFERENCES menus(id) ON DELETE CASCADE
);