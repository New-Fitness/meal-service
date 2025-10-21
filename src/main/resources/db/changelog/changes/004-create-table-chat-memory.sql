--liquibase formatted sql

--changeset tesinitsyn:005
CREATE TABLE IF NOT EXISTS chat_memory
(
    id         UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id    UUID,
    role       VARCHAR(10) CHECK (role IN ('USER', 'AI')),
    content    TEXT CHECK (char_length(content) > 0),
    embedding  VECTOR(768),
    metadata   JSONB            DEFAULT '{}'::jsonb,
    created_at TIMESTAMP        DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_chat_memory_user_id ON chat_memory (user_id);
CREATE INDEX IF NOT EXISTS idx_chat_memory_created_at ON chat_memory (created_at DESC);
CREATE INDEX IF NOT EXISTS idx_chat_memory_embedding
    ON chat_memory USING ivfflat (embedding vector_l2_ops) WITH (lists = 100);
--rollback DROP TABLE IF EXISTS chat_memory;