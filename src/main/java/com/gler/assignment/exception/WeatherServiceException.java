package com.gler.assignment.exception;

public class WeatherServiceException extends RuntimeException {

    private final int statusCode;

    public WeatherServiceException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
