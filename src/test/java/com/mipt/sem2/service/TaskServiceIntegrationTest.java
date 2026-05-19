package com.mipt.sem2.service;

import com.mipt.sem2.dto.TaskCreateDto;
import com.mipt.sem2.exception.TaskNotFoundException;
import com.mipt.sem2.model.Priority;
import com.mipt.sem2.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
class TaskServiceIntegrationTest {

    @Autowired
    private TaskService taskService;

    @Autowired
    private TaskRepository taskRepository;

    @BeforeEach
    void setUp() {
        taskRepository.deleteAll();
    }

    @Test
    void bulkCompleteTasks_shouldCompleteAllWhenAllExist() {
        TaskCreateDto dto = new TaskCreateDto();
        dto.setTitle("Task 1");
        dto.setDueDate(LocalDate.now().plusDays(1));
        dto.setPriority(Priority.MEDIUM);
        var task1 = taskService.create(dto);

        dto.setTitle("Task 2");
        var task2 = taskService.create(dto);

        taskService.bulkCompleteTasks(List.of(task1.getId(), task2.getId()));

        assertThat(taskService.findById(task1.getId()).isCompleted()).isTrue();
        assertThat(taskService.findById(task2.getId()).isCompleted()).isTrue();
    }

    @Test
    void bulkCompleteTasks_shouldRollbackWhenAnyIdMissing() {
        TaskCreateDto dto = new TaskCreateDto();
        dto.setTitle("Valid Task");
        dto.setDueDate(LocalDate.now().plusDays(1));
        dto.setPriority(Priority.MEDIUM);
        var validTask = taskService.create(dto);

        assertThrows(TaskNotFoundException.class, () ->
                taskService.bulkCompleteTasks(List.of(validTask.getId(), 999L))
        );

        assertThat(taskService.findById(validTask.getId()).isCompleted()).isFalse();
    }
}
