package com.taskmanager.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.taskmanager.api.model.dto.request.RegisterRequest;
import com.taskmanager.api.model.dto.request.TaskRequest;
import com.taskmanager.api.model.dto.response.AuthResponse;
import com.taskmanager.api.model.entity.enums.TaskPriority;
import com.taskmanager.api.model.entity.enums.TaskStatus;
import com.taskmanager.api.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("TaskController Integration Tests")
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AuthService authService;

    private String accessToken;

    @BeforeEach
    void setUp() {
        try {
            AuthResponse auth = authService.register(RegisterRequest.builder()
                    .fullName("Task Test User")
                    .email("task-test@example.com")
                    .password("password123")
                    .build());
            accessToken = auth.getAccessToken();
        } catch (Exception e) {
            // User already exists, login instead
            AuthResponse auth = authService.login(
                    new com.taskmanager.api.model.dto.request.LoginRequest("task-test@example.com", "password123"));
            accessToken = auth.getAccessToken();
        }
    }

    @Test
    @DisplayName("Should create task with valid JWT")
    void createTask_Success() throws Exception {
        TaskRequest request = TaskRequest.builder()
                .title("Integration Test Task")
                .description("Created during integration test")
                .status(TaskStatus.TODO)
                .priority(TaskPriority.HIGH)
                .dueDate(LocalDate.now().plusDays(7))
                .build();

        mockMvc.perform(post("/api/tasks")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Integration Test Task"))
                .andExpect(jsonPath("$.status").value("TODO"))
                .andExpect(jsonPath("$.priority").value("HIGH"));
    }

    @Test
    @DisplayName("Should fail without authentication")
    void createTask_Unauthorized() throws Exception {
        TaskRequest request = TaskRequest.builder()
                .title("Unauthorized Task")
                .build();

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Should get paginated tasks")
    void getTasks_Paginated() throws Exception {
        mockMvc.perform(get("/api/tasks")
                        .header("Authorization", "Bearer " + accessToken)
                        .param("page", "0")
                        .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.pageNumber").value(0))
                .andExpect(jsonPath("$.pageSize").value(5));
    }

    @Test
    @DisplayName("Should fail creating task with blank title")
    void createTask_ValidationError() throws Exception {
        TaskRequest request = TaskRequest.builder()
                .title("")
                .build();

        mockMvc.perform(post("/api/tasks")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.title").exists());
    }

    @Test
    @DisplayName("Should return 404 for non-existent task")
    void getTask_NotFound() throws Exception {
        mockMvc.perform(get("/api/tasks/99999")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").exists());
    }
}
