CREATE TABLE tasks (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    task_name VARCHAR(255) NOT NULL,
    description VARCHAR(2000),
    created_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP,
    time_to_spend INT,
    time_spent INT DEFAULT 0,
    is_complete BOOLEAN DEFAULT FALSE
);

CREATE TABLE subtasks (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    task_id INT NOT NULL,
    subtask_name VARCHAR(255) NOT NULL,
    description VARCHAR(2000),
    created_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP,
    time_to_spend INT,
    time_spent INT DEFAULT 0,
    is_complete BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (task_id) REFERENCES tasks(id) ON DELETE CASCADE
);