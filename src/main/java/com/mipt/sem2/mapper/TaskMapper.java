package com.mipt.sem2.mapper;

import com.mipt.sem2.dto.TaskCreateDto;
import com.mipt.sem2.dto.TaskResponseDto;
import com.mipt.sem2.dto.TaskUpdateDto;
import com.mipt.sem2.model.Task;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface TaskMapper {

  Task toEntity(TaskCreateDto dto);

  void updateEntity(TaskUpdateDto dto, @MappingTarget Task task);

  TaskResponseDto toResponseDto(Task task);
}