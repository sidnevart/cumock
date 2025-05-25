CREATE TABLE pvp_contest (
                             id SERIAL PRIMARY KEY,
                             room_id VARCHAR(100) NOT NULL,
                             player1_id UUID NOT NULL,
                             player2_id UUID NOT NULL,
                             problem1_id BIGINT NOT NULL,
                             problem2_id BIGINT NOT NULL,
                             winner_id UUID,
                             player1_score INTEGER DEFAULT 0,
                             player2_score INTEGER DEFAULT 0,
                             player1_attempts INTEGER DEFAULT 0,
                             player2_attempts INTEGER DEFAULT 0,
                             started_at TIMESTAMP NOT NULL,
                             ended_at TIMESTAMP,
                             status VARCHAR(20) NOT NULL
);
