package com.gler.assignment.constant;

public enum WeatherMeasure {

    TEMPERATURE_2M_MAX ("temperature_2m_max"),
    RELATIVE_HUMIDITY_2M_MAX ("relative_humidity_2m_max"),
    WIND_SPEED_MAX ("wind_speed_10m_max");

    private String weatherMvalue;

    WeatherMeasure(String weatherMvalue) {
        this.weatherMvalue = weatherMvalue;
    }

    public String getMeasureMValue(){
        return weatherMvalue;
    }
}
