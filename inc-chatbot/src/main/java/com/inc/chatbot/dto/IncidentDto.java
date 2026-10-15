package com.inc.chatbot.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class IncidentDto {

    private Long id;

    @NotBlank(message = "Incident number is required")
    @Size(max = 50, message = "Incident number cannot exceed 50 characters")
    private String incidentNo;

    @Size(max = 50, message = "Task number cannot exceed 50 characters")
    private String taskNo;

    @Size(max = 100, message = "Application name cannot exceed 100 characters")
    private String app;

    private String description;
    private String status;
    private String lastComment;
    private String comments;
    private String rca;
    private String planSolution;
    private Boolean isApproved;

    public IncidentDto() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getIncidentNo() { return incidentNo; }
    public void setIncidentNo(String incidentNo) { this.incidentNo = incidentNo; }
    public String getTaskNo() { return taskNo; }
    public void setTaskNo(String taskNo) { this.taskNo = taskNo; }
    public String getApp() { return app; }
    public void setApp(String app) { this.app = app; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getLastComment() { return lastComment; }
    public void setLastComment(String lastComment) { this.lastComment = lastComment; }
    public String getComments() { return comments; }
    public void setComments(String comments) { this.comments = comments; }
    public String getRca() { return rca; }
    public void setRca(String rca) { this.rca = rca; }
    public String getPlanSolution() { return planSolution; }
    public void setPlanSolution(String planSolution) { this.planSolution = planSolution; }
    public Boolean getIsApproved() { return isApproved; }
    public void setIsApproved(Boolean isApproved) { this.isApproved = isApproved; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private final IncidentDto dto = new IncidentDto();
        public Builder incidentNo(String v)   { dto.incidentNo = v; return this; }
        public Builder taskNo(String v)        { dto.taskNo = v; return this; }
        public Builder app(String v)           { dto.app = v; return this; }
        public Builder description(String v)   { dto.description = v; return this; }
        public Builder status(String v)        { dto.status = v; return this; }
        public Builder planSolution(String v)  { dto.planSolution = v; return this; }
        public IncidentDto build()             { return dto; }
    }
}
