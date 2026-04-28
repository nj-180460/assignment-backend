package com.gler.assignment.dto.response;

public record GenericResponse <T>
        (
                String requestId,
                String timestamp,
                String status,
                String message,
                T data
        )
{ }
