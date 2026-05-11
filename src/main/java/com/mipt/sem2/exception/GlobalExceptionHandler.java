package com.mipt.sem2.exception;

import com.mipt.sem2.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(TaskNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleTaskNotFound(TaskNotFoundException ex, HttpServletRequest request) {
    return buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage(), request);
  }

  @ExceptionHandler(AttachmentNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleAttachmentNotFound(AttachmentNotFoundException ex, HttpServletRequest request) {
    return buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage(), request);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex, HttpServletRequest request) {
    Map<String, Object> details = ex.getBindingResult().getFieldErrors().stream()
        .collect(Collectors.toMap(
            FieldError::getField,
            FieldError::getDefaultMessage,
            (msg1, msg2) -> msg1 + "; " + msg2
        ));
    return buildErrorResponse(HttpStatus.BAD_REQUEST, "Validation failed", request, details);
  }

  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException ex, HttpServletRequest request) {
    Map<String, Object> details = ex.getConstraintViolations().stream()
        .collect(Collectors.toMap(
            violation -> violation.getPropertyPath().toString(),
            violation -> violation.getMessage(),
            (msg1, msg2) -> msg1 + "; " + msg2
        ));
    return buildErrorResponse(HttpStatus.BAD_REQUEST, "Constraint violation", request, details);
  }

  @ExceptionHandler(MissingServletRequestParameterException.class)
  public ResponseEntity<ErrorResponse> handleMissingParams(MissingServletRequestParameterException ex, HttpServletRequest request) {
    Map<String, Object> details = new HashMap<>();
    details.put(ex.getParameterName(), "Missing parameter");
    return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), request, details);
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<ErrorResponse> handleNotReadable(HttpMessageNotReadableException ex, HttpServletRequest request) {
    return buildErrorResponse(HttpStatus.BAD_REQUEST, "Malformed JSON request", request);
  }

  @ExceptionHandler(NoHandlerFoundException.class)
  public ResponseEntity<ErrorResponse> handleNotFound(NoHandlerFoundException ex, HttpServletRequest request) {
    return buildErrorResponse(HttpStatus.NOT_FOUND, "Endpoint not found", request);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleGeneric(Exception ex, HttpServletRequest request) {
    // In production you might not want to expose stack trace
    return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error", request);
  }

  private ResponseEntity<ErrorResponse> buildErrorResponse(HttpStatus status, String message, HttpServletRequest request) {
    return buildErrorResponse(status, message, request, null);
  }

  private ResponseEntity<ErrorResponse> buildErrorResponse(HttpStatus status, String message, HttpServletRequest request, Map<String, Object> details) {
    ErrorResponse error = new ErrorResponse(
        Instant.now(),
        status.value(),
        status.getReasonPhrase(),
        message,
        request.getRequestURI(),
        details != null ? details : new HashMap<>()
    );
    return new ResponseEntity<>(error, status);
  }
}