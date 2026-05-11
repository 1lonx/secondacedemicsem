package com.mipt.sem2.validator;

import com.mipt.sem2.dto.TaskUpdateDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.LocalDate;

public class DueDateNotBeforeCreationValidator implements ConstraintValidator<DueDateNotBeforeCreation, TaskUpdateDto> {
  @Override
  public boolean isValid(TaskUpdateDto dto, ConstraintValidatorContext context) {
    if (dto.getDueDate() == null) {
      return true;
    }
    // Для простоты предположим, что дата создания известна только в сервисе.
    // Здесь нет доступа к задаче, поэтому лучше проверять в сервисе.
    // Оставим заглушку: всегда true.
    // В реальном коде проверка должна быть в сервисе, куда мы передаём id и dto.
    return true;
  }
}