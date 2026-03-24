package com.mipt.sem2.controller;

import com.mipt.sem2.dto.TaskResponseDto;
import com.mipt.sem2.mapper.TaskMapper;
import com.mipt.sem2.service.FavoritesService;
import com.mipt.sem2.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/favorites")
@RequiredArgsConstructor
public class FavoritesController {
  private final FavoritesService favoritesService;
  private final TaskService taskService;
  private final TaskMapper taskMapper;

  @Value("${app.api.version:2.0.0}")
  private String apiVersion;

  @PostMapping("/{taskId}")
  @Operation(summary = "Add task to favorites")
  public ResponseEntity<Void> addFavorite(@PathVariable Long taskId, HttpSession session) {
    favoritesService.addFavorite(session, taskId);
    return ResponseEntity.ok().header("X-API-Version", apiVersion).build();
  }

  @DeleteMapping("/{taskId}")
  @Operation(summary = "Remove task from favorites")
  public ResponseEntity<Void> removeFavorite(@PathVariable Long taskId, HttpSession session) {
    favoritesService.removeFavorite(session, taskId);
    return ResponseEntity.ok().header("X-API-Version", apiVersion).build();
  }

  @GetMapping
  @Operation(summary = "Get favorite tasks")
  public ResponseEntity<List<TaskResponseDto>> getFavorites(HttpSession session) {
    List<TaskResponseDto> favorites = favoritesService.getFavorites(session).stream()
        .map(taskService::findById)
        .map(taskMapper::toResponseDto)
        .collect(Collectors.toList());
    return ResponseEntity.ok()
        .header("X-API-Version", apiVersion)
        .body(favorites);
  }
}