package com.mipt.sem2.controller;

import com.mipt.sem2.dto.AttachmentResponseDto;
import com.mipt.sem2.service.AttachmentService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class AttachmentController {
  private final AttachmentService attachmentService;

  @Value("${app.api.version:2.0.0}")
  private String apiVersion;

  @PostMapping("/api/tasks/{taskId}/attachments")
  @Operation(summary = "Upload attachment for a task")
  public ResponseEntity<AttachmentResponseDto> uploadAttachment(@PathVariable Long taskId,
      @RequestParam("file") MultipartFile file) throws IOException {
    AttachmentResponseDto dto = attachmentService.storeAttachment(taskId, file);
    return ResponseEntity.status(HttpStatus.CREATED)
        .header("X-API-Version", apiVersion)
        .body(dto);
  }

  @GetMapping("/api/attachments/{attachmentId}")
  @Operation(summary = "Download attachment")
  public ResponseEntity<Resource> downloadAttachment(@PathVariable Long attachmentId) throws IOException {
    Resource resource = attachmentService.loadAsResource(attachmentId);
    String fileName = resource.getFilename();
    return ResponseEntity.ok()
        .contentType(MediaType.APPLICATION_OCTET_STREAM)
        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
        .header("X-API-Version", apiVersion)
        .body(resource);
  }

  @DeleteMapping("/api/attachments/{attachmentId}")
  @Operation(summary = "Delete attachment")
  public ResponseEntity<Void> deleteAttachment(@PathVariable Long attachmentId) throws IOException {
    attachmentService.deleteAttachment(attachmentId);
    return ResponseEntity.noContent()
        .header("X-API-Version", apiVersion)
        .build();
  }

  @GetMapping("/api/tasks/{taskId}/attachments")
  @Operation(summary = "List attachments for a task")
  public ResponseEntity<List<AttachmentResponseDto>> listAttachments(@PathVariable Long taskId) {
    List<AttachmentResponseDto> attachments = attachmentService.getAttachmentsByTaskId(taskId);
    return ResponseEntity.ok()
        .header("X-API-Version", apiVersion)
        .body(attachments);
  }
}