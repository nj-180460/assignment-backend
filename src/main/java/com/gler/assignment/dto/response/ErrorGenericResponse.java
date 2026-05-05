package com.gler.assignment.dto.response;

public record ErrorGenericResponse
        (
                String timestamp,
                int status,
                String error,
                String message,
                String path
        )
{ }
