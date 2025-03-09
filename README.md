# Task Manager - Design Rationale

## Data Layer

Located under `task_manager/data/`, this layer handles data storage and retrieval.

### **local/**
- **`TaskDao`**: Data Access Object (DAO) interface for database queries.
- **`TaskDatabase`**: Singleton Room Database instance.
- **`TaskDatabase_Impl`**: Auto-generated implementation for `TaskDatabase`.

### **models/**
- **`Task`**: Data class defining the attributes of a task.

### **repository/**
- **`TaskRepository`**: Mediates between the DAO and ViewModels.

### **Rationale**
A **repository pattern** is used to provide a clean API for data access, ensuring **separation of concerns** between the database and business logic.

---

## Domain Layer

Located under `task_manager/domain/`, this layer contains business logic.

- **`NotificationWorker`**: Handles task notifications using WorkManager.
- **`ScheduleWorkManager`**: Manages background task scheduling.

### **Rationale**
Keeping **business logic separate** from UI ensures better **testability** and **maintainability**.

---

## UI Layer

Located under `task_manager/ui/`, this layer handles user interaction and presentation.

### **Adapters**
- **`TaskAdapter`**: RecyclerView adapter for displaying tasks.
- **`TaskDiffCallback`**: Optimizes updates by checking item differences.

### **Fragments**
- **`HomeFragment`**: Displays tasks and provides sorting/filtering.
- **`CreateFragment`**: Handles task creation.
- **`SettingsFragment`**: Manages app settings.
- **`TaskViewFragment`**: Displays detailed task information.

### **Rationale**
The app follows the **MVVM pattern**, with **Fragments** acting as UI components that observe data from **ViewModels**.

### **ViewModels**
- **`TaskViewModel`**: Handles task-related operations (add, delete, update).
- **`TaskViewModelFactory`**: Provides ViewModel instances with dependencies.

### **Rationale**
ViewModels **decouple UI from business logic** and ensure **data persistence** during configuration changes.
