package com.mipt.sem2.service;

import com.mipt.sem2.dto.AttachmentResponseDto;
import com.mipt.sem2.entity.Task;
import com.mipt.sem2.entity.TaskAttachment;
import com.mipt.sem2.exception.AttachmentNotFoundException;
import com.mipt.sem2.exception.TaskNotFoundException;
import com.mipt.sem2.repository.TaskAttachmentRepository;
import com.mipt.sem2.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AttachmentService {

  private final TaskAttachmentRepository attachmentRepository;
  private final TaskRepository taskRepository;

  @Value("${app.upload.dir:uploads}")
  private String uploadDir;

  @Transactional
  public AttachmentResponseDto storeAttachment(Long taskId, MultipartFile file) throws IOException {
    Task task = taskRepository.findById(taskId)
            .orElseThrow(() -> new TaskNotFoundException(taskId));

    Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
    Files.createDirectories(uploadPath);

    String originalFileName = file.getOriginalFilename();
    String storedFileName = UUID.randomUUID().toString() + "_" + originalFileName;
    Path targetPath = uploadPath.resolve(storedFileName);
    file.transferTo(targetPath);

    TaskAttachment attachment = new TaskAttachment();
    attachment.setTask(task);
    attachment.setFileName(originalFileName);
    attachment.setStoredFileName(storedFileName);
    attachment.setContentType(file.getContentType());
    attachment.setSize(file.getSize());

    TaskAttachment saved = attachmentRepository.save(attachment);

    AttachmentResponseDto dto = new AttachmentResponseDto();
    dto.setId(saved.getId());
    dto.setFileName(saved.getFileName());
    dto.setSize(saved.getSize());
    dto.setUploadedAt(saved.getUploadedAt());
    return dto;
  }

  @Transactional(readOnly = true)
  public Resource loadAsResource(Long attachmentId) throws IOException {
    TaskAttachment attachment = attachmentRepository.findById(attachmentId)
            .orElseThrow(() -> new AttachmentNotFoundException(attachmentId));
    Path filePath = Paths.get(uploadDir).resolve(attachment.getStoredFileName()).normalize();
    Resource resource = new UrlResource(filePath.toUri());
    if (resource.exists() && resource.isReadable()) {
      return resource;
    } else {
      throw new IOException("File not found or not readable");
    }
  }

  @Transactional
  public void deleteAttachment(Long attachmentId) throws IOException {
    TaskAttachment attachment = attachmentRepository.findById(attachmentId)
            .orElseThrow(() -> new AttachmentNotFoundException(attachmentId));
    Path filePath = Paths.get(uploadDir).resolve(attachment.getStoredFileName()).normalize();
    Files.deleteIfExists(filePath);
    attachmentRepository.delete(attachment);
  }

  @Transactional(readOnly = true)
  public List<AttachmentResponseDto> getAttachmentsByTaskId(Long taskId) {
    if (!taskRepository.existsById(taskId)) {
      throw new TaskNotFoundException(taskId);
    }
    return attachmentRepository.findByTaskId(taskId).stream()
            .map(att -> {
              AttachmentResponseDto dto = new AttachmentResponseDto();
              dto.setId(att.getId());
              dto.setFileName(att.getFileName());
              dto.setSize(att.getSize());
              dto.setUploadedAt(att.getUploadedAt());
              return dto;
            })
            .collect(Collectors.toList());
  }
}
