package com.gler.assignment.util;

import com.gler.assignment.dto.response.WeatherResponseDTO;
import com.gler.assignment.model.HourlyWeather;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface WeatherObjectMapper {

    @Mapping(target = "temperature", source = "hourlyWeather.temperature")
    @Mapping(target = "temperatureTime", source = "hourlyWeather.temperatureTime")
    @Mapping(target = "relativeHumidity", source = "hourlyWeather.relativeHumidity")
    @Mapping(target = "relativeHumidityTime", source = "hourlyWeather.relativeHumidityTime")
    @Mapping(target = "windSpeed", source = "hourlyWeather.windSpeed")
    @Mapping(target = "windSpeedTime", source = "hourlyWeather.windSpeedTime")
    WeatherResponseDTO toDTO(HourlyWeather hourlyWeather);

}
