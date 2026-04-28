package com.gler.assignment.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record WeatherResponseDTO(
        double latitude,
        double longitude,
        @JsonProperty("generationtime_ms")
        double generationTimeMs,
        @JsonProperty("utc_offset_seconds")
        int utcOffsetSeconds,
        String timezone,
        @JsonProperty("timezone_abbreviation")
        String timezoneAbbreviation,
        double elevation,
        @JsonProperty("current_units")
        CurrentUnitsDTO currentUnits,
        CurrentDataDTO current,
        @JsonProperty("hourly_units")
        HourlyUnitsDTO hourlyUnits,
        HourlyDataDTO hourly
) {

    public record CurrentUnitsDTO(
            String time,
            String interval,
            @JsonProperty("temperature_2m")
            String temperature2m,
            @JsonProperty("wind_speed_10m")
            String windSpeed10m
    ) {}

    public record CurrentDataDTO(
            String time,
            int interval,
            @JsonProperty("temperature_2m")
            double temperature2m,
            @JsonProperty("wind_speed_10m")
            double windSpeed10m
    ) {}

    public record HourlyUnitsDTO(
            String time,
            @JsonProperty("temperature_2m")
            String temperature2m,
            @JsonProperty("relative_humidity_2m")
            String relativeHumidity2m,
            @JsonProperty("wind_speed_10m")
            String windSpeed10m
    ) {}

    public record HourlyDataDTO(
            List<String> time,
            @JsonProperty("temperature_2m")
            List<Double> temperature2m,
            @JsonProperty("relative_humidity_2m")
            List<Integer> relativeHumidity2m,
            @JsonProperty("wind_speed_10m")
            List<Double> windSpeed10m
    ) {}
}
