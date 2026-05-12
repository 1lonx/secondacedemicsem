package com.mipt.sem2.api;

import com.mipt.sem2.dto.CreateTaskRequest;
import com.mipt.sem2.dto.TaskDto;
import com.mipt.sem2.service.TasksGatewayService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tasks")
@RequiredArgsConstructor
public class TasksController {

    private final TasksGatewayService service;

    @PostMapping
    public ResponseEntity<TaskDto> create(@RequestBody @Valid CreateTaskRequest req) {
        return service.createTask(req);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskDto> getById(@PathVariable long id) {
        return ResponseEntity.ok(service.getTask(id));
    }

    @GetMapping
    public ResponseEntity<List<TaskDto>> list(
            @RequestParam(required = false) Boolean completed,
            @RequestParam(required = false) Integer limit) {
        return ResponseEntity.ok(service.listTasks(completed, limit));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable long id) {
        service.deleteTask(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/probe")
    public ResponseEntity<String> probe(@RequestParam String mode) {
        return ResponseEntity.ok(service.probeUnstable(mode));
    }
}
