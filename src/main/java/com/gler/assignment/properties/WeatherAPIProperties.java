package com.gler.assignment.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("api.open-meteo")
public record WeatherAPIProperties (
        String endpoint
) {}
