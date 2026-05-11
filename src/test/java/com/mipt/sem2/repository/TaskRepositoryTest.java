package com.mipt.sem2.repository;

import com.mipt.sem2.entity.Task;
import com.mipt.sem2.entity.TaskAttachment;
import com.mipt.sem2.model.Priority;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDate;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class TaskRepositoryTest {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void shouldSaveAndFindTask() {
        Task task = new Task();
        task.setTitle("Test Task");
        task.setPriority(Priority.HIGH);
        task.setDueDate(LocalDate.now().plusDays(1));
        task.setTags(Set.of("tag1", "tag2"));

        Task saved = taskRepository.save(task);
        entityManager.flush();

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getCreatedAt()).isNotNull();

        Task found = taskRepository.findById(saved.getId()).orElse(null);
        assertThat(found).isNotNull();
        assertThat(found.getTags()).contains("tag1", "tag2");
    }

    @Test
    void shouldFindByCompletedAndPriority() {
        Task task1 = new Task();
        task1.setTitle("Completed HIGH");
        task1.setCompleted(true);
        task1.setPriority(Priority.HIGH);
        taskRepository.save(task1);

        Task task2 = new Task();
        task2.setTitle("Not completed HIGH");
        task2.setCompleted(false);
        task2.setPriority(Priority.HIGH);
        taskRepository.save(task2);

        var result = taskRepository.findByCompletedAndPriority(true, Priority.HIGH);
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("Completed HIGH");
    }

    @Test
    void shouldFindTasksDueWithinNextDays() {
        Task task1 = new Task();
        task1.setTitle("Due tomorrow");
        task1.setDueDate(LocalDate.now().plusDays(1));
        taskRepository.save(task1);

        Task task2 = new Task();
        task2.setTitle("Due in 10 days");
        task2.setDueDate(LocalDate.now().plusDays(10));
        taskRepository.save(task2);

        LocalDate today = LocalDate.now();
        LocalDate nextWeek = today.plusDays(7);
        var result = taskRepository.findTasksDueWithinNextDays(today, nextWeek);
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("Due tomorrow");
    }

    @Test
    void shouldFetchTaskWithAttachmentsWithoutNPlusOne() {
        Task task = new Task();
        task.setTitle("Parent");
        taskRepository.save(task);

        TaskAttachment att1 = new TaskAttachment();
        att1.setTask(task);
        att1.setFileName("file1.txt");
        att1.setStoredFileName("uuid1.txt");
        att1.setSize(100);
        entityManager.persist(att1);

        TaskAttachment att2 = new TaskAttachment();
        att2.setTask(task);
        att2.setFileName("file2.txt");
        att2.setStoredFileName("uuid2.txt");
        att2.setSize(200);
        entityManager.persist(att2);

        entityManager.flush();
        entityManager.clear();

        Task fetched = taskRepository.findByIdWithAttachments(task.getId());
        assertThat(fetched.getAttachments()).hasSize(2);
    }
}
