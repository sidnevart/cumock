CREATE TABLE submissions (
                             id BIGSERIAL PRIMARY KEY,
                             user_id BIGINT NOT NULL,
                             problem_id BIGINT NOT NULL,
                             code TEXT NOT NULL,
                             language VARCHAR(50) NOT NULL,
                             passed INTEGER NOT NULL,
                             failed INTEGER NOT NULL,
                             total INTEGER NOT NULL,
                             verdict VARCHAR(100) NOT NULL,
                             created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                             attempt INTEGER NOT NULL DEFAULT 1,
                             pvp BOOLEAN NOT NULL DEFAULT FALSE,
                             contest_id BIGINT
);
