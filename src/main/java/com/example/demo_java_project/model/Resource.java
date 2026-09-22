package com.example.demo_java_project.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents a row from the "resources" table.
 * A Resource is anything bookable: a meeting room, projector, lab equipment, etc.
 */
public class Resource {

    private int id;
    private String name;
    private String type;
    private String location;
    private String description;

    @JsonProperty("created_at")
    private String createdAt;

    public Resource() {
    }

    public Resource(int id, String name, String type, String location,
                    String description, String createdAt) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.location = location;
        this.description = description;
        this.createdAt = createdAt;
    }

    // Convenience constructor for creating a NEW resource
    public Resource(String name, String type, String location, String description) {
        this.name = name;
        this.type = type;
        this.location = location;
        this.description = description;
    }

    // ---- Getters and Setters ----

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Resource{id=" + id + ", name='" + name + "', type='" + type + "'}";
    }
}