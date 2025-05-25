-- V2__add_auth_fields.sql
ALTER TABLE users
  ADD COLUMN email VARCHAR(255),
  ADD COLUMN password_hash VARCHAR(255),
  ADD COLUMN email_verified BOOLEAN DEFAULT false,
  ADD COLUMN verification_code VARCHAR(64),
  ADD COLUMN telegram_id BIGINT;

ALTER TABLE users
    ADD CONSTRAINT uc_users_email UNIQUE(email),
  ADD CONSTRAINT uc_users_telegram UNIQUE(telegram_id);
