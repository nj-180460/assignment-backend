package com.gler.assignment.service;

import com.gler.assignment.dto.request.ForecastRequest;
import com.gler.assignment.dto.response.WeatherResponseDTO;

public interface WeatherService {

    WeatherResponseDTO getWeather(ForecastRequest forecastRequest);

}
