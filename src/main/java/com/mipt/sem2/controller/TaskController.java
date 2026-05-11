package com.mipt.sem2.controller;

import com.mipt.sem2.dto.*;
import com.mipt.sem2.entity.Task;
import com.mipt.sem2.mapper.TaskMapper;
import com.mipt.sem2.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

  private final TaskService taskService;
  private final TaskMapper taskMapper;

  @Value("${app.api.version:2.0.0}")
  private String apiVersion;

  @GetMapping
  @Operation(summary = "Get all tasks")
  @ApiResponses(value = {
          @ApiResponse(responseCode = "200", description = "List of tasks")
  })
  public ResponseEntity<List<TaskResponseDto>> getAllTasks() {
    List<Task> tasks = taskService.findAll();
    List<TaskResponseDto> dtos = tasks.stream()
            .map(taskMapper::toResponseDto)
            .collect(Collectors.toList());
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Total-Count", String.valueOf(tasks.size()));
    headers.add("X-API-Version", apiVersion);
    return ResponseEntity.ok().headers(headers).body(dtos);
  }

  @GetMapping("/{id}")
  @Operation(summary = "Get task by id")
  public ResponseEntity<TaskResponseDto> getTask(@PathVariable Long id) {
    Task task = taskService.findById(id);
    return ResponseEntity.ok()
            .header("X-API-Version", apiVersion)
            .body(taskMapper.toResponseDto(task));
  }

  @PostMapping
  @Operation(summary = "Create a new task")
  public ResponseEntity<TaskResponseDto> createTask(@Validated(OnCreate.class) @RequestBody TaskCreateDto dto) {
    Task task = taskService.create(dto);
    return ResponseEntity.status(HttpStatus.CREATED)
            .header("X-API-Version", apiVersion)
            .body(taskMapper.toResponseDto(task));
  }

  @PutMapping("/{id}")
  @Operation(summary = "Update an existing task")
  public ResponseEntity<TaskResponseDto> updateTask(@PathVariable Long id,
                                                    @Validated(OnUpdate.class) @RequestBody TaskUpdateDto dto) {
    Task task = taskService.update(id, dto);
    return ResponseEntity.ok()
            .header("X-API-Version", apiVersion)
            .body(taskMapper.toResponseDto(task));
  }

  @DeleteMapping("/{id}")
  @Operation(summary = "Delete a task")
  public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
    taskService.delete(id);
    return ResponseEntity.noContent()
            .header("X-API-Version", apiVersion)
            .build();
  }
}
