package com.mipt.sem2.service;

import com.mipt.sem2.client.ExternalTasksClient;
import com.mipt.sem2.dto.CreateTaskRequest;
import com.mipt.sem2.dto.TaskDto;
import com.mipt.sem2.exception.ExternalApiException;
import com.mipt.sem2.exception.TaskNotFoundException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TasksGatewayService {

    private final ExternalTasksClient client;

    @CircuitBreaker(name = "externalApi", fallbackMethod = "getTaskFallback")
    @RateLimiter(name = "externalApi")
    public TaskDto getTask(long id) {
        return client.getTask(id);
    }

    public TaskDto getTaskFallback(long id, Throwable t) {
        if (t instanceof TaskNotFoundException tne) throw tne;
        log.warn("getTask fallback id={} cause={}", id, t.getClass().getSimpleName());
        return new TaskDto(id, "unavailable", false, "Service temporarily unavailable");
    }

    @CircuitBreaker(name = "externalApi", fallbackMethod = "createTaskFallback")
    @RateLimiter(name = "externalApi")
    public ResponseEntity<TaskDto> createTask(CreateTaskRequest req) {
        return client.createTask(req);
    }

    public ResponseEntity<TaskDto> createTaskFallback(CreateTaskRequest req, Throwable t) {
        log.warn("createTask fallback cause={}", t.getMessage());
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
    }

    @CircuitBreaker(name = "externalApi", fallbackMethod = "listTasksFallback")
    @RateLimiter(name = "externalApi")
    public List<TaskDto> listTasks(Boolean completed, Integer limit) {
        return client.listTasks(completed, limit);
    }

    public List<TaskDto> listTasksFallback(Boolean completed, Integer limit, Throwable t) {
        log.warn("listTasks fallback cause={}", t.getMessage());
        return List.of();
    }

    @CircuitBreaker(name = "externalApi", fallbackMethod = "deleteTaskFallback")
    @RateLimiter(name = "externalApi")
    public void deleteTask(long id) {
        client.deleteTask(id);
    }

    public void deleteTaskFallback(long id, Throwable t) {
        if (t instanceof TaskNotFoundException tne) throw tne;
        log.warn("deleteTask fallback id={} cause={}", id, t.getMessage());
        throw new ExternalApiException("Service unavailable, could not delete task " + id);
    }

    @CircuitBreaker(name = "externalApi", fallbackMethod = "probeUnstableFallback")
    @RateLimiter(name = "externalApi")
    public String probeUnstable(String mode) {
        return client.probeUnstable(mode);
    }

    public String probeUnstableFallback(String mode, Throwable t) {
        log.warn("probeUnstable fallback mode={} cause={}: {}", mode, t.getClass().getSimpleName(), t.getMessage());
        return "fallback: " + t.getClass().getSimpleName();
    }
}
