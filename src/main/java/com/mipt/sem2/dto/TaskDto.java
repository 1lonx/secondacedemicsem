package com.mipt.sem2.dto;

public record TaskDto(
        long id,
        String title,
        boolean completed,
        String description
) {}
