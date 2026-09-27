package com.example.demo_java_project.model;

public class Resource extends BaseEntity {

    private String name;
    private String type;
    private String location;
    private String description;

    public Resource() {
    }

    public Resource(int id, String name, String type, String location,
                    String description, String createdAt) {
        super(id, createdAt);
        this.name = name;
        this.type = type;
        this.location = location;
        this.description = description;
    }

    // Convenience constructor for creating a NEW resource
    public Resource(String name, String type, String location, String description) {
        this.name = name;
        this.type = type;
        this.location = location;
        this.description = description;
    }

    // ---- Getters and Setters ----

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

    @Override
    public String getSummary() {
        return name + " (" + type + ")";
    }

    @Override
    public String toString() {
        return "Resource{id=" + id + ", name='" + name + "', type='" + type + "'}";
    }
}