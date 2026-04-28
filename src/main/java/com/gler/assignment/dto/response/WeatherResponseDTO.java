package com.gler.assignment.dto.response;

public record WeatherResponseDTO
        (
                String temperatureTime,
                double temperature,
                String relativeHumidityTime,
                double relativeHumidity,
                String windSpeedTime,
                double windSpeed
        )
{ }
