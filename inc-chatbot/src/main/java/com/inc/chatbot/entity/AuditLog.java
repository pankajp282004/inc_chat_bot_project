package com.inc.chatbot.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "audit_logs")
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "action", length = 100)
    private String action;

    @Column(name = "entity_type", length = 50)
    private String entityType;

    @Column(name = "entity_id")
    private Long entityId;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "ip_address", length = 50)
    private String ipAddress;

    // ---- Constructors ----
    public AuditLog() {}

    // ---- Getters ----
    public Long getId() { return id; }
    public User getUser() { return user; }
    public String getAction() { return action; }
    public String getEntityType() { return entityType; }
    public Long getEntityId() { return entityId; }
    public String getDescription() { return description; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public String getIpAddress() { return ipAddress; }

    // ---- Setters ----
    public void setId(Long id) { this.id = id; }
    public void setUser(User user) { this.user = user; }
    public void setAction(String action) { this.action = action; }
    public void setEntityType(String entityType) { this.entityType = entityType; }
    public void setEntityId(Long entityId) { this.entityId = entityId; }
    public void setDescription(String description) { this.description = description; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }

    // ---- Builder ----
    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private final AuditLog log = new AuditLog();

        public Builder user(User v)          { log.user = v; return this; }
        public Builder action(String v)      { log.action = v; return this; }
        public Builder entityType(String v)  { log.entityType = v; return this; }
        public Builder entityId(Long v)      { log.entityId = v; return this; }
        public Builder description(String v) { log.description = v; return this; }
        public Builder ipAddress(String v)   { log.ipAddress = v; return this; }
        public AuditLog build()              { return log; }
    }

    @Override
    public String toString() {
        return "AuditLog{id=" + id + ", action='" + action + "'}";
    }
}
