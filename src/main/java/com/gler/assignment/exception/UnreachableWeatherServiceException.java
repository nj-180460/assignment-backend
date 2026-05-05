package com.gler.assignment.exception;

import org.springframework.web.client.ResourceAccessException;

public class UnreachableWeatherServiceException extends ResourceAccessException {

    private final int statusCode;
    private final String error;

    public UnreachableWeatherServiceException(String message, int statusCode, String error) {
        super(message);
        this.statusCode = statusCode;
        this.error = error;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getError() { return error; }
}
