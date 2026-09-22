package com.example.demo_java_project.concurrency;

import com.example.demo_java_project.dao.ResourceDAO;
import com.example.demo_java_project.dao.UserDAO;
import com.example.demo_java_project.database.DatabaseConnection;
import com.example.demo_java_project.model.Resource;
import com.example.demo_java_project.model.User;
import com.example.demo_java_project.security.PasswordUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

public class ConcurrencyTest {
    public static void main(String[] args) throws Exception {
        DatabaseConnection.initializeDatabase();

        UserDAO userDAO = new UserDAO();
        ResourceDAO resourceDAO = new ResourceDAO();

        Resource testResource = new Resource("Stress Test Room", "Meeting Room", "Test Floor", "Used for concurrency testing");
        int resourceId = resourceDAO.createResource(testResource);

        int threadCount = 10;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        List<Future<BookingTask.BookingResult>> futures = new ArrayList<>();
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);

        for (int i = 0; i < threadCount; i++) {
            User user = new User("Stress User " + i, "stressuser" + i + "@example.com",
                    PasswordUtil.hashPassword("password123"), "USER");
            int userId = userDAO.createUser(user);

            BookingTask task = new BookingTask(userId, resourceId, "2026-05-01 10:00", "2026-05-01 11:00");
            futures.add(executor.submit(task));
        }

        for (Future<BookingTask.BookingResult> future : futures) {
            BookingTask.BookingResult result = future.get();
            if (result.isSuccess()) {
                successCount.incrementAndGet();
            } else {
                failureCount.incrementAndGet();
            }
        }

        executor.shutdown();

        System.out.println("Total threads attempted: " + threadCount);
        System.out.println("Successful bookings: " + successCount.get());
        System.out.println("Failed bookings (conflict detected): " + failureCount.get());
    }
}