package com.mipt.sem2.exception;

public class AttachmentNotFoundException extends RuntimeException {
  public AttachmentNotFoundException(Long id) {
    super("Attachment with id " + id + " not found");
  }
}