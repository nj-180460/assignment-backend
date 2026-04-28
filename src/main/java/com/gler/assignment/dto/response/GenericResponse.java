package com.gler.assignment.dto.response;

public record GenericResponse <T>
        (
                String timestamp,
                String status,
                T body
        )
{ }
