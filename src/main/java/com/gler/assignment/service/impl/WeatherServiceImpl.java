package com.gler.assignment.service.impl;

import com.gler.assignment.repository.WeatherRepository;
import com.gler.assignment.client.WeatherClient;
import com.gler.assignment.constant.HourlyMeasure;
import com.gler.assignment.dto.request.ForecastRequest;
import com.gler.assignment.dto.response.WeatherClientServiceResponse;
import com.gler.assignment.dto.response.WeatherResponseDTO;
import com.gler.assignment.model.HourlyWeather;
import com.gler.assignment.service.WeatherService;
import com.gler.assignment.util.WeatherMapper;
import com.gler.assignment.util.WeatherObjectMapper;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class WeatherServiceImpl implements WeatherService {

    private static final Logger LOGGER = LoggerFactory.getLogger(WeatherServiceImpl.class);

    private final WeatherClient weatherClient;
    private final WeatherRepository weatherRepository;
    private final WeatherObjectMapper weatherObjectMapper;

    public WeatherServiceImpl(WeatherClient weatherClient, WeatherRepository weatherRepository, WeatherObjectMapper weatherObjectMapper) {
        this.weatherClient = weatherClient;
        this.weatherRepository = weatherRepository;
        this.weatherObjectMapper = weatherObjectMapper;
    }

    /**
     * This is in Berlin - for testing purposes
     * @return WeatherResponseDTO
     */
    @Transactional
    @Override
    public WeatherResponseDTO getWeather(ForecastRequest forecastRequest) {
        Map<String, String> filter = filterHourlyParam(forecastRequest);
        WeatherClientServiceResponse weatherClientServiceResponse = weatherClient.getForecast(
                52.52,
                13.41,
                filter.get("current"),
                filter.get("hourly")
        );

        HourlyWeather dbResponse = weatherRepository.save(WeatherMapper.mapToHourlyWeather(weatherClientServiceResponse));
        LOGGER.info("event=getWeather, id={}, message=Successfully save the max hourly weather", dbResponse.getId());

        return mapToWeatherResponseDTO(dbResponse);
    }


    private Map<String, String> filterHourlyParam(ForecastRequest forecastRequest) {

        StringBuilder current = new StringBuilder();
        StringBuilder hourly = new StringBuilder();

        if (forecastRequest.addTemperature()) {
            current.append(HourlyMeasure.TEMPERATURE_2M.getHourlyMValue());
            hourly.append(HourlyMeasure.TEMPERATURE_2M.getHourlyMValue());
        }
        if (forecastRequest.addWindSpeed()) {
            current
                    .append(",")
                    .append(HourlyMeasure.WIND_SPEED.getHourlyMValue());
            hourly
                    .append(",")
                    .append(HourlyMeasure.WIND_SPEED.getHourlyMValue());
        }
        if (forecastRequest.addHumidity()) {
            hourly
                    .append(",")
                    .append(HourlyMeasure.RELATIVE_HUMIDITY_2M.getHourlyMValue());
        }

        Map<String, String> filter = new HashMap<>();
        filter.put("current", current.toString());
        filter.put("hourly", hourly.toString());
        return filter;
    }


    private WeatherResponseDTO mapToWeatherResponseDTO(HourlyWeather hourlyWeather){
        return weatherObjectMapper.toDTO(hourlyWeather);
    }
}
