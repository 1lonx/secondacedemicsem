package com.mipt.sem2.service;

import com.mipt.sem2.entity.Task;
import com.mipt.sem2.model.Priority;
import com.mipt.sem2.repository.TaskRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class TaskStatisticsJdbcServiceTest {

    @Autowired
    private TaskStatisticsJdbcService statisticsService;

    @Autowired
    private TaskRepository taskRepository;

    @Test
    void shouldReturnCountByPriority() {
        Task low = new Task();
        low.setTitle("Low");
        low.setPriority(Priority.LOW);
        low.setDueDate(LocalDate.now());
        taskRepository.save(low);

        Task medium = new Task();
        medium.setTitle("Medium");
        medium.setPriority(Priority.MEDIUM);
        medium.setDueDate(LocalDate.now());
        taskRepository.save(medium);

        Task high = new Task();
        high.setTitle("High");
        high.setPriority(Priority.HIGH);
        high.setDueDate(LocalDate.now());
        taskRepository.save(high);

        Task high2 = new Task();
        high2.setTitle("High2");
        high2.setPriority(Priority.HIGH);
        high2.setDueDate(LocalDate.now());
        taskRepository.save(high2);

        Map<Priority, Long> stats = statisticsService.getTasksCountByPriority();
        assertThat(stats.get(Priority.LOW)).isEqualTo(1L);
        assertThat(stats.get(Priority.MEDIUM)).isEqualTo(1L);
        assertThat(stats.get(Priority.HIGH)).isEqualTo(2L);
    }
}
