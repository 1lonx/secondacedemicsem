package com.mipt.sem2.repository;

import com.mipt.sem2.model.Task;
import java.util.List;
import java.util.Optional;

public interface TaskRepository {
  List<Task> findAll();
  Optional<Task> findById(Long id);
  Task save(Task task);
  void deleteById(Long id);
  boolean existsById(Long id);
}