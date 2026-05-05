package com.gler.assignment.service.impl;

import com.gler.assignment.constant.WeatherMeasure;
import com.gler.assignment.model.WeatherDetails;
import com.gler.assignment.repository.WeatherRepository;
import com.gler.assignment.client.WeatherClient;
import com.gler.assignment.dto.request.ForecastRequest;
import com.gler.assignment.dto.response.WeatherClientServiceResponse;
import com.gler.assignment.dto.response.WeatherResponseDTO;
import com.gler.assignment.service.WeatherService;
import com.gler.assignment.util.WeatherMapper;
import com.gler.assignment.util.WeatherObjectMapper;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
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
        Map<String, Object> apiParams = buildParams();
        WeatherClientServiceResponse weatherClientServiceResponse = weatherClient.getForecast(apiParams);
        LOGGER.info("event=getWeather, message=Retrieved weather info from client");

        WeatherDetails dbResponse = weatherRepository.save(WeatherMapper.mapToHourlyWeather(weatherClientServiceResponse, forecastRequest));
        LOGGER.info("event=getWeather, id={}, message=Successfully save the max hourly weather", dbResponse.getId());

        return mapToWeatherResponseDTO(dbResponse);
    }

    private Map<String, Object> buildParams() {
        List<String> dailyParams = new ArrayList<>();
        Map<String, Object> params = new java.util.HashMap<>(Map.of("daily", String.join(",", dailyParams)));

        params.put("latitude", 52.52);
        params.put("longitude", 13.41);
        params.put("timezone", "auto");

        dailyParams.add(WeatherMeasure.TEMPERATURE_2M_MAX.getMeasureMValue());
        dailyParams.add(WeatherMeasure.RELATIVE_HUMIDITY_2M_MAX.getMeasureMValue());
        dailyParams.add(WeatherMeasure.WIND_SPEED_MAX.getMeasureMValue());
        params.put("daily", String.join(",", dailyParams));

        return params;
    }


    private WeatherResponseDTO mapToWeatherResponseDTO(WeatherDetails weatherDetails){
        return weatherObjectMapper.toDTO(weatherDetails);
    }
}
