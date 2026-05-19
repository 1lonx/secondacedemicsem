package com.mipt.sem2.repository;

import com.mipt.sem2.config.JpaAuditingConfig;
import com.mipt.sem2.entity.Task;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(JpaAuditingConfig.class)
class TaskRepositoryIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private TaskRepository taskRepository;

    @Test
    void findTasksDueWithinNextDays_returnsOnlyTasksInRange() {
        Task dueTomorrow = new Task();
        dueTomorrow.setTitle("Due tomorrow");
        dueTomorrow.setDueDate(LocalDate.now().plusDays(1));
        taskRepository.save(dueTomorrow);

        Task dueLater = new Task();
        dueLater.setTitle("Due in two weeks");
        dueLater.setDueDate(LocalDate.now().plusDays(14));
        taskRepository.save(dueLater);

        List<Task> result = taskRepository.findTasksDueWithinNextDays(
                LocalDate.now(),
                LocalDate.now().plusDays(7)
        );

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("Due tomorrow");
    }
}
