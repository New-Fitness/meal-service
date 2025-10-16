--liquibase formatted sql

--changeset tesinitsyn:006
CREATE TABLE IF NOT EXISTS meal_feedback
(
    id         UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id    UUID NOT NULL,
    chat_id    UUID REFERENCES chat_memory (id) ON DELETE SET NULL,
    meal_id    UUID REFERENCES meal (id) ON DELETE SET NULL,
    rating     SMALLINT CHECK (rating BETWEEN 1 AND 5),
    comment    TEXT,
    created_at TIMESTAMP        DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_feedback_user_id ON meal_feedback (user_id);
CREATE INDEX IF NOT EXISTS idx_feedback_meal_id ON meal_feedback (meal_id);
--rollback DROP TABLE IF EXISTS meal_feedback;
