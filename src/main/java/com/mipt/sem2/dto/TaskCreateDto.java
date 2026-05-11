package com.mipt.sem2.dto;

import com.mipt.sem2.model.Priority;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.Set;

@Data
public class TaskCreateDto {
  @NotBlank(groups = OnCreate.class)
  @Size(min = 3, max = 100, groups = OnCreate.class)
  private String title;

  @Size(max = 500, groups = OnCreate.class)
  private String description;

  @NotNull(groups = OnCreate.class)
  @FutureOrPresent(groups = OnCreate.class)
  private LocalDate dueDate;

  @NotNull(groups = OnCreate.class)
  private Priority priority;

  @Size(max = 5, groups = OnCreate.class)
  private Set<String> tags;
}
