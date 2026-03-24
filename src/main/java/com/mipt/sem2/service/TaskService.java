package com.mipt.sem2.service;

import com.mipt.sem2.dto.TaskCreateDto;
import com.mipt.sem2.dto.TaskUpdateDto;
import com.mipt.sem2.exception.TaskNotFoundException;
import com.mipt.sem2.mapper.TaskMapper;
import com.mipt.sem2.model.Task;
import com.mipt.sem2.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskService {
  private final TaskRepository taskRepository;
  private final TaskMapper taskMapper;

  public List<Task> findAll() {
    return taskRepository.findAll();
  }

  public Task findById(Long id) {
    return taskRepository.findById(id)
        .orElseThrow(() -> new TaskNotFoundException(id));
  }

  @Transactional
  public Task create(TaskCreateDto dto) {
    Task task = taskMapper.toEntity(dto);
    return taskRepository.save(task);
  }

  @Transactional
  public Task update(Long id, TaskUpdateDto dto) {
    Task task = findById(id);
    taskMapper.updateEntity(dto, task);
    if (dto.getCompleted() != null) {
      task.setCompleted(dto.getCompleted());
    }
    return taskRepository.save(task);
  }

  @Transactional
  public void delete(Long id) {
    if (!taskRepository.existsById(id)) {
      throw new TaskNotFoundException(id);
    }
    taskRepository.deleteById(id);
  }
}