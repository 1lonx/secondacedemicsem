package com.mipt.sem2;

import com.mipt.sem2.dto.TaskCreateDto;
import com.mipt.sem2.dto.TaskUpdateDto;
import com.mipt.sem2.exception.TaskNotFoundException;
import com.mipt.sem2.model.Priority;
import com.mipt.sem2.model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class TaskServiceTest {

  @Autowired
  private TaskService taskService;

  private TaskCreateDto createDto;
  private TaskUpdateDto updateDto;

  @BeforeEach
  void setUp() {
    createDto = new TaskCreateDto();
    createDto.setTitle("Service Test Task");
    createDto.setDescription("Test Description");
    createDto.setDueDate(LocalDate.now().plusDays(1));
    createDto.setPriority(Priority.MEDIUM);
    createDto.setTags(Set.of("test"));

    updateDto = new TaskUpdateDto();
    updateDto.setTitle("Updated Service Task");
    updateDto.setCompleted(true);
  }

  @Test
  void create_ShouldCreateAndReturnTask() {
    Task task = taskService.create(createDto);

    assertNotNull(task.getId());
    assertEquals("Service Test Task", task.getTitle());
    assertEquals("Test Description", task.getDescription());
    assertEquals(Priority.MEDIUM, task.getPriority());
    assertNotNull(task.getCreatedAt());
    assertFalse(task.isCompleted());
  }

  @Test
  void findAll_ShouldReturnAllTasks() {
    int initialSize = taskService.findAll().size();

    taskService.create(createDto);
    taskService.create(createDto);

    assertEquals(initialSize + 2, taskService.findAll().size());
  }

  @Test
  void findById_WithExistingId_ShouldReturnTask() {
    Task created = taskService.create(createDto);
    Task found = taskService.findById(created.getId());

    assertEquals(created.getId(), found.getId());
    assertEquals(created.getTitle(), found.getTitle());
  }

  @Test
  void findById_WithNonExistingId_ShouldThrowException() {
    assertThrows(TaskNotFoundException.class, () -> taskService.findById(99999L));
  }

  @Test
  void update_ShouldUpdateTaskFields() {
    Task created = taskService.create(createDto);
    Task updated = taskService.update(created.getId(), updateDto);

    assertEquals("Updated Service Task", updated.getTitle());
    assertTrue(updated.isCompleted());
    assertEquals(created.getDescription(), updated.getDescription());
    assertEquals(created.getPriority(), updated.getPriority());
  }

  @Test
  void update_WithNonExistingId_ShouldThrowException() {
    assertThrows(TaskNotFoundException.class, () -> taskService.update(99999L, updateDto));
  }

  @Test
  void delete_ShouldRemoveTask() {
    Task created = taskService.create(createDto);
    int sizeAfterCreate = taskService.findAll().size();

    taskService.delete(created.getId());

    assertEquals(sizeAfterCreate - 1, taskService.findAll().size());
    assertThrows(TaskNotFoundException.class, () -> taskService.findById(created.getId()));
  }

  @Test
  void delete_WithNonExistingId_ShouldThrowException() {
    assertThrows(TaskNotFoundException.class, () -> taskService.delete(99999L));
  }
}