package com.gler.assignment.util;

import com.gler.assignment.dto.response.WeatherResponseDTO;
import com.gler.assignment.model.WeatherDetails;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface WeatherObjectMapper {

    @Mapping(target = "temperatureMax", source = "weatherDetails.temperature")
    @Mapping(target = "temperatureTime", source = "weatherDetails.temperatureTime")
    @Mapping(target = "relativeHumidityMax", source = "weatherDetails.relativeHumidity")
    @Mapping(target = "relativeHumidityTime", source = "weatherDetails.relativeHumidityTime")
    @Mapping(target = "windSpeedMax", source = "weatherDetails.windSpeed")
    @Mapping(target = "windSpeedTime", source = "weatherDetails.windSpeedTime")
    WeatherResponseDTO toDTO(WeatherDetails weatherDetails);

}
