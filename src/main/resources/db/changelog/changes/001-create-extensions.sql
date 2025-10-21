--liquibase formatted sql

--changeset tesinitsyn:001
CREATE EXTENSION IF NOT EXISTS "pgcrypto";
CREATE EXTENSION IF NOT EXISTS "vector";
--rollback DROP EXTENSION IF EXISTS "vector"; DROP EXTENSION IF EXISTS "pgcrypto";