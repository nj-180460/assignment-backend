package com.gler.assignment.repository;


import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import com.gler.assignment.model.HourlyWeather;

@DataJpaTest
class WeatherRepositoryTest {

    @Autowired
    private WeatherRepository weatherRepository;

    @Test
    @DisplayName("Should save and retrieve hourly weather peak data")
    void shouldSaveAndRetrieveWeather() {
        HourlyWeather weather = new HourlyWeather(
                null,
                "2024-03-20T14:00", 32.5,
                "2024-03-20T18:00", 85.0,
                "2024-03-20T10:00", 15.2
        );

        HourlyWeather savedWeather = weatherRepository.save(weather);

        assertThat(savedWeather).isNotNull();
        assertThat(savedWeather.getId()).isGreaterThan(0);
        assertThat(savedWeather.getTemperature()).isEqualTo(32.5);
        assertThat(savedWeather.getTemperatureTime()).isEqualTo("2024-03-20T14:00");

        HourlyWeather foundWeather = weatherRepository.findById(savedWeather.getId()).orElse(null);
        assertThat(foundWeather).isNotNull();
        assertThat(foundWeather.getRelativeHumidity()).isEqualTo(85.0);
    }
}