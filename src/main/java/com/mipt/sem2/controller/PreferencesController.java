package com.mipt.sem2.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/preferences")
public class PreferencesController {

  @Value("${app.api.version:2.0.0}")
  private String apiVersion;

  @GetMapping("/view")
  @Operation(summary = "Get view preference from cookie")
  public ResponseEntity<String> getViewPreference(@CookieValue(name = "viewPreference", defaultValue = "compact") String mode) {
    return ResponseEntity.ok()
        .header("X-API-Version", apiVersion)
        .body(mode);
  }

  @PostMapping("/view")
  @Operation(summary = "Set view preference")
  public ResponseEntity<Void> setViewPreference(@RequestParam String mode, HttpServletResponse response) {
    Cookie cookie = new Cookie("viewPreference", mode);
    cookie.setPath("/");
    cookie.setMaxAge(60 * 60 * 24 * 365); // 1 year
    response.addCookie(cookie);
    return ResponseEntity.ok()
        .header("X-API-Version", apiVersion)
        .build();
  }
}