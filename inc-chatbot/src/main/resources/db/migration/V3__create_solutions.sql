CREATE TABLE IF NOT EXISTS solutions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    incident_id BIGINT NOT NULL,
    solution_type ENUM('SQL','STEPS','DOC') NOT NULL DEFAULT 'STEPS',
    solution_text LONGTEXT,
    file_path VARCHAR(500),
    version INT NOT NULL DEFAULT 1,
    created_by VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    INDEX idx_solutions_incident_id (incident_id),
    INDEX idx_solutions_is_active (is_active),
    CONSTRAINT fk_solutions_incident FOREIGN KEY (incident_id) REFERENCES incidents(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
