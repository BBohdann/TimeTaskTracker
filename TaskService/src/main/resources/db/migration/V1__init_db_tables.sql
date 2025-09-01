CREATE TABLE tasks (
    id SERIAL PRIMARY KEY,
    user_id INT NOT NULL,
    task_name VARCHAR(255) NOT NULL,
    description VARCHAR(2000),
    created_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP,
    time_to_spend INT,
    time_spent INT DEFAULT '0',
    is_complete BOOLEAN DEFAULT FALSE
);


CREATE TABLE subtasks (
    id SERIAL PRIMARY KEY,
    task_id INT NOT NULL,
    subtask_name VARCHAR(255) NOT NULL,
    description VARCHAR(2000),
    created_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP,
    time_to_spend INT,
    time_spent INT DEFAULT '0',
    is_complete BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (task_id) REFERENCES tasks(id) ON DELETE CASCADE
);

CREATE OR REPLACE FUNCTION update_is_complete_task() RETURNS TRIGGER AS $$
BEGIN
    IF NEW.time_spent >= NEW.time_to_spend THEN
        NEW.is_complete := TRUE;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER set_is_complete_task
BEFORE INSERT OR UPDATE ON tasks
FOR EACH ROW
EXECUTE FUNCTION update_is_complete_task();

CREATE OR REPLACE FUNCTION update_is_complete_subtask() RETURNS TRIGGER AS $$
BEGIN
    IF NEW.time_spent >= NEW.time_to_spend THEN
        NEW.is_complete := TRUE;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER set_is_complete_subtask
BEFORE INSERT OR UPDATE ON subtasks
FOR EACH ROW
EXECUTE FUNCTION update_is_complete_subtask();