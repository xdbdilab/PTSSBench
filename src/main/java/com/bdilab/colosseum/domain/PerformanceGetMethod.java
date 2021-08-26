package com.bdilab.colosseum.domain;

public class PerformanceGetMethod {
    private Long id;

    private String sutName;

    private String getPerformanceUrl;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSutName() {
        return sutName;
    }

    public void setSutName(String sutName) {
        this.sutName = sutName == null ? null : sutName.trim();
    }

    public String getGetPerformanceUrl() {
        return getPerformanceUrl;
    }

    public void setGetPerformanceUrl(String getPerformanceUrl) {
        this.getPerformanceUrl = getPerformanceUrl == null ? null : getPerformanceUrl.trim();
    }
}