package com.taskmanager.api.service;

import com.taskmanager.api.exception.DuplicateResourceException;
import com.taskmanager.api.exception.ResourceNotFoundException;
import com.taskmanager.api.model.dto.request.ProjectRequest;
import com.taskmanager.api.model.dto.response.PagedResponse;
import com.taskmanager.api.model.dto.response.ProjectResponse;
import com.taskmanager.api.model.entity.Project;
import com.taskmanager.api.model.entity.User;
import com.taskmanager.api.model.mapper.ProjectMapper;
import com.taskmanager.api.repository.ProjectRepository;
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
public class ProjectService {

    private final ProjectRepository projectRepository;

    @Transactional(readOnly = true)
    public PagedResponse<ProjectResponse> getAllProjects(User user, int page, int size, String sortBy, String direction) {
        Sort sort = direction.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Project> projectPage = projectRepository.findByOwnerId(user.getId(), pageable);

        return PagedResponse.<ProjectResponse>builder()
                .content(projectPage.getContent().stream().map(ProjectMapper::toResponse).toList())
                .pageNumber(projectPage.getNumber())
                .pageSize(projectPage.getSize())
                .totalElements(projectPage.getTotalElements())
                .totalPages(projectPage.getTotalPages())
                .first(projectPage.isFirst())
                .last(projectPage.isLast())
                .build();
    }

    @Transactional(readOnly = true)
    public ProjectResponse getProjectById(Long projectId, User user) {
        Project project = projectRepository.findByIdAndOwnerId(projectId, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Project", "id", projectId));
        return ProjectMapper.toResponse(project);
    }

    @Transactional
    public ProjectResponse createProject(ProjectRequest request, User user) {
        if (projectRepository.existsByNameAndOwnerId(request.getName(), user.getId())) {
            throw new DuplicateResourceException("Project", "name", request.getName());
        }

        Project project = Project.builder()
                .name(request.getName())
                .description(request.getDescription())
                .owner(user)
                .build();

        project = projectRepository.save(project);
        log.info("Project created: id={}, name='{}', user={}", project.getId(), project.getName(), user.getEmail());
        return ProjectMapper.toResponse(project);
    }

    @Transactional
    public ProjectResponse updateProject(Long projectId, ProjectRequest request, User user) {
        Project project = projectRepository.findByIdAndOwnerId(projectId, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Project", "id", projectId));

        if (!project.getName().equals(request.getName())
                && projectRepository.existsByNameAndOwnerId(request.getName(), user.getId())) {
            throw new DuplicateResourceException("Project", "name", request.getName());
        }

        project.setName(request.getName());
        project.setDescription(request.getDescription());

        project = projectRepository.save(project);
        log.info("Project updated: id={}, user={}", project.getId(), user.getEmail());
        return ProjectMapper.toResponse(project);
    }

    @Transactional
    public void deleteProject(Long projectId, User user) {
        Project project = projectRepository.findByIdAndOwnerId(projectId, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Project", "id", projectId));
        projectRepository.delete(project);
        log.info("Project deleted: id={}, user={}", projectId, user.getEmail());
    }
}
