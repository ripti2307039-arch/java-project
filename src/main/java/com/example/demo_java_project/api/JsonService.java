package com.example.demo_java_project.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

public class JsonService {

    private final ObjectMapper objectMapper;

    public JsonService() {
        this.objectMapper = new ObjectMapper();
    }

    public List<ExternalPost> parsePostList(String json) throws JsonProcessingException {
        return objectMapper.readValue(json, new TypeReference<List<ExternalPost>>() {});
    }

    public ExternalPost parseSinglePost(String json) throws JsonProcessingException {
        return objectMapper.readValue(json, ExternalPost.class);
    }

    public String toJson(Object object) throws JsonProcessingException {
        return objectMapper.writeValueAsString(object);
    }
}