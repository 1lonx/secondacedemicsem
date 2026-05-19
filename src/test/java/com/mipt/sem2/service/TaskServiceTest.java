package com.mipt.sem2.service;

import com.mipt.sem2.dto.TaskUpdateDto;
import com.mipt.sem2.entity.Task;
import com.mipt.sem2.model.Priority;
import com.mipt.sem2.repository.TaskRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
class TaskServiceTest {

    @Autowired
    private TaskService taskService;

    @MockBean
    private TaskRepository taskRepository;

    @Test
    void update_completedStatus_persistsChange() {
        Task existing = new Task();
        existing.setTitle("Fix the bug");
        existing.setPriority(Priority.HIGH);
        existing.setDueDate(LocalDate.now().plusDays(3));

        TaskUpdateDto dto = new TaskUpdateDto();
        dto.setCompleted(true);

        ArgumentCaptor<Task> captor = ArgumentCaptor.forClass(Task.class);
        when(taskRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(taskRepository.save(any(Task.class))).thenAnswer(inv -> inv.getArgument(0));

        Task result = taskService.update(1L, dto);

        verify(taskRepository).save(captor.capture());
        assertThat(captor.getValue().isCompleted()).isTrue();
        assertThat(result.isCompleted()).isTrue();
        assertThat(result.getTitle()).isEqualTo("Fix the bug");
    }
}
