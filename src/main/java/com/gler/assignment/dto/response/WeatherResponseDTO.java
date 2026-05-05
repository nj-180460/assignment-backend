package com.gler.assignment.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record WeatherResponseDTO
        (
                LocalDateTime dateCreated,
                LocalDate recordDate,
                String temperatureTime,
                Double temperatureMax,
                String relativeHumidityTime,
                Double relativeHumidityMax,
                String windSpeedTime,
                Double windSpeedMax
        )
{ }
