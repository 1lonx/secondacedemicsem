package com.mipt.sem2;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mipt.sem2.dto.TaskCreateDto;
import com.mipt.sem2.model.Priority;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class FavoritesControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  private Long taskId1;
  private Long taskId2;
  private MockHttpSession session;

  @BeforeEach
  void setUp() throws Exception {
    session = new MockHttpSession();

    TaskCreateDto createDto = new TaskCreateDto();
    createDto.setTitle("Favorite Task 1");
    createDto.setDueDate(LocalDate.now().plusDays(1));
    createDto.setPriority(Priority.HIGH);

    String response1 = mockMvc.perform(post("/api/tasks")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(createDto)))
        .andExpect(status().isCreated())
        .andReturn().getResponse().getContentAsString();
    taskId1 = objectMapper.readTree(response1).get("id").asLong();

    createDto.setTitle("Favorite Task 2");
    String response2 = mockMvc.perform(post("/api/tasks")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(createDto)))
        .andExpect(status().isCreated())
        .andReturn().getResponse().getContentAsString();
    taskId2 = objectMapper.readTree(response2).get("id").asLong();
  }

  @Test
  void addFavorite_ShouldAddTaskToSession() throws Exception {
    mockMvc.perform(post("/api/favorites/{taskId}", taskId1)
            .session(session))
        .andExpect(status().isOk())
        .andExpect(header().exists("X-API-Version"));
  }

  @Test
  void addFavorite_MultipleTasks_ShouldAddAll() throws Exception {
    mockMvc.perform(post("/api/favorites/{taskId}", taskId1).session(session))
        .andExpect(status().isOk());

    mockMvc.perform(post("/api/favorites/{taskId}", taskId2).session(session))
        .andExpect(status().isOk());

    mockMvc.perform(get("/api/favorites").session(session))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(2));
  }

  @Test
  void removeFavorite_ShouldRemoveFromSession() throws Exception {
    mockMvc.perform(post("/api/favorites/{taskId}", taskId1).session(session))
        .andExpect(status().isOk());

    mockMvc.perform(delete("/api/favorites/{taskId}", taskId1).session(session))
        .andExpect(status().isOk());

    mockMvc.perform(get("/api/favorites").session(session))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(0));
  }

  @Test
  void getFavorites_WithNoFavorites_ShouldReturnEmptyList() throws Exception {
    mockMvc.perform(get("/api/favorites").session(session))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$.length()").value(0));
  }

  @Test
  void getFavorites_WithFavorites_ShouldReturnTaskList() throws Exception {
    mockMvc.perform(post("/api/favorites/{taskId}", taskId1).session(session))
        .andExpect(status().isOk());

    mockMvc.perform(get("/api/favorites").session(session))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].id").value(taskId1))
        .andExpect(jsonPath("$[0].title").value("Favorite Task 1"));
  }

  @Test
  void addFavorite_WithNonExistingTask_ShouldReturn404() throws Exception {
    mockMvc.perform(post("/api/favorites/{taskId}", 99999).session(session))
        .andExpect(status().isNotFound());
  }
}
