package com.taskmanager.api.service;

import com.taskmanager.api.exception.ResourceNotFoundException;
import com.taskmanager.api.model.dto.request.TaskRequest;
import com.taskmanager.api.model.dto.response.PagedResponse;
import com.taskmanager.api.model.dto.response.TaskResponse;
import com.taskmanager.api.model.entity.Project;
import com.taskmanager.api.model.entity.Task;
import com.taskmanager.api.model.entity.User;
import com.taskmanager.api.model.entity.enums.TaskPriority;
import com.taskmanager.api.model.entity.enums.TaskStatus;
import com.taskmanager.api.model.mapper.TaskMapper;
import com.taskmanager.api.repository.ProjectRepository;
import com.taskmanager.api.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;

    @Transactional(readOnly = true)
    public PagedResponse<TaskResponse> getAllTasks(
            User user, int page, int size, String sortBy, String direction,
            TaskStatus status, TaskPriority priority, Long projectId, String search) {

        Sort sort = direction.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Task> taskPage = taskRepository.findByFilters(
                user.getId(), status, priority, projectId, search, pageable);

        return buildPagedResponse(taskPage);
    }

    @Transactional(readOnly = true)
    public TaskResponse getTaskById(Long taskId, User user) {
        Task task = taskRepository.findByIdAndAssigneeId(taskId, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Task", "id", taskId));
        return TaskMapper.toResponse(task);
    }

    @Transactional
    public TaskResponse createTask(TaskRequest request, User user) {
        Task task = Task.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .status(request.getStatus() != null ? request.getStatus() : TaskStatus.TODO)
                .priority(request.getPriority() != null ? request.getPriority() : TaskPriority.MEDIUM)
                .dueDate(request.getDueDate())
                .assignee(user)
                .build();

        if (request.getProjectId() != null) {
            Project project = projectRepository.findByIdAndOwnerId(request.getProjectId(), user.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Project", "id", request.getProjectId()));
            task.setProject(project);
        }

        task = taskRepository.save(task);
        log.info("Task created: id={}, title='{}', user={}", task.getId(), task.getTitle(), user.getEmail());
        return TaskMapper.toResponse(task);
    }

    @Transactional
    public TaskResponse updateTask(Long taskId, TaskRequest request, User user) {
        Task task = taskRepository.findByIdAndAssigneeId(taskId, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Task", "id", taskId));

        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());

        if (request.getStatus() != null) {
            task.setStatus(request.getStatus());
        }
        if (request.getPriority() != null) {
            task.setPriority(request.getPriority());
        }
        task.setDueDate(request.getDueDate());

        if (request.getProjectId() != null) {
            Project project = projectRepository.findByIdAndOwnerId(request.getProjectId(), user.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Project", "id", request.getProjectId()));
            task.setProject(project);
        } else {
            task.setProject(null);
        }

        task = taskRepository.save(task);
        log.info("Task updated: id={}, user={}", task.getId(), user.getEmail());
        return TaskMapper.toResponse(task);
    }

    @Transactional
    public void deleteTask(Long taskId, User user) {
        Task task = taskRepository.findByIdAndAssigneeId(taskId, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Task", "id", taskId));
        taskRepository.delete(task);
        log.info("Task deleted: id={}, user={}", taskId, user.getEmail());
    }

    private PagedResponse<TaskResponse> buildPagedResponse(Page<Task> page) {
        return PagedResponse.<TaskResponse>builder()
                .content(page.getContent().stream().map(TaskMapper::toResponse).toList())
                .pageNumber(page.getNumber())
                .pageSize(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .first(page.isFirst())
                .last(page.isLast())
                .build();
    }
}
