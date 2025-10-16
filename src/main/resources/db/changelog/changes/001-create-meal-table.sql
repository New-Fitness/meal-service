--liquibase formatted sql
--changeset tesinitsyn:001

CREATE EXTENSION IF NOT EXISTS "pgcrypto";

CREATE TABLE IF NOT EXISTS meal (
                                    id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                    user_id      UUID        NOT NULL,
                                    name         VARCHAR(255) NOT NULL CHECK (char_length(name) > 0),
                                    description  TEXT,
                                    calories     INTEGER     CHECK (calories >= 0),
                                    created_at   TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_meal_user_id ON meal (user_id);
CREATE INDEX IF NOT EXISTS idx_meal_created_at ON meal (created_at DESC);

--rollback DROP TABLE IF EXISTS meal;
