package com.mipt.sem2.external;

import com.mipt.sem2.dto.CreateTaskRequest;
import com.mipt.sem2.dto.TaskDto;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequestMapping("/external/v1")
public class ExternalApiController {

    private final Map<Long, TaskDto> store = new ConcurrentHashMap<>();
    private final AtomicLong idGen = new AtomicLong(1);

    @PostMapping("/tasks")
    public ResponseEntity<TaskDto> createTask(@RequestBody CreateTaskRequest req,
                                               UriComponentsBuilder ucb) {
        long id = idGen.getAndIncrement();
        TaskDto task = new TaskDto(id, req.title(), false, req.description());
        store.put(id, task);
        URI location = ucb.path("/external/v1/tasks/{id}").buildAndExpand(id).toUri();
        return ResponseEntity.created(location).body(task);
    }

    @GetMapping("/tasks/{id}")
    public ResponseEntity<?> getTask(@PathVariable long id, HttpServletRequest req) {
        TaskDto task = store.get(id);
        if (task == null) return taskNotFound(id, req);
        return ResponseEntity.ok(task);
    }

    @GetMapping("/tasks")
    public ResponseEntity<List<TaskDto>> listTasks(
            @RequestParam(required = false) Boolean completed,
            @RequestParam(required = false) Integer limit) {
        var stream = store.values().stream();
        if (completed != null) stream = stream.filter(t -> t.completed() == completed);
        if (limit != null) stream = stream.limit(limit);
        return ResponseEntity.ok(stream.toList());
    }

    @DeleteMapping("/tasks/{id}")
    public ResponseEntity<?> deleteTask(@PathVariable long id, HttpServletRequest req) {
        if (store.remove(id) == null) return taskNotFound(id, req);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/unstable")
    public ResponseEntity<?> unstable(@RequestParam String mode) throws InterruptedException {
        return switch (mode) {
            case "timeout" -> {
                Thread.sleep(60_000);
                yield ResponseEntity.ok("ok");
            }
            case "500" -> ResponseEntity.internalServerError()
                    .body(Map.of("error", "simulated internal error"));
            case "429" -> ResponseEntity.status(429)
                    .header("Retry-After", "5")
                    .body(Map.of("error", "slow down"));
            case "html" -> ResponseEntity.status(502)
                    .contentType(MediaType.TEXT_HTML)
                    .body("<html><body><h1>Bad Gateway</h1></body></html>");
            default -> ResponseEntity.badRequest()
                    .body(Map.of("error", "unknown mode: " + mode));
        };
    }

    private ResponseEntity<ProblemDetail> taskNotFound(long id, HttpServletRequest req) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, "Task " + id + " not found");
        pd.setTitle("Task Not Found");
        pd.setInstance(URI.create(req.getRequestURI()));
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(pd);
    }
}
