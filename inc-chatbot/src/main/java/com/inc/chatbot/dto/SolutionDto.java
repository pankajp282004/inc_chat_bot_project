package com.inc.chatbot.dto;

import jakarta.validation.constraints.NotNull;

public class SolutionDto {

    private Long id;

    @NotNull(message = "Incident ID is required")
    private Long incidentId;

    private String solutionType;
    private String solutionText;
    private String filePath;
    private Integer version;
    private Boolean isActive;

    public SolutionDto() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getIncidentId() { return incidentId; }
    public void setIncidentId(Long incidentId) { this.incidentId = incidentId; }
    public String getSolutionType() { return solutionType; }
    public void setSolutionType(String solutionType) { this.solutionType = solutionType; }
    public String getSolutionText() { return solutionText; }
    public void setSolutionText(String solutionText) { this.solutionText = solutionText; }
    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }
    public Integer getVersion() { return version; }
    public void setVersion(Integer version) { this.version = version; }
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
}
