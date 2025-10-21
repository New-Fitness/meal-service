--liquibase formatted sql

--changeset tesinitsyn:002
CREATE TABLE IF NOT EXISTS meal
(
    id             UUID PRIMARY KEY      DEFAULT gen_random_uuid(),
    user_id        UUID         NOT NULL,
    name           VARCHAR(255) NOT NULL CHECK (char_length(name) > 0),
    description    TEXT,
    total_calories INTEGER CHECK (total_calories >= 0),
    meal_time      TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    source         VARCHAR(20)           DEFAULT 'manual' CHECK (source IN ('manual', 'photo', 'ai')),
    created_at     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_meal_user_id ON meal (user_id);
CREATE INDEX IF NOT EXISTS idx_meal_meal_time ON meal (meal_time);
--roll