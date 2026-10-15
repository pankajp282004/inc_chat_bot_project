package com.inc.chatbot.dto;

import java.time.LocalDateTime;
import java.util.List;

public class SearchResponse {

    private String query;
    private int totalResults;
    private List<IncidentSearchResult> results;
    private boolean hasMore;

    public SearchResponse() {}

    public SearchResponse(String query, int totalResults, List<IncidentSearchResult> results, boolean hasMore) {
        this.query = query;
        this.totalResults = totalResults;
        this.results = results;
        this.hasMore = hasMore;
    }

    public String getQuery() { return query; }
    public void setQuery(String query) { this.query = query; }
    public int getTotalResults() { return totalResults; }
    public void setTotalResults(int totalResults) { this.totalResults = totalResults; }
    public List<IncidentSearchResult> getResults() { return results; }
    public void setResults(List<IncidentSearchResult> results) { this.results = results; }
    public boolean isHasMore() { return hasMore; }
    public void setHasMore(boolean hasMore) { this.hasMore = hasMore; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private final SearchResponse r = new SearchResponse();
        public Builder query(String v)                      { r.query = v; return this; }
        public Builder totalResults(int v)                  { r.totalResults = v; return this; }
        public Builder results(List<IncidentSearchResult> v){ r.results = v; return this; }
        public Builder hasMore(boolean v)                   { r.hasMore = v; return this; }
        public SearchResponse build()                       { return r; }
    }

    // ---- Inner class ----
    public static class IncidentSearchResult {
        private Long id;
        private String incidentNo;
        private String taskNo;
        private String app;
        private String description;
        private String status;
        private String rca;
        private String planSolution;
        private String solution;
        private String matchReason;
        private int matchScore;
        private LocalDateTime createdAt;
        private Boolean isApproved;

        public IncidentSearchResult() {}

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
        public String getRca() { return rca; }
        public void setRca(String rca) { this.rca = rca; }
        public String getPlanSolution() { return planSolution; }
        public void setPlanSolution(String planSolution) { this.planSolution = planSolution; }
        public String getSolution() { return solution; }
        public void setSolution(String solution) { this.solution = solution; }
        public String getMatchReason() { return matchReason; }
        public void setMatchReason(String matchReason) { this.matchReason = matchReason; }
        public int getMatchScore() { return matchScore; }
        public void setMatchScore(int matchScore) { this.matchScore = matchScore; }
        public LocalDateTime getCreatedAt() { return createdAt; }
        public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
        public Boolean getIsApproved() { return isApproved; }
        public void setIsApproved(Boolean isApproved) { this.isApproved = isApproved; }

        public static IBuilder builder() { return new IBuilder(); }

        public static class IBuilder {
            private final IncidentSearchResult r = new IncidentSearchResult();
            public IBuilder id(Long v)              { r.id = v; return this; }
            public IBuilder incidentNo(String v)    { r.incidentNo = v; return this; }
            public IBuilder taskNo(String v)        { r.taskNo = v; return this; }
            public IBuilder app(String v)           { r.app = v; return this; }
            public IBuilder description(String v)   { r.description = v; return this; }
            public IBuilder status(String v)        { r.status = v; return this; }
            public IBuilder rca(String v)           { r.rca = v; return this; }
            public IBuilder planSolution(String v)  { r.planSolution = v; return this; }
            public IBuilder solution(String v)      { r.solution = v; return this; }
            public IBuilder matchReason(String v)   { r.matchReason = v; return this; }
            public IBuilder matchScore(int v)       { r.matchScore = v; return this; }
            public IBuilder createdAt(LocalDateTime v) { r.createdAt = v; return this; }
            public IBuilder isApproved(Boolean v)   { r.isApproved = v; return this; }
            public IncidentSearchResult build()     { return r; }
        }
    }
}
