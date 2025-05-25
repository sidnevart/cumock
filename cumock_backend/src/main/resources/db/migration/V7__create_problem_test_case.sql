CREATE TABLE problem_test_case (
                                   id SERIAL PRIMARY KEY,
                                   problem_id BIGINT NOT NULL REFERENCES problems(id) ON DELETE CASCADE,
                                   input TEXT NOT NULL,
                                   expected_output TEXT NOT NULL,
                                   is_sample BOOLEAN DEFAULT FALSE,
                                   is_pvp BOOLEAN DEFAULT FALSE
);
