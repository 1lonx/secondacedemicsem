package com.mipt.sem2.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.Instant;
import java.util.Map;

@Data
@AllArgsConstructor
public class ErrorResponse {
  private Instant timestamp;
  private int status;
  private String error;
  private String message;
  private String path;
  private Map<String, Object> details;
}
