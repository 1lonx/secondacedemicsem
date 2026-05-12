package com.mipt.sem2.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class DocsController {

    @GetMapping("/api/v1/docs")
    public ResponseEntity<Map<String, String>> docs() {
        return ResponseEntity.ok(Map.of(
                "message", "API documentation",
                "version", "1.0",
                "tasks", "/api/v1/tasks"
        ));
    }
}
