package com.gler.assignment.dto.response;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record WeatherClientServiceResponse(
        Double latitude,
        Double longitude,
        @JsonProperty("generationtime_ms")
        Double generationTimeMs,
        String timezone,
        DailyUnitsDTO daily_units,
        DailyDataDTO daily
) {
    public record DailyUnitsDTO(
            String time,
            @JsonProperty("temperature_2m_max")
            String temperature2mMax,
            @JsonProperty("relative_humidity_2m_max")
            String relativeHumidity2mMax,
            @JsonProperty("wind_speed_10m_max")
            String windSpeed10mMax
    ) {}

    public record DailyDataDTO(
            List<String> time,
            @JsonProperty("temperature_2m_max")
            List<Double> temperature2mMax,
            @JsonProperty("relative_humidity_2m_max")
            List<Double> relativeHumidity2mMax,
            @JsonProperty("wind_speed_10m_max")
            List<Double> windSpeed10mMax
    ) {}
}
