package com.mipt.sem2.repository;

import com.mipt.sem2.model.TaskAttachment;
import java.util.List;
import java.util.Optional;

public interface AttachmentRepository {
  List<TaskAttachment> findByTaskId(Long taskId);
  Optional<TaskAttachment> findById(Long id);
  TaskAttachment save(TaskAttachment attachment);
  void deleteById(Long id);
  boolean existsById(Long id);
}
