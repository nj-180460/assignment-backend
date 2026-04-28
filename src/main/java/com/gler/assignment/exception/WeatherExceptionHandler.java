package com.gler.assignment.exception;

import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.DefaultResponseErrorHandler;

import java.io.IOException;

public class WeatherExceptionHandler extends DefaultResponseErrorHandler {

    @Override
    public void handleError(ClientHttpResponse response) throws IOException {
        int code = response.getStatusCode().value();
        if (response.getStatusCode().is5xxServerError()) {
            throw new WeatherServiceException("The Weather Service is currently unavailable.", code);
        } else if (response.getStatusCode().is4xxClientError()) {
            throw new WeatherServiceException("We couldn't process the weather request. Please check your inputs.", code);
        }
    }
}
