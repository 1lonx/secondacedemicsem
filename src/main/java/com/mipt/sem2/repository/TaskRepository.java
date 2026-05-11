package com.mipt.sem2.repository;

import com.mipt.sem2.entity.Task;
import com.mipt.sem2.model.Priority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

  List<Task> findByCompletedAndPriority(boolean completed, Priority priority);

  @Query("SELECT t FROM Task t WHERE t.dueDate BETWEEN :today AND :nextWeek")
  List<Task> findTasksDueWithinNextDays(@Param("today") LocalDate today, @Param("nextWeek") LocalDate nextWeek);

  @Query("SELECT DISTINCT t FROM Task t LEFT JOIN FETCH t.attachments WHERE t.id = :id")
  Task findByIdWithAttachments(@Param("id") Long id);
}
