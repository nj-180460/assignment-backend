package com.gler.assignment.client;

import com.gler.assignment.dto.response.WeatherClientServiceResponse;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

@HttpExchange("/v1")
public interface WeatherClient {

    @GetExchange("/forecast")
    WeatherClientServiceResponse getForecast(
            @RequestParam double latitude,
            @RequestParam double longitude,
            @RequestParam String current,
            @RequestParam String hourly
    );

}
