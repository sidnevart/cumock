-- Создаём базовую таблицу пользователей
CREATE TABLE users (
                       id BIGSERIAL PRIMARY KEY,
                       username VARCHAR(50) NOT NULL UNIQUE,
                       created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT now()
);

