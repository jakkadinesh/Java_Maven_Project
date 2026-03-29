package com.taskmanager.api.service;

import com.taskmanager.api.exception.ResourceNotFoundException;
import com.taskmanager.api.model.dto.request.TaskRequest;
import com.taskmanager.api.model.dto.response.PagedResponse;
import com.taskmanager.api.model.dto.response.TaskResponse;
import com.taskmanager.api.model.entity.Task;
import com.taskmanager.api.model.entity.User;
import com.taskmanager.api.model.entity.enums.TaskPriority;
import com.taskmanager.api.model.entity.enums.TaskStatus;
import com.taskmanager.api.repository.ProjectRepository;
import com.taskmanager.api.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TaskService Unit Tests")
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private ProjectRepository projectRepository;

    @InjectMocks
    private TaskService taskService;

    private User testUser;
    private Task testTask;
    private TaskRequest testRequest;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .fullName("John Doe")
                .email("john@example.com")
                .build();

        testTask = Task.builder()
                .id(1L)
                .title("Test Task")
                .description("Test description")
                .status(TaskStatus.TODO)
                .priority(TaskPriority.HIGH)
                .dueDate(LocalDate.now().plusDays(7))
                .assignee(testUser)
                .build();

        testRequest = TaskRequest.builder()
                .title("Test Task")
                .description("Test description")
                .status(TaskStatus.TODO)
                .priority(TaskPriority.HIGH)
                .dueDate(LocalDate.now().plusDays(7))
                .build();
    }

    @Test
    @DisplayName("Should create task successfully")
    void createTask_Success() {
        when(taskRepository.save(any(Task.class))).thenReturn(testTask);

        TaskResponse response = taskService.createTask(testRequest, testUser);

        assertThat(response).isNotNull();
        assertThat(response.getTitle()).isEqualTo("Test Task");
        assertThat(response.getStatus()).isEqualTo("TODO");
        assertThat(response.getPriority()).isEqualTo("HIGH");
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    @DisplayName("Should get task by ID successfully")
    void getTaskById_Success() {
        when(taskRepository.findByIdAndAssigneeId(1L, 1L)).thenReturn(Optional.of(testTask));

        TaskResponse response = taskService.getTaskById(1L, testUser);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getTitle()).isEqualTo("Test Task");
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when task not found")
    void getTaskById_NotFound() {
        when(taskRepository.findByIdAndAssigneeId(99L, 1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.getTaskById(99L, testUser))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Task not found");
    }

    @Test
    @DisplayName("Should return paginated tasks")
    void getAllTasks_Paginated() {
        Page<Task> taskPage = new PageImpl<>(List.of(testTask), PageRequest.of(0, 10), 1);
        when(taskRepository.findByFilters(eq(1L), isNull(), isNull(), isNull(), isNull(), any(Pageable.class)))
                .thenReturn(taskPage);

        PagedResponse<TaskResponse> response = taskService.getAllTasks(
                testUser, 0, 10, "createdAt", "desc", null, null, null, null);

        assertThat(response.getContent()).hasSize(1);
        assertThat(response.getTotalElements()).isEqualTo(1);
        assertThat(response.getPageNumber()).isEqualTo(0);
    }

    @Test
    @DisplayName("Should update task successfully")
    void updateTask_Success() {
        TaskRequest updateRequest = TaskRequest.builder()
                .title("Updated Task")
                .description("Updated description")
                .status(TaskStatus.IN_PROGRESS)
                .priority(TaskPriority.CRITICAL)
                .build();

        Task updatedTask = Task.builder()
                .id(1L)
                .title("Updated Task")
                .description("Updated description")
                .status(TaskStatus.IN_PROGRESS)
                .priority(TaskPriority.CRITICAL)
                .assignee(testUser)
                .build();

        when(taskRepository.findByIdAndAssigneeId(1L, 1L)).thenReturn(Optional.of(testTask));
        when(taskRepository.save(any(Task.class))).thenReturn(updatedTask);

        TaskResponse response = taskService.updateTask(1L, updateRequest, testUser);

        assertThat(response.getTitle()).isEqualTo("Updated Task");
        assertThat(response.getStatus()).isEqualTo("IN_PROGRESS");
        assertThat(response.getPriority()).isEqualTo("CRITICAL");
    }

    @Test
    @DisplayName("Should delete task successfully")
    void deleteTask_Success() {
        when(taskRepository.findByIdAndAssigneeId(1L, 1L)).thenReturn(Optional.of(testTask));

        taskService.deleteTask(1L, testUser);

        verify(taskRepository, times(1)).delete(testTask);
    }

    @Test
    @DisplayName("Should throw when deleting non-existent task")
    void deleteTask_NotFound() {
        when(taskRepository.findByIdAndAssigneeId(99L, 1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.deleteTask(99L, testUser))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
