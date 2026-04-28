package com.gler.assignment.dto.response;

public record ErrorGenericResponse
        (
                String timestamp,
                String status,
                String error,
                String message,
                String path
        )
{ }
