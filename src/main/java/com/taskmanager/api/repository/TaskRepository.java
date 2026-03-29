package com.taskmanager.api.repository;

import com.taskmanager.api.model.entity.Task;
import com.taskmanager.api.model.entity.enums.TaskPriority;
import com.taskmanager.api.model.entity.enums.TaskStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    Page<Task> findByAssigneeId(Long assigneeId, Pageable pageable);

    Optional<Task> findByIdAndAssigneeId(Long id, Long assigneeId);

    @Query("SELECT t FROM Task t WHERE t.assignee.id = :userId " +
            "AND (:status IS NULL OR t.status = :status) " +
            "AND (:priority IS NULL OR t.priority = :priority) " +
            "AND (:projectId IS NULL OR t.project.id = :projectId) " +
            "AND (:search IS NULL OR LOWER(t.title) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Task> findByFilters(
            @Param("userId") Long userId,
            @Param("status") TaskStatus status,
            @Param("priority") TaskPriority priority,
            @Param("projectId") Long projectId,
            @Param("search") String search,
            Pageable pageable
    );

    @Query("SELECT COUNT(t) FROM Task t WHERE t.assignee.id = :userId AND t.status = :status")
    long countByAssigneeIdAndStatus(@Param("userId") Long userId, @Param("status") TaskStatus status);
}
