--liquibase formatted sql

--changeset tesinitsyn:007
CREATE TABLE IF NOT EXISTS model_run
(
    id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id       UUID,
    model_name    VARCHAR(100),
    prompt        TEXT,
    response      TEXT,
    latency_ms    INTEGER,
    tokens_input  INTEGER,
    tokens_output INTEGER,
    created_at    TIMESTAMP        DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_model_run_user_id ON model_run (user_id);
CREATE INDEX IF NOT EXISTS idx_model_run_created_at ON model_run (created_at DESC);
--rollback DROP TABLE IF EXISTS model_run;

--changeset tesinitsyn:008
CREATE TABLE IF NOT EXISTS embedding_source
(
    id         UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    title      TEXT NOT NULL,
    content    TEXT NOT NULL,
    embedding  VECTOR(768),
    metadata   JSONB            DEFAULT '{}'::jsonb,
    created_at TIMESTAMP        DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_embedding_source_embedding
    ON embedding_source USING ivfflat (embedding vector_l2_ops) WITH (lists = 100);
--rollback DROP TABLE IF EXISTS embedding_source;
