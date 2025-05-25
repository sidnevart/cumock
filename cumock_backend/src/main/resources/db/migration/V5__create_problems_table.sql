CREATE TABLE Problems (
                          id BIGSERIAL PRIMARY KEY,
                          title VARCHAR(255) NOT NULL,
                          description TEXT NOT NULL,
                          difficulty VARCHAR(20) NOT NULL
);
