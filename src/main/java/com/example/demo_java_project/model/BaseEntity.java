package com.example.demo_java_project.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public abstract class BaseEntity {

    protected int id;

    @JsonProperty("created_at")
    protected String createdAt;

    protected BaseEntity() {
    }

    protected BaseEntity(int id, String createdAt) {
        this.id = id;
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public abstract String getSummary();
}