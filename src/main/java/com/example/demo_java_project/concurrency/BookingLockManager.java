package com.example.demo_java_project.concurrency;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

public class BookingLockManager {

    private static final BookingLockManager INSTANCE = new BookingLockManager();

    private final ConcurrentHashMap<Integer, ReentrantLock> resourceLocks = new ConcurrentHashMap<>();

    private BookingLockManager() {
    }

    public static BookingLockManager getInstance() {
        return INSTANCE;
    }

    public ReentrantLock getLockForResource(int resourceId) {
        return resourceLocks.computeIfAbsent(resourceId, id -> new ReentrantLock());
    }
}