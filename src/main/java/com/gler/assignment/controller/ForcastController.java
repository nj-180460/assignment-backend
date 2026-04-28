package com.gler.assignment.controller;

import com.gler.assignment.dto.request.ForecastRequest;
import com.gler.assignment.dto.response.GenericResponse;
import com.gler.assignment.dto.response.WeatherClientServiceResponse;
import com.gler.assignment.dto.response.WeatherResponseDTO;
import com.gler.assignment.service.WeatherService;
import com.gler.assignment.util.RequestIdGenerator;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/v1")
public class ForcastController {

    private final WeatherService weatherService;

    public ForcastController (WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    @PostMapping("/forecast")
    public ResponseEntity<GenericResponse<WeatherResponseDTO>> getForecast(@Valid @RequestBody ForecastRequest forecastRequest) {
        return ResponseEntity.ok(new GenericResponse<>(
                RequestIdGenerator.newRequestId(),
                "time",
                "success",
                "Successfully created weather record",
                weatherService.getWeather(forecastRequest)
        ));
    }

}
