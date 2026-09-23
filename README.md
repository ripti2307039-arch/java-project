# SlotSync

A real-time, thread-synchronized resource booking and management system built with JavaFX and SQLite.

## Overview

SlotSync allows users to log in and book shared resources such as meeting rooms, projectors, and lab equipment in real time. The core challenge this project solves is preventing double-booking when multiple users attempt to reserve the same resource at the same time, using Java multithreading and locking mechanisms.

## Features

- User authentication with BCrypt password hashing
- Session management across the application
- Dashboard displaying all available resources
- Thread-safe booking system using ReentrantLock, preventing double-booking under concurrent access
- My Bookings page for viewing and cancelling personal reservations
- Profile management
- Admin panel for managing users, resources, and bookings
- External JSON API integration using Jackson for notification syncing
- SQLite as the persistent local database

## Tech Stack

- Language: Java 21
- GUI: JavaFX 21.0.6 with FXML and CSS
- Build Tool: Maven
- Database: SQLite via sqlite-jdbc
- Concurrency: Java Threads, ExecutorService, ReentrantLock
- JSON: Jackson (jackson-databind, jackson-core, jackson-annotations)
- HTTP Client: java.net.http.HttpClient
- Security: BCrypt via jbcrypt

## Project Structure
demo_java_project
├── pom.xml
└── src
└── main
├── java
│ └── com/example/demo_java_project
│ ├── Launcher.java
│ ├── SlotSyncApplication.java
│ ├── controller
│ ├── model
│ ├── dao
│ ├── service
│ ├── concurrency
│ ├── database
│ ├── api
│ ├── security
│ └── session
└── resources
└── com.example.demo_java_project
├── fxml
└── css

## How the Concurrency Model Works

Every resource has its own dedicated lock, managed by `BookingLockManager`, which maps resource IDs to `ReentrantLock` instances using a `ConcurrentHashMap`. When a booking request comes in, `BookingTask` acquires the lock for that specific resource before checking for time-slot conflicts and inserting the booking. This ensures that two threads can never simultaneously confirm overlapping bookings for the same resource, while bookings for different resources can still proceed in parallel through a fixed thread pool managed by `BookingService`.

## Setup Instructions

1. Clone the repository
2. Open the project in IntelliJ IDEA as a Maven project
3. Ensure JDK 21 is configured
4. Let Maven download all dependencies
5. Run the application using the javafx-maven-plugin:
    - Open the Maven panel
    - Navigate to Plugins → javafx → javafx:run

## Creating an Admin Account

By default, all new accounts are created with the USER role. To promote an account to ADMIN, update the database directly:

```sql
UPDATE users SET role = 'ADMIN' WHERE email = 'your-email@example.com';
```

Log out and log back in for the role change to take effect.

## Database

SQLite database file `slotsync.db` is created automatically in the project root on first run. It is excluded from version control via `.gitignore`.

## Author

Built as a learning project to demonstrate real-world multithreading, JavaFX desktop application development, and clean layered architecture (Model, DAO, Service, Controller).

