package com.gler.assignment;

import com.gler.assignment.model.HourlyWeather;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WeatherRepository extends JpaRepository<HourlyWeather, Long> {
}
