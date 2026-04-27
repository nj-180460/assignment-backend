package com.gler.assignment.service.impl;

import com.gler.assignment.WeatherRepository;
import com.gler.assignment.client.WeatherClient;
import com.gler.assignment.constant.HourlyMeasure;
import com.gler.assignment.dto.request.ForecastRequest;
import com.gler.assignment.dto.response.WeatherResponseDTO;
import com.gler.assignment.model.HourlyWeather;
import com.gler.assignment.service.WeatherService;
import com.gler.assignment.util.WeatherMapper;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class WeatherServiceImpl implements WeatherService {

    private final WeatherClient weatherClient;
    private final WeatherRepository weatherRepository;

    public WeatherServiceImpl(WeatherClient weatherClient, WeatherRepository weatherRepository) {
        this.weatherClient = weatherClient;
        this.weatherRepository = weatherRepository;
    }

    /**
     * This is in Berlin - for testing purposes
     * @return
     */
    @Transactional
    @Override
    public WeatherResponseDTO getWeather(ForecastRequest forecastRequest) {
        Map<String, String> filter = filterHourlyParam(forecastRequest);
        WeatherResponseDTO weatherResponseDTO = weatherClient.getForecast(
                52.52,
                13.41,
                filter.get("current"),
                filter.get("hourly")
        );

        HourlyWeather dbResponse = weatherRepository.save(WeatherMapper.mapToHourlyWeather(weatherResponseDTO));
        if (dbResponse.getId() > 0) {
            System.out.println("Successfully saved!");
        }

        return weatherResponseDTO;
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

}
