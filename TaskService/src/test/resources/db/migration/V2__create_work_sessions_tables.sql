CREATE TABLE work_sessions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    task_id BIGINT NOT NULL,
    subtask_id BIGINT,
    status VARCHAR(20) NOT NULL,

    FOREIGN KEY (task_id) REFERENCES tasks(id) ON DELETE CASCADE,
    FOREIGN KEY (subtask_id) REFERENCES subtasks(id) ON DELETE CASCADE
);

CREATE TABLE work_session_intervals (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    work_session_id BIGINT NOT NULL,

    started_at TIMESTAMP NOT NULL,
    finished_at TIMESTAMP,
    duration INT DEFAULT 0,

    FOREIGN KEY (work_session_id) REFERENCES work_sessions(id) ON DELETE CASCADE
);