package com.inc.chatbot.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "attachments")
public class IncidentAttachment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "incident_id", nullable = false)
    private Incident incident;

    @Column(name = "file_name", nullable = false, length = 255)
    private String fileName;

    @Column(name = "file_path", nullable = false, length = 500)
    private String filePath;

    @Column(name = "file_type", length = 50)
    private String fileType;

    @Column(name = "file_size")
    private Long fileSize;

    @CreationTimestamp
    @Column(name = "uploaded_at", updatable = false)
    private LocalDateTime uploadedAt;

    @Column(name = "uploaded_by", length = 100)
    private String uploadedBy;

    // ---- Constructors ----
    public IncidentAttachment() {}

    // ---- Getters ----
    public Long getId() { return id; }
    public Incident getIncident() { return incident; }
    public String getFileName() { return fileName; }
    public String getFilePath() { return filePath; }
    public String getFileType() { return fileType; }
    public Long getFileSize() { return fileSize; }
    public LocalDateTime getUploadedAt() { return uploadedAt; }
    public String getUploadedBy() { return uploadedBy; }

    // ---- Setters ----
    public void setId(Long id) { this.id = id; }
    public void setIncident(Incident incident) { this.incident = incident; }
    public void setFileName(String fileName) { this.fileName = fileName; }
    public void setFilePath(String filePath) { this.filePath = filePath; }
    public void setFileType(String fileType) { this.fileType = fileType; }
    public void setFileSize(Long fileSize) { this.fileSize = fileSize; }
    public void setUploadedAt(LocalDateTime uploadedAt) { this.uploadedAt = uploadedAt; }
    public void setUploadedBy(String uploadedBy) { this.uploadedBy = uploadedBy; }

    // ---- Builder ----
    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private final IncidentAttachment att = new IncidentAttachment();

        public Builder incident(Incident v)   { att.incident = v; return this; }
        public Builder fileName(String v)     { att.fileName = v; return this; }
        public Builder filePath(String v)     { att.filePath = v; return this; }
        public Builder fileType(String v)     { att.fileType = v; return this; }
        public Builder fileSize(Long v)       { att.fileSize = v; return this; }
        public Builder uploadedBy(String v)   { att.uploadedBy = v; return this; }
        public IncidentAttachment build()     { return att; }
    }

    @Override
    public String toString() {
        return "IncidentAttachment{id=" + id + ", fileName='" + fileName + "'}";
    }
}
