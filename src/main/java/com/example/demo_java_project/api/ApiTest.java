package com.example.demo_java_project.api;

import java.util.List;

public class ApiTest {
    public static void main(String[] args) throws Exception {
        ApiClient apiClient = new ApiClient();
        JsonService jsonService = new JsonService();

        String json = apiClient.get("https://jsonplaceholder.typicode.com/posts");
        List<ExternalPost> posts = jsonService.parsePostList(json);

        System.out.println("Total posts fetched: " + posts.size());
        System.out.println("First post title: " + posts.get(0).getTitle());
    }
}