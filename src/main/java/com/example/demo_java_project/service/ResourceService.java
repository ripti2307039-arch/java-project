package com.example.demo_java_project.service;

import com.example.demo_java_project.dao.ResourceDAO;
import com.example.demo_java_project.model.Resource;

import java.util.List;
import java.util.Optional;

public class ResourceService {

    private final ResourceDAO resourceDAO;

    public ResourceService() {
        this.resourceDAO = new ResourceDAO();
    }

    public List<Resource> getAllResources() {
        return resourceDAO.findAll();
    }

    public Optional<Resource> getResourceById(int id) {
        return resourceDAO.findById(id);
    }

    public boolean addResource(String name, String type, String location, String description) {
        if (name == null || name.isBlank() || type == null || type.isBlank()) {
            return false;
        }

        Resource resource = new Resource(name, type, location, description);
        int newId = resourceDAO.createResource(resource);
        return newId != -1;
    }

    public boolean updateResource(Resource resource) {
        return resourceDAO.updateResource(resource);
    }

    public boolean deleteResource(int id) {
        return resourceDAO.deleteResource(id);
    }
}