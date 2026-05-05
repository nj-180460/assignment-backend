package com.gler.assignment.dto.request;

import jakarta.validation.constraints.NotNull;

public record ForecastRequest
        (
                @NotNull(message = "Temperature flag must be provided")
                Boolean addTemperature,

                @NotNull(message = "Humidity flag must be provided")
                Boolean addHumidity,

                @NotNull(message = "Wind speed flag must be provided")
                Boolean addWindSpeed
        )
{
}
