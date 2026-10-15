package com.inc.chatbot.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "incident_embeddings")
public class IncidentEmbedding {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "incident_id", nullable = false, unique = true)
    private Incident incident;

    @Column(name = "embedding_vector", columnDefinition = "LONGTEXT")
    private String embeddingVector;

    @Column(name = "model_name", length = 100)
    private String modelName;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    // ---- Constructors ----
    public IncidentEmbedding() {}

    // ---- Getters ----
    public Long getId() { return id; }
    public Incident getIncident() { return incident; }
    public String getEmbeddingVector() { return embeddingVector; }
    public String getModelName() { return modelName; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    // ---- Setters ----
    public void setId(Long id) { this.id = id; }
    public void setIncident(Incident incident) { this.incident = incident; }
    public void setEmbeddingVector(String embeddingVector) { this.embeddingVector = embeddingVector; }
    public void setModelName(String modelName) { this.modelName = modelName; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return "IncidentEmbedding{id=" + id + "}";
    }
}
