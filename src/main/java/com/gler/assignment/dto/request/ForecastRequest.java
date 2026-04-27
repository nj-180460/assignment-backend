package com.gler.assignment.dto.request;

import jakarta.validation.constraints.NotNull;
import static java.util.Objects.requireNonNull;

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
        public ForecastRequest {
                requireNonNull(addTemperature, "addTemperature cannot be null");
                requireNonNull(addHumidity, "addHumidity cannot be null");
                requireNonNull(addWindSpeed, "addWindSpeed cannot be null");
        }
}
