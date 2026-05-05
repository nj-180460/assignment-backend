package com.gler.assignment.util;

import com.gler.assignment.dto.response.WeatherResponseDTO;
import com.gler.assignment.model.WeatherDetails;
import java.time.LocalDate;
import java.time.LocalDateTime;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-05-06T01:41:12+0800",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.2 (Oracle Corporation)"
)
@Component
public class WeatherObjectMapperImpl implements WeatherObjectMapper {

    @Override
    public WeatherResponseDTO toDTO(WeatherDetails weatherDetails) {
        if ( weatherDetails == null ) {
            return null;
        }

        Double temperatureMax = null;
        String temperatureTime = null;
        Double relativeHumidityMax = null;
        String relativeHumidityTime = null;
        Double windSpeedMax = null;
        String windSpeedTime = null;
        LocalDateTime dateCreated = null;
        LocalDate recordDate = null;

        temperatureMax = weatherDetails.getTemperature();
        temperatureTime = weatherDetails.getTemperatureTime();
        relativeHumidityMax = weatherDetails.getRelativeHumidity();
        relativeHumidityTime = weatherDetails.getRelativeHumidityTime();
        windSpeedMax = weatherDetails.getWindSpeed();
        windSpeedTime = weatherDetails.getWindSpeedTime();
        dateCreated = weatherDetails.getDateCreated();
        recordDate = weatherDetails.getRecordDate();

        WeatherResponseDTO weatherResponseDTO = new WeatherResponseDTO( dateCreated, recordDate, temperatureTime, temperatureMax, relativeHumidityTime, relativeHumidityMax, windSpeedTime, windSpeedMax );

        return weatherResponseDTO;
    }
}
