package com.gler.assignment.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "weather_details")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class WeatherDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime dateCreated;
    private LocalDate recordDate;

    private String temperatureTime;
    private Double temperature;

    private String relativeHumidityTime;
    private Double relativeHumidity;

    private String windSpeedTime;
    private Double windSpeed;

}
