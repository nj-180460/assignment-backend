package com.gler.assignment.client;

import com.gler.assignment.dto.response.WeatherClientServiceResponse;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

import java.util.Map;

@HttpExchange("/v1")
public interface WeatherClient {

    @GetExchange("/forecast")
    WeatherClientServiceResponse getForecast(@RequestParam Map<String, Object> params);

}
