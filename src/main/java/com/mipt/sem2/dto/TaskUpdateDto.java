package com.mipt.sem2.dto;

import com.mipt.sem2.model.Priority;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;
import java.util.Set;

@Data
public class TaskUpdateDto {
  @Size(min = 3, max = 100, groups = OnUpdate.class)
  private String title;

  @Size(max = 500, groups = OnUpdate.class)
  private String description;

  private Boolean completed;

  private LocalDate dueDate;

  private Priority priority;

  @Size(max = 5, groups = OnUpdate.class)
  private Set<String> tags;
}