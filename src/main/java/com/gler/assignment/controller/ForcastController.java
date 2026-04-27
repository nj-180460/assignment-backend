package com.gler.assignment.controller;

import com.gler.assignment.dto.request.ForecastRequest;
import com.gler.assignment.dto.response.GenericResponse;
import com.gler.assignment.service.WeatherService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Timestamp;

@RestController
@RequestMapping("/api/v1")
public class ForcastController {

    private final WeatherService weatherService;

    public ForcastController (WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    @PostMapping("/forecast")
    public ResponseEntity<GenericResponse<Object>> getForecast(@Valid @RequestBody ForecastRequest forecastRequest) {

        GenericResponse<Object> ok = new GenericResponse<>(
                "time",
                "success",
                weatherService.getWeather(forecastRequest)
        );

        return ResponseEntity.ok(ok);
    }

}
