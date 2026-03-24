package com.mipt.sem2.repository;

import com.mipt.sem2.model.TaskAttachment;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository
public class InMemoryAttachmentRepository implements AttachmentRepository {
  private final Map<Long, TaskAttachment> storage = new ConcurrentHashMap<>();
  private final AtomicLong idGenerator = new AtomicLong(1);

  @Override
  public List<TaskAttachment> findByTaskId(Long taskId) {
    return storage.values().stream()
        .filter(a -> Objects.equals(a.getTaskId(), taskId))
        .collect(Collectors.toList());
  }

  @Override
  public Optional<TaskAttachment> findById(Long id) {
    return Optional.ofNullable(storage.get(id));
  }

  @Override
  public TaskAttachment save(TaskAttachment attachment) {
    if (attachment.getId() == null) {
      attachment.setId(idGenerator.getAndIncrement());
    }
    storage.put(attachment.getId(), attachment);
    return attachment;
  }

  @Override
  public void deleteById(Long id) {
    storage.remove(id);
  }

  @Override
  public boolean existsById(Long id) {
    return storage.containsKey(id);
  }
}