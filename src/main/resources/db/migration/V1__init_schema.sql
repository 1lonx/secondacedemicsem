CREATE TABLE IF NOT EXISTS tasks (
                                     id BIGSERIAL PRIMARY KEY,
                                     title VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    completed BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL,
    last_modified_at TIMESTAMP,
    due_date DATE,
    priority VARCHAR(10) CHECK (priority IN ('LOW', 'MEDIUM', 'HIGH')),
    tags TEXT
    );

CREATE TABLE IF NOT EXISTS task_attachments (
                                                id BIGSERIAL PRIMARY KEY,
                                                task_id BIGINT NOT NULL,
                                                file_name VARCHAR(255) NOT NULL,
    stored_file_name VARCHAR(255) NOT NULL UNIQUE,
    content_type VARCHAR(100),
    size BIGINT NOT NULL,
    uploaded_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_task_attachment FOREIGN KEY (task_id) REFERENCES tasks(id) ON DELETE CASCADE
    );

CREATE INDEX idx_attachments_task_id ON task_attachments(task_id);