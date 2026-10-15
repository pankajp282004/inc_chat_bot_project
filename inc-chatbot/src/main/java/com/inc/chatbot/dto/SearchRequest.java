package com.inc.chatbot.dto;

public class SearchRequest {

    private String query;
    private String app;
    private String status;
    private int page = 0;
    private int size = 10;

    public SearchRequest() {}

    public SearchRequest(String query, String app, String status, int page, int size) {
        this.query = query;
        this.app = app;
        this.status = status;
        this.page = page;
        this.size = size;
    }

    public String getQuery() { return query; }
    public void setQuery(String query) { this.query = query; }
    public String getApp() { return app; }
    public void setApp(String app) { this.app = app; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public int getPage() { return page; }
    public void setPage(int page) { this.page = page; }
    public int getSize() { return size; }
    public void setSize(int size) { this.size = size; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private final SearchRequest r = new SearchRequest();
        public Builder query(String v) { r.query = v; return this; }
        public Builder app(String v)   { r.app = v; return this; }
        public Builder status(String v){ r.status = v; return this; }
        public Builder page(int v)     { r.page = v; return this; }
        public Builder size(int v)     { r.size = v; return this; }
        public SearchRequest build()   { return r; }
    }
}
