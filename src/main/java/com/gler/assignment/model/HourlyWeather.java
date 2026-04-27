package com.gler.assignment.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "hourly_weather")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class HourlyWeather {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String temperatureTime;
    private double temperature;

    private String relativeHumidityTime;
    private double relativeHumidity;

    private String windSpeedTime;
    private double windSpeed;

}
