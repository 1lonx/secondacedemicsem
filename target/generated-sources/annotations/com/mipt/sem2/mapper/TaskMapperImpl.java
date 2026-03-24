package com.mipt.sem2.mapper;

import com.mipt.sem2.dto.TaskCreateDto;
import com.mipt.sem2.dto.TaskResponseDto;
import com.mipt.sem2.dto.TaskUpdateDto;
import com.mipt.sem2.model.Task;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-03-25T01:39:42+0300",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.8 (Microsoft)"
)
@Component
public class TaskMapperImpl implements TaskMapper {

    @Override
    public Task toEntity(TaskCreateDto dto) {
        if ( dto == null ) {
            return null;
        }

        Task task = new Task();

        return task;
    }

    @Override
    public void updateEntity(TaskUpdateDto dto, Task task) {
        if ( dto == null ) {
            return;
        }
    }

    @Override
    public TaskResponseDto toResponseDto(Task task) {
        if ( task == null ) {
            return null;
        }

        TaskResponseDto taskResponseDto = new TaskResponseDto();

        return taskResponseDto;
    }
}
