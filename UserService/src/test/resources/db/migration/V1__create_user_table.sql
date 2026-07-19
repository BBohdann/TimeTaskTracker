CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nickname VARCHAR(100) NOT NULL,
    login VARCHAR(30) NOT NULL UNIQUE,
    password VARCHAR(500) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    last_updated_time DATE NOT NULL,
    created_time DATE NOT NULL,
    CONSTRAINT check_login_length CHECK (LENGTH(login) >= 4 AND LENGTH(login) <= 30)
);