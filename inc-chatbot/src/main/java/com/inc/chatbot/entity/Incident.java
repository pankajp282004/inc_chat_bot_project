package com.inc.chatbot.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "incidents")
public class Incident {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "incident_no", unique = true, nullable = false, length = 50)
    private String incidentNo;

    @Column(name = "task_no", length = 50)
    private String taskNo;

    @Column(name = "app", length = 100)
    private String app;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private IncidentStatus status = IncidentStatus.DRAFT;

    @Column(name = "last_comment", columnDefinition = "TEXT")
    private String lastComment;

    @Column(name = "comments", columnDefinition = "TEXT")
    private String comments;

    @Column(name = "rca", columnDefinition = "TEXT")
    private String rca;

    @Column(name = "plan_solution", columnDefinition = "TEXT")
    private String planSolution;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "is_approved")
    private Boolean isApproved = false;

    @Column(name = "created_by", length = 100)
    private String createdBy;

    @OneToMany(mappedBy = "incident", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Solution> solutions = new ArrayList<>();

    @OneToMany(mappedBy = "incident", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<IncidentAttachment> attachments = new ArrayList<>();

    @OneToOne(mappedBy = "incident", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private IncidentEmbedding embedding;

    public enum IncidentStatus {
        OPEN, PENDING_SCRIPT, CLOSED, IN_PROGRESS, DRAFT
    }

    // ---- Constructors ----
    public Incident() {}

    // ---- Getters ----
    public Long getId() { return id; }
    public String getIncidentNo() { return incidentNo; }
    public String getTaskNo() { return taskNo; }
    public String getApp() { return app; }
    public String getDescription() { return description; }
    public IncidentStatus getStatus() { return status; }
    public String getLastComment() { return lastComment; }
    public String getComments() { return comments; }
    public String getRca() { return rca; }
    public String getPlanSolution() { return planSolution; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public Boolean getIsApproved() { return isApproved; }
    public String getCreatedBy() { return createdBy; }
    public List<Solution> getSolutions() { return solutions; }
    public List<IncidentAttachment> getAttachments() { return attachments; }
    public IncidentEmbedding getEmbedding() { return embedding; }

    // ---- Setters ----
    public void setId(Long id) { this.id = id; }
    public void setIncidentNo(String incidentNo) { this.incidentNo = incidentNo; }
    public void setTaskNo(String taskNo) { this.taskNo = taskNo; }
    public void setApp(String app) { this.app = app; }
    public void setDescription(String description) { this.description = description; }
    public void setStatus(IncidentStatus status) { this.status = status; }
    public void setLastComment(String lastComment) { this.lastComment = lastComment; }
    public void setComments(String comments) { this.comments = comments; }
    public void setRca(String rca) { this.rca = rca; }
    public void setPlanSolution(String planSolution) { this.planSolution = planSolution; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    public void setIsApproved(Boolean isApproved) { this.isApproved = isApproved; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    public void setSolutions(List<Solution> solutions) { this.solutions = solutions; }
    public void setAttachments(List<IncidentAttachment> attachments) { this.attachments = attachments; }
    public void setEmbedding(IncidentEmbedding embedding) { this.embedding = embedding; }

    // ---- Builder ----
    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private final Incident incident = new Incident();

        public Builder incidentNo(String v)     { incident.incidentNo = v; return this; }
        public Builder taskNo(String v)          { incident.taskNo = v; return this; }
        public Builder app(String v)             { incident.app = v; return this; }
        public Builder description(String v)     { incident.description = v; return this; }
        public Builder status(IncidentStatus v)  { incident.status = v; return this; }
        public Builder lastComment(String v)     { incident.lastComment = v; return this; }
        public Builder comments(String v)        { incident.comments = v; return this; }
        public Builder rca(String v)             { incident.rca = v; return this; }
        public Builder planSolution(String v)    { incident.planSolution = v; return this; }
        public Builder createdBy(String v)       { incident.createdBy = v; return this; }
        public Builder isApproved(Boolean v)     { incident.isApproved = v; return this; }
        public Incident build()                  { return incident; }
    }

    @Override
    public String toString() {
        return "Incident{id=" + id + ", incidentNo='" + incidentNo + "'}";
    }
}
