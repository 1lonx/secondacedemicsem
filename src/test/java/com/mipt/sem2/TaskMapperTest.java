package com.mipt.sem2;

import com.mipt.sem2.dto.*;
import com.mipt.sem2.model.Priority;
import com.mipt.sem2.model.Task;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class TaskMapperTest {

  @Autowired
  private TaskMapper taskMapper;

  @Test
  void toEntity_ShouldMapCreateDtoToTask() {
    TaskCreateDto dto = new TaskCreateDto();
    dto.setTitle("Test Task");
    dto.setDescription("Test Description");
    dto.setDueDate(LocalDate.now().plusDays(1));
    dto.setPriority(Priority.HIGH);
    dto.setTags(Set.of("work", "urgent"));

    Task task = taskMapper.toEntity(dto);

    assertNotNull(task);
    assertNull(task.getId());
    assertEquals("Test Task", task.getTitle());
    assertEquals("Test Description", task.getDescription());
    assertEquals(LocalDate.now().plusDays(1), task.getDueDate());
    assertEquals(Priority.HIGH, task.getPriority());
    assertEquals(2, task.getTags().size());
    assertTrue(task.getTags().contains("work"));
    assertTrue(task.getTags().contains("urgent"));
    assertFalse(task.isCompleted());
    assertNotNull(task.getCreatedAt());
  }

  @Test
  void updateEntity_ShouldUpdateOnlyNonNullFields() {
    Task task = new Task();
    task.setId(1L);
    task.setTitle("Old Title");
    task.setDescription("Old Description");
    task.setDueDate(LocalDate.now());
    task.setPriority(Priority.LOW);
    task.setTags(Set.of("old"));
    task.setCompleted(false);
    task.setCreatedAt(LocalDateTime.now());

    TaskUpdateDto dto = new TaskUpdateDto();
    dto.setTitle("New Title");
    dto.setPriority(Priority.HIGH);
    dto.setCompleted(true);

    taskMapper.updateEntity(dto, task);

    assertEquals("New Title", task.getTitle());
    assertEquals("Old Description", task.getDescription());
    assertEquals(Priority.HIGH, task.getPriority());
    assertTrue(task.isCompleted());
    assertEquals(LocalDate.now(), task.getDueDate());
    assertEquals(1, task.getTags().size());
  }

  @Test
  void toResponseDto_ShouldMapTaskToResponseDto() {
    Task task = new Task();
    task.setId(1L);
    task.setTitle("Test Task");
    task.setDescription("Test Description");
    task.setCompleted(true);
    task.setCreatedAt(LocalDateTime.now());
    task.setDueDate(LocalDate.now().plusDays(1));
    task.setPriority(Priority.MEDIUM);
    task.setTags(Set.of("test"));

    TaskResponseDto dto = taskMapper.toResponseDto(task);

    assertEquals(1L, dto.getId());
    assertEquals("Test Task", dto.getTitle());
    assertEquals("Test Description", dto.getDescription());
    assertTrue(dto.isCompleted());
    assertEquals(task.getCreatedAt(), dto.getCreatedAt());
    assertEquals(task.getDueDate(), dto.getDueDate());
    assertEquals(Priority.MEDIUM, dto.getPriority());
    assertEquals(1, dto.getTags().size());
  }
}