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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AttachmentControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  private Long taskId;

  @BeforeEach
  void setUp() throws Exception {
    TaskCreateDto createDto = new TaskCreateDto();
    createDto.setTitle("Task with Attachments");
    createDto.setDueDate(LocalDate.now().plusDays(1));
    createDto.setPriority(Priority.MEDIUM);

    String response = mockMvc.perform(post("/api/tasks")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(createDto)))
        .andExpect(status().isCreated())
        .andReturn().getResponse().getContentAsString();

    taskId = objectMapper.readTree(response).get("id").asLong();
  }

  @Test
  void uploadAttachment_WithValidFile_ShouldReturn201() throws Exception {
    MockMultipartFile file = new MockMultipartFile(
        "file",
        "test.txt",
        MediaType.TEXT_PLAIN_VALUE,
        "Hello World".getBytes()
    );

    mockMvc.perform(multipart("/api/tasks/{taskId}/attachments", taskId)
            .file(file))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.fileName").value("test.txt"))
        .andExpect(jsonPath("$.size").value(11))
        .andExpect(jsonPath("$.uploadedAt").exists());
  }

  @Test
  void uploadAttachment_WithEmptyFile_ShouldReturn200() throws Exception {
    MockMultipartFile file = new MockMultipartFile(
        "file",
        "empty.txt",
        MediaType.TEXT_PLAIN_VALUE,
        new byte[0]
    );

    mockMvc.perform(multipart("/api/tasks/{taskId}/attachments", taskId)
            .file(file))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.fileName").value("empty.txt"))
        .andExpect(jsonPath("$.size").value(0));
  }

  @Test
  void uploadAttachment_WithNonExistingTask_ShouldReturn404() throws Exception {
    MockMultipartFile file = new MockMultipartFile(
        "file",
        "test.txt",
        MediaType.TEXT_PLAIN_VALUE,
        "Hello".getBytes()
    );

    mockMvc.perform(multipart("/api/tasks/{taskId}/attachments", 99999)
            .file(file))
        .andExpect(status().isNotFound());
  }

  @Test
  void listAttachments_ShouldReturn200AndList() throws Exception {
    MockMultipartFile file = new MockMultipartFile(
        "file",
        "test.txt",
        MediaType.TEXT_PLAIN_VALUE,
        "Hello".getBytes()
    );

    mockMvc.perform(multipart("/api/tasks/{taskId}/attachments", taskId)
            .file(file))
        .andExpect(status().isCreated());

    mockMvc.perform(get("/api/tasks/{taskId}/attachments", taskId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$[0].fileName").value("test.txt"));
  }

  @Test
  void listAttachments_WithNoAttachments_ShouldReturn200AndEmptyList() throws Exception {
    mockMvc.perform(get("/api/tasks/{taskId}/attachments", taskId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$.length()").value(0));
  }

  @Test
  void downloadAttachment_WithExistingId_ShouldReturn200AndFile() throws Exception {
    MockMultipartFile file = new MockMultipartFile(
        "file",
        "download.txt",
        MediaType.TEXT_PLAIN_VALUE,
        "Download content".getBytes()
    );

    String response = mockMvc.perform(multipart("/api/tasks/{taskId}/attachments", taskId)
            .file(file))
        .andExpect(status().isCreated())
        .andReturn().getResponse().getContentAsString();

    Long attachmentId = objectMapper.readTree(response).get("id").asLong();

    mockMvc.perform(get("/api/attachments/{attachmentId}", attachmentId))
        .andExpect(status().isOk())
        .andExpect(header().exists("Content-Disposition"));
  }

  @Test
  void downloadAttachment_WithNonExistingId_ShouldReturn404() throws Exception {
    mockMvc.perform(get("/api/attachments/99999"))
        .andExpect(status().isNotFound());
  }

  @Test
  void deleteAttachment_WithExistingId_ShouldReturn204() throws Exception {
    MockMultipartFile file = new MockMultipartFile(
        "file",
        "delete.txt",
        MediaType.TEXT_PLAIN_VALUE,
        "To delete".getBytes()
    );

    String response = mockMvc.perform(multipart("/api/tasks/{taskId}/attachments", taskId)
            .file(file))
        .andExpect(status().isCreated())
        .andReturn().getResponse().getContentAsString();

    Long attachmentId = objectMapper.readTree(response).get("id").asLong();

    mockMvc.perform(delete("/api/attachments/{attachmentId}", attachmentId))
        .andExpect(status().isNoContent());
  }

  @Test
  void deleteAttachment_WithNonExistingId_ShouldReturn404() throws Exception {
    mockMvc.perform(delete("/api/attachments/99999"))
        .andExpect(status().isNotFound());
  }
}
