package com.gler.assignment.util;

import com.gler.assignment.dto.response.WeatherResponseDTO;
import com.gler.assignment.model.HourlyWeather;

import java.util.Collections;
import java.util.Comparator;
import java.util.Optional;
import java.util.stream.IntStream;

public final class WeatherMapper {

    private WeatherMapper () {
        throw new UnsupportedOperationException("Cannot be instantiated. Use static methods only.");
    }

    public static HourlyWeather mapToHourlyWeather(WeatherResponseDTO response) {
        if (response == null || response.hourly() == null || response.hourly().time() == null) {
            return null;
        }

        WeatherResponseDTO.HourlyDataDTO hourly = response.hourly();

        int maxTempIdx = 0;
        if (hourly.temperature2m() != null) {
            maxTempIdx = IntStream.range(0, hourly.temperature2m().size())
                    .boxed()
                    .max(Comparator.comparing(hourly.temperature2m()::get))
                    .orElse(0);
        }

        int maxHumidityIdx = 0;
        if (hourly.relativeHumidity2m() != null) {
            maxHumidityIdx = IntStream.range(0, hourly.relativeHumidity2m().size())
                    .boxed()
                    .max(Comparator.comparing(hourly.relativeHumidity2m()::get))
                    .orElse(0);
        }

        int maxWindIdx = 0;
        if ( hourly.windSpeed10m() != null) {
            maxWindIdx = IntStream.range(0, hourly.windSpeed10m().size())
                    .boxed()
                    .max(Comparator.comparing(hourly.windSpeed10m()::get))
                    .orElse(0);
        }

        HourlyWeather hourlyWeather = new HourlyWeather();
        hourlyWeather.setTemperatureTime(hourly.time().get(maxTempIdx));
        hourlyWeather.setTemperature(hourly.temperature2m()
                != null ? hourly.temperature2m().get(maxTempIdx) : maxTempIdx);

        hourlyWeather.setRelativeHumidity(hourly.relativeHumidity2m()
                != null ? hourly.relativeHumidity2m().get(maxHumidityIdx).doubleValue() : maxHumidityIdx);
        hourlyWeather.setRelativeHumidityTime(hourly.time().get(maxHumidityIdx));

        hourlyWeather.setWindSpeed(hourly.windSpeed10m()
                != null ? hourly.windSpeed10m().get(maxWindIdx) : maxWindIdx);
        hourlyWeather.setWindSpeedTime(hourly.time().get(maxWindIdx));

        return hourlyWeather;
    }
}
