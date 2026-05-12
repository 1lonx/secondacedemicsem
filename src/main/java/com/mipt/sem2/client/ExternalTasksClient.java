package com.mipt.sem2.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mipt.sem2.dto.CreateTaskRequest;
import com.mipt.sem2.dto.TaskDto;
import com.mipt.sem2.exception.ExternalApiException;
import com.mipt.sem2.exception.TaskNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class ExternalTasksClient {

    private final RestClient externalRestClient;
    private final ObjectMapper objectMapper;

    public TaskDto getTask(long id) {
        return externalRestClient.get()
                .uri("/tasks/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(status -> status.value() == 404, this::handle404)
                .onStatus(HttpStatusCode::is5xxServerError, this::handle5xx)
                .body(TaskDto.class);
    }

    public ResponseEntity<TaskDto> createTask(CreateTaskRequest req) {
        return externalRestClient.post()
                .uri("/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(req)
                .retrieve()
                .onStatus(HttpStatusCode::is5xxServerError, this::handle5xx)
                .toEntity(TaskDto.class);
    }

    public List<TaskDto> listTasks(Boolean completed, Integer limit) {
        return externalRestClient.get()
                .uri(builder -> {
                    var b = builder.path("/tasks");
                    if (completed != null) b = b.queryParam("completed", completed);
                    if (limit != null) b = b.queryParam("limit", limit);
                    return b.build();
                })
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(HttpStatusCode::is5xxServerError, this::handle5xx)
                .body(new ParameterizedTypeReference<List<TaskDto>>() {});
    }

    public void deleteTask(long id) {
        externalRestClient.delete()
                .uri("/tasks/{id}", id)
                .retrieve()
                .onStatus(status -> status.value() == 404, this::handle404)
                .onStatus(HttpStatusCode::is5xxServerError, this::handle5xx)
                .toBodilessEntity();
    }

    public String probeUnstable(String mode) {
        return externalRestClient.get()
                .uri(b -> b.path("/unstable").queryParam("mode", mode).build())
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(status -> status.value() == 429, (req, res) -> {
                    String retryAfter = res.getHeaders().getFirst("Retry-After");
                    throw new ExternalApiException("External rate limit hit, Retry-After: " + retryAfter);
                })
                .onStatus(HttpStatusCode::is5xxServerError, this::handle5xx)
                .body(String.class);
    }

    private void handle404(HttpRequest req, ClientHttpResponse res) throws IOException {
        byte[] bytes = res.getBody().readAllBytes();
        try {
            ProblemDetail pd = objectMapper.readValue(bytes, ProblemDetail.class);
            String detail = pd.getDetail() != null ? pd.getDetail() : "Task not found";
            throw new TaskNotFoundException(detail);
        } catch (JsonProcessingException e) {
            throw new TaskNotFoundException("Task not found");
        }
    }

    private void handle5xx(HttpRequest req, ClientHttpResponse res) throws IOException {
        MediaType contentType = res.getHeaders().getContentType();
        byte[] bytes = res.getBody().readAllBytes();
        String body = new String(bytes, StandardCharsets.UTF_8);
        String snippet = body.length() > 300 ? body.substring(0, 300) + "..." : body;

        if (contentType == null || !contentType.isCompatibleWith(MediaType.APPLICATION_JSON)) {
            log.warn("External API returned non-JSON response ({}): {}", contentType, snippet);
        } else {
            log.warn("External API {} error: {}", res.getStatusCode(), snippet);
        }

        throw new ExternalApiException("External service returned " + res.getStatusCode());
    }
}
