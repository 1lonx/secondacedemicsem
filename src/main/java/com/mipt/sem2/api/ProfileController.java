package com.mipt.sem2.api;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class ProfileController {

    @GetMapping("/api/v1/profile")
    public ResponseEntity<Map<String, Object>> profile(Authentication auth) {
        return ResponseEntity.ok(Map.of(
                "username", auth.getName(),
                "authorities", auth.getAuthorities().stream().map(a -> a.getAuthority()).toList()
        ));
    }
}
