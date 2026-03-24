package com.mipt.sem2.model;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;


@Data
public class Task {
  private Long id;
  private String title;
  private String description;
  private boolean completed;
  private LocalDateTime createdAt;
  private LocalDate dueDate;
  private Priority priority;
  private Set<String> tags = new HashSet<>();

  public Task() {
    this.createdAt = LocalDateTime.now();
  }
}