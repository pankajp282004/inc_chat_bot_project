CREATE TABLE IF NOT EXISTS incident_embeddings (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    incident_id BIGINT NOT NULL UNIQUE,
    embedding_vector LONGTEXT,
    model_name VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_embeddings_incident_id (incident_id),
    CONSTRAINT fk_embeddings_incident FOREIGN KEY (incident_id) REFERENCES incidents(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
