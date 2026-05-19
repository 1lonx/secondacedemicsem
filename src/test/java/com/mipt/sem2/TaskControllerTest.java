package com.mipt.sem2;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mipt.sem2.dto.TaskCreateDto;
import com.mipt.sem2.dto.TaskUpdateDto;
import com.mipt.sem2.model.Priority;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser
class TaskControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  private TaskCreateDto validCreateDto;
  private TaskUpdateDto validUpdateDto;

  @BeforeEach
  void setUp() {
    validCreateDto = new TaskCreateDto();
    validCreateDto.setTitle("Valid Task");
    validCreateDto.setDescription("Valid Description");
    validCreateDto.setDueDate(LocalDate.now().plusDays(1));
    validCreateDto.setPriority(Priority.MEDIUM);
    validCreateDto.setTags(Set.of("test"));

    validUpdateDto = new TaskUpdateDto();
    validUpdateDto.setTitle("Updated Task");
    validUpdateDto.setDescription("Updated Description");
    validUpdateDto.setCompleted(true);
    validUpdateDto.setDueDate(LocalDate.now().plusDays(2));
    validUpdateDto.setPriority(Priority.HIGH);
  }

  @Test
  void getAllTasks_ShouldReturn200AndTaskList() throws Exception {
    mockMvc.perform(get("/api/tasks"))
            .andExpect(status().isOk())
            .andExpect(header().exists("X-Total-Count"))
            .andExpect(header().exists("X-API-Version"))
            .andExpect(jsonPath("$").isArray());
  }

  @Test
  void createTask_WithValidData_ShouldReturn201() throws Exception {
    mockMvc.perform(post("/api/tasks")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(validCreateDto)))
            .andExpect(status().isCreated())
            .andExpect(header().exists("X-API-Version"))
            .andExpect(jsonPath("$.title").value("Valid Task"))
            .andExpect(jsonPath("$.priority").value("MEDIUM"));
  }

  @Test
  void createTask_WithBlankTitle_ShouldReturn400() throws Exception {
    TaskCreateDto invalidDto = new TaskCreateDto();
    invalidDto.setTitle("");
    invalidDto.setDueDate(LocalDate.now().plusDays(1));
    invalidDto.setPriority(Priority.MEDIUM);

    mockMvc.perform(post("/api/tasks")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(invalidDto)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400));
  }

  @Test
  void createTask_WithTitleTooShort_ShouldReturn400() throws Exception {
    TaskCreateDto invalidDto = new TaskCreateDto();
    invalidDto.setTitle("ab");
    invalidDto.setDueDate(LocalDate.now().plusDays(1));
    invalidDto.setPriority(Priority.MEDIUM);

    mockMvc.perform(post("/api/tasks")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(invalidDto)))
            .andExpect(status().isBadRequest());
  }

  @Test
  void createTask_WithPastDueDate_ShouldReturn400() throws Exception {
    TaskCreateDto invalidDto = new TaskCreateDto();
    invalidDto.setTitle("Valid Title");
    invalidDto.setDueDate(LocalDate.now().minusDays(1));
    invalidDto.setPriority(Priority.MEDIUM);

    mockMvc.perform(post("/api/tasks")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(invalidDto)))
            .andExpect(status().isBadRequest());
  }

  @Test
  void createTask_WithoutPriority_ShouldReturn400() throws Exception {
    TaskCreateDto invalidDto = new TaskCreateDto();
    invalidDto.setTitle("Valid Title");
    invalidDto.setDueDate(LocalDate.now().plusDays(1));

    mockMvc.perform(post("/api/tasks")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(invalidDto)))
            .andExpect(status().isBadRequest());
  }

  @Test
  void getTaskById_WithExistingId_ShouldReturn200() throws Exception {
    String response = mockMvc.perform(post("/api/tasks")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(validCreateDto)))
            .andExpect(status().isCreated())
            .andReturn().getResponse().getContentAsString();

    Long id = objectMapper.readTree(response).get("id").asLong();

    mockMvc.perform(get("/api/tasks/{id}", id))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(id))
            .andExpect(jsonPath("$.title").value("Valid Task"));
  }

  @Test
  void getTaskById_WithNonExistingId_ShouldReturn404() throws Exception {
    mockMvc.perform(get("/api/tasks/99999"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.status").value(404));
  }

  @Test
  void updateTask_WithValidData_ShouldReturn200() throws Exception {
    String response = mockMvc.perform(post("/api/tasks")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(validCreateDto)))
            .andExpect(status().isCreated())
            .andReturn().getResponse().getContentAsString();

    Long id = objectMapper.readTree(response).get("id").asLong();

    mockMvc.perform(put("/api/tasks/{id}", id)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(validUpdateDto)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.title").value("Updated Task"))
            .andExpect(jsonPath("$.completed").value(true));
  }

  @Test
  void updateTask_WithNonExistingId_ShouldReturn404() throws Exception {
    mockMvc.perform(put("/api/tasks/99999")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(validUpdateDto)))
            .andExpect(status().isNotFound());
  }

  @Test
  void deleteTask_WithExistingId_ShouldReturn204() throws Exception {
    String response = mockMvc.perform(post("/api/tasks")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(validCreateDto)))
            .andExpect(status().isCreated())
            .andReturn().getResponse().getContentAsString();

    Long id = objectMapper.readTree(response).get("id").asLong();

    mockMvc.perform(delete("/api/tasks/{id}", id))
            .andExpect(status().isNoContent())
            .andExpect(header().exists("X-API-Version"));
  }

  @Test
  void deleteTask_WithNonExistingId_ShouldReturn404() throws Exception {
    mockMvc.perform(delete("/api/tasks/99999"))
            .andExpect(status().isNotFound());
  }
}
