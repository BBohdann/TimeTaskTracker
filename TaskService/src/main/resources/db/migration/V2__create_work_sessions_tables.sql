CREATE TABLE work_sessions (
    id SERIAL PRIMARY KEY,
    user_id INT NOT NULL,
    task_id INT NOT NULL,
    subtask_id INT,
    status VARCHAR(20) NOT NULL,

    FOREIGN KEY (task_id) REFERENCES tasks(id) ON DELETE CASCADE,
    FOREIGN KEY (subtask_id) REFERENCES subtasks(id) ON DELETE CASCADE
);


CREATE TABLE work_session_intervals (
    id SERIAL PRIMARY KEY,
    work_session_id INT NOT NULL,

    started_at TIMESTAMP NOT NULL,
    finished_at TIMESTAMP,
    duration INT DEFAULT 0,

    FOREIGN KEY (work_session_id) REFERENCES work_sessions(id) ON DELETE CASCADE
);


CREATE INDEX idx_work_sessions_task_id
    ON work_sessions(task_id);

CREATE INDEX idx_work_sessions_user_id
    ON work_sessions(user_id);

CREATE INDEX idx_work_session_intervals_session_id
    ON work_session_intervals(work_session_id);