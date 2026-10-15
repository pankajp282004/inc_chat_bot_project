package com.inc.chatbot.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "solutions")
public class Solution {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "incident_id", nullable = false)
    private Incident incident;

    @Enumerated(EnumType.STRING)
    @Column(name = "solution_type", nullable = false)
    private SolutionType solutionType = SolutionType.STEPS;

    @Column(name = "solution_text", columnDefinition = "LONGTEXT")
    private String solutionText;

    @Column(name = "file_path", length = 500)
    private String filePath;

    @Column(name = "version")
    private Integer version = 1;

    @Column(name = "created_by", length = 100)
    private String createdBy;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "is_active")
    private Boolean isActive = true;

    public enum SolutionType {
        SQL, STEPS, DOC
    }

    // ---- Constructors ----
    public Solution() {}

    // ---- Getters ----
    public Long getId() { return id; }
    public Incident getIncident() { return incident; }
    public SolutionType getSolutionType() { return solutionType; }
    public String getSolutionText() { return solutionText; }
    public String getFilePath() { return filePath; }
    public Integer getVersion() { return version; }
    public String getCreatedBy() { return createdBy; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public Boolean getIsActive() { return isActive; }

    // ---- Setters ----
    public void setId(Long id) { this.id = id; }
    public void setIncident(Incident incident) { this.incident = incident; }
    public void setSolutionType(SolutionType solutionType) { this.solutionType = solutionType; }
    public void setSolutionText(String solutionText) { this.solutionText = solutionText; }
    public void setFilePath(String filePath) { this.filePath = filePath; }
    public void setVersion(Integer version) { this.version = version; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }

    // ---- Builder ----
    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private final Solution solution = new Solution();

        public Builder incident(Incident v)        { solution.incident = v; return this; }
        public Builder solutionType(SolutionType v){ solution.solutionType = v; return this; }
        public Builder solutionText(String v)      { solution.solutionText = v; return this; }
        public Builder filePath(String v)          { solution.filePath = v; return this; }
        public Builder version(Integer v)          { solution.version = v; return this; }
        public Builder createdBy(String v)         { solution.createdBy = v; return this; }
        public Builder isActive(Boolean v)         { solution.isActive = v; return this; }
        public Solution build()                    { return solution; }
    }

    @Override
    public String toString() {
        return "Solution{id=" + id + ", version=" + version + "}";
    }
}
