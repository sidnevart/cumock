CREATE TABLE pvp_contest (
                             id SERIAL PRIMARY KEY,
                             user1_id BIGINT NOT NULL,
                             user2_id BIGINT NOT NULL,
                             problem1_id BIGINT NOT NULL,
                             problem2_id BIGINT NOT NULL,
                             start_time TIMESTAMP NOT NULL,
                             end_time TIMESTAMP,
                             status VARCHAR(20) NOT NULL,
                             winner_id BIGINT,
                             challenge_expires_at TIMESTAMP,
                             created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);