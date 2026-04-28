package com.gler.assignment.util;

import com.gler.assignment.dto.response.WeatherResponseDTO;
import com.gler.assignment.model.HourlyWeather;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-04-29T01:56:12+0800",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.2 (Oracle Corporation)"
)
@Component
public class WeatherObjectMapperImpl implements WeatherObjectMapper {

    @Override
    public WeatherResponseDTO toDTO(HourlyWeather hourlyWeather) {
        if ( hourlyWeather == null ) {
            return null;
        }

        double temperature = 0.0d;
        String temperatureTime = null;
        double relativeHumidity = 0.0d;
        String relativeHumidityTime = null;
        double windSpeed = 0.0d;
        String windSpeedTime = null;

        temperature = hourlyWeather.getTemperature();
        temperatureTime = hourlyWeather.getTemperatureTime();
        relativeHumidity = hourlyWeather.getRelativeHumidity();
        relativeHumidityTime = hourlyWeather.getRelativeHumidityTime();
        windSpeed = hourlyWeather.getWindSpeed();
        windSpeedTime = hourlyWeather.getWindSpeedTime();

        WeatherResponseDTO weatherResponseDTO = new WeatherResponseDTO( temperatureTime, temperature, relativeHumidityTime, relativeHumidity, windSpeedTime, windSpeed );

        return weatherResponseDTO;
    }
}
