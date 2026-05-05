package com.gler.assignment.repository;

import com.gler.assignment.model.WeatherDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WeatherRepository extends JpaRepository<WeatherDetails, Long> {
}
