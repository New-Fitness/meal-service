--liquibase formatted sql

--changeset tesinitsyn:003
CREATE TABLE IF NOT EXISTS meal_item
(
    id        UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    meal_id   UUID         NOT NULL REFERENCES meal (id) ON DELETE CASCADE,
    name      VARCHAR(255) NOT NULL,
    weight_g  NUMERIC(10, 2) CHECK (weight_g > 0),
    calories  INTEGER CHECK (calories >= 0),
    protein_g NUMERIC(6, 2) CHECK (protein_g >= 0),
    fat_g     NUMERIC(6, 2) CHECK (fat_g >= 0),
    carbs_g   NUMERIC(6, 2) CHECK (carbs_g >= 0)
);

CREATE INDEX IF NOT EXISTS idx_meal_item_meal_id ON meal_item (meal_id);
--rollback DROP TABLE IF EXISTS meal_item;

--changeset tesinitsyn:004
CREATE TABLE IF NOT EXISTS ingredient_reference
(
    id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name          VARCHAR(255) UNIQUE NOT NULL,
    calories_100g INTEGER CHECK (calories_100g >= 0),
    protein_100g  NUMERIC(6, 2) CHECK (protein_100g >= 0),
    fat_100g      NUMERIC(6, 2) CHECK (fat_100g >= 0),
    carbs_100g    NUMERIC(6, 2) CHECK (carbs_100g >= 0)
);
--rollback DROP TABLE IF EXISTS ingredient_reference;