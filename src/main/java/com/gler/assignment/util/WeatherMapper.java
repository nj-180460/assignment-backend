package com.gler.assignment.util;

import com.gler.assignment.dto.request.ForecastRequest;
import com.gler.assignment.dto.response.WeatherClientServiceResponse;
import com.gler.assignment.model.WeatherDetails;

import java.time.LocalDate;
import java.time.LocalDateTime;

public final class WeatherMapper {

    private WeatherMapper () {
        throw new UnsupportedOperationException("Cannot be instantiated. Use static methods only.");
    }

    public static WeatherDetails mapToHourlyWeather(WeatherClientServiceResponse response, ForecastRequest forecastRequest) {
        if (response == null || response.daily() == null) return null;

        WeatherDetails entity = new WeatherDetails();
        entity.setDateCreated(LocalDateTime.now());

        if (!response.daily().time().isEmpty()) {
            entity.setRecordDate(LocalDate.parse(response.daily().time().get(0)));
        }

        if (forecastRequest.addTemperature() && response.daily().temperature2mMax() != null) {
            entity.setTemperature(response.daily().temperature2mMax().get(0));
            entity.setTemperatureTime(response.daily().time().get(0));
        }

        if (forecastRequest.addHumidity() && response.daily().relativeHumidity2mMax() != null) {
            entity.setRelativeHumidity(response.daily().relativeHumidity2mMax().get(0));
            entity.setRelativeHumidityTime(response.daily().time().get(0));
        }

        if (forecastRequest.addWindSpeed() && response.daily().windSpeed10mMax() != null) {
            entity.setWindSpeed(response.daily().windSpeed10mMax().get(0));
            entity.setWindSpeedTime(response.daily().time().get(0));
        }

        return entity;
    }
}
