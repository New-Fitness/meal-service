--liquibase formatted sql

--changeset timofey:001
CREATE TABLE IF NOT EXISTS meal  (
                      id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                      user_id UUID NOT NULL,
                      name VARCHAR(255) NOT NULL,
                      description TEXT,
                      calories INTEGER,
                      created_at TIMESTAMP DEFAULT NOW()
);

--rollback DROP TABLE meal;
