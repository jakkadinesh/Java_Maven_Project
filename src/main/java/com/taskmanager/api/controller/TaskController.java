package com.taskmanager.api.controller;

import com.taskmanager.api.model.dto.request.TaskRequest;
import com.taskmanager.api.model.dto.response.PagedResponse;
import com.taskmanager.api.model.dto.response.TaskResponse;
import com.taskmanager.api.model.entity.User;
import com.taskmanager.api.model.entity.enums.TaskPriority;
import com.taskmanager.api.model.entity.enums.TaskStatus;
import com.taskmanager.api.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "Tasks", description = "Task management operations")
public class TaskController {

    private final TaskService taskService;

    @GetMapping
    @Operation(summary = "List tasks", description = "Retrieve paginated and filtered list of tasks for the authenticated user")
    public ResponseEntity<PagedResponse<TaskResponse>> getAllTasks(
            @AuthenticationPrincipal User user,
            @RequestParam(defaultValue = "0") @Parameter(description = "Page number (0-indexed)") int page,
            @RequestParam(defaultValue = "10") @Parameter(description = "Page size") int size,
            @RequestParam(defaultValue = "createdAt") @Parameter(description = "Sort field") String sortBy,
            @RequestParam(defaultValue = "desc") @Parameter(description = "Sort direction (asc/desc)") String direction,
            @RequestParam(required = false) @Parameter(description = "Filter by status") TaskStatus status,
            @RequestParam(required = false) @Parameter(description = "Filter by priority") TaskPriority priority,
            @RequestParam(required = false) @Parameter(description = "Filter by project ID") Long projectId,
            @RequestParam(required = false) @Parameter(description = "Search in title") String search
    ) {
        PagedResponse<TaskResponse> response = taskService.getAllTasks(
                user, page, size, sortBy, direction, status, priority, projectId, search);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get task by ID")
    public ResponseEntity<TaskResponse> getTaskById(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        TaskResponse response = taskService.getTaskById(id, user);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    @Operation(summary = "Create a new task")
    public ResponseEntity<TaskResponse> createTask(
            @Valid @RequestBody TaskRequest request,
            @AuthenticationPrincipal User user) {
        TaskResponse response = taskService.createTask(request, user);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing task")
    public ResponseEntity<TaskResponse> updateTask(
            @PathVariable Long id,
            @Valid @RequestBody TaskRequest request,
            @AuthenticationPrincipal User user) {
        TaskResponse response = taskService.updateTask(id, request, user);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a task")
    public ResponseEntity<Void> deleteTask(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        taskService.deleteTask(id, user);
        return ResponseEntity.noContent().build();
    }
}
