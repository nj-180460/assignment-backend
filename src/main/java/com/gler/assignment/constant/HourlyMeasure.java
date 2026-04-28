package com.gler.assignment.constant;

public enum HourlyMeasure {

    TEMPERATURE_2M ("temperature_2m"),
    RELATIVE_HUMIDITY_2M ("relative_humidity_2m"),
    WIND_SPEED ("wind_speed_10m");

    private String hourlyMvalue;

    HourlyMeasure (String hourlyMvalue) {
        this.hourlyMvalue = hourlyMvalue;
    }

    public String getHourlyMValue(){
        return hourlyMvalue;
    }
}
