-- Выполняется только при первом создании volume PostgreSQL.
-- Полная схема — в db/migrations/ (Flyway).

CREATE EXTENSION IF NOT EXISTS pgcrypto;
