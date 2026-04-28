package com.gler.assignment.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gler.assignment.client.WeatherClient;
import com.gler.assignment.dto.request.ForecastRequest;
import com.gler.assignment.dto.response.WeatherResponseDTO;
import com.gler.assignment.exception.WeatherServiceException;
import com.gler.assignment.repository.WeatherRepository;
import com.gler.assignment.service.WeatherService;
import com.gler.assignment.util.RequestIdGenerator;
import com.gler.assignment.util.WeatherObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ForcastController.class)
class ForcastControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private WeatherService weatherService;

    @MockitoBean
    private RequestIdGenerator requestIdGenerator;

    @MockitoBean
    private WeatherClient weatherClient;

    @MockitoBean
    private WeatherRepository weatherRepository;

    @MockitoBean
    private WeatherObjectMapper weatherObjectMapper;


    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("POST /forecast - Success Scenario")
    void getForecast_Success() throws Exception {
        ForecastRequest request = new ForecastRequest(true, true, true);
        WeatherResponseDTO mockResponse = new WeatherResponseDTO(
                "2024-03-20T14:00", 32.5,
                "2024-03-20T18:00", 85.0,
                "2024-03-20T10:00", 15.2
        );

        when(weatherService.getWeather(any(ForecastRequest.class))).thenReturn(mockResponse);

        mockMvc.perform(post("/api/v1/forecast")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("Successfully created weather record"))

                .andExpect(jsonPath("$.data").exists())

                .andExpect(jsonPath("$.data.temperatureTime").value("2024-03-20T14:00"))
                .andExpect(jsonPath("$.data.temperature").value(32.5))

                .andExpect(jsonPath("$.data.relativeHumidityTime").value("2024-03-20T18:00"))
                .andExpect(jsonPath("$.data.relativeHumidity").value(85.0))

                .andExpect(jsonPath("$.data.windSpeedTime").value("2024-03-20T10:00"))
                .andExpect(jsonPath("$.data.windSpeed").value(15.2));
    }

    @Test
    @DisplayName("POST /forecast - Invalid Request Body")
    void getForecast_BadRequest() throws Exception {
        String invalidJson = "{ \"addTemperature\": \"not_a_boolean\" }";

        mockMvc.perform(post("/api/v1/forecast")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest());
    }


    @Nested
    @DisplayName("Global Exception Handler Tests")
    class ExceptionHandlerTests {

        @Test
        @DisplayName("400 Bad Request - Validation Failure")
        void testValidationHandling() throws Exception {
            String invalidJson = "{\"addTemperature\": null, \"addHumidity\": true, \"addWindSpeed\": true}";

            mockMvc.perform(post("/api/v1/forecast")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(invalidJson))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value("400"))
                    .andExpect(jsonPath("$.error").value("Bad Request: \"Invalid input format: Ensure data types (boolean, numbers, etc.) are correct.\""))
                    .andExpect(jsonPath("$.message").value("JSON parse error: Cannot construct instance of `com.gler.assignment.dto.request.ForecastRequest`, problem: addTemperature cannot be null"));
        }

        @Test
        @DisplayName("400 Bad Request - JSON Type Mismatch")
        void testTypeMismatchHandling() throws Exception {
            // Sending a string "YES" instead of a boolean true/false
            String badTypeJson = "{\"addTemperature\": \"YES\", \"addHumidity\": true, \"addWindSpeed\": true}";

            mockMvc.perform(post("/api/v1/forecast")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(badTypeJson))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error").value("Bad Request: \"Invalid input format: Ensure data types (boolean, numbers, etc.) are correct.\""));
        }

        @Test
        @DisplayName("502 Bad Gateway - Weather Service Failure")
        void testWeatherServiceException() throws Exception {

            when(weatherService.getWeather(any()))
                    .thenThrow(new WeatherServiceException("External API Timeout", 503));

            mockMvc.perform(post("/api/v1/forecast")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"addTemperature\": true, \"addHumidity\": true, \"addWindSpeed\": true}"))
                    .andExpect(status().isBadGateway())
                    .andExpect(jsonPath("$.error").value("Weather client api call error"))
                    .andExpect(jsonPath("$.message").value("External API Timeout"));
        }

        @Test
        @DisplayName("500 Internal Server Error - Uncaught Exception")
        void testUncaughtException() throws Exception {
            when(weatherService.getWeather(any()))
                    .thenThrow(new RuntimeException("Something went wrong internally"));

            mockMvc.perform(post("/api/v1/forecast")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"addTemperature\": true, \"addHumidity\": true, \"addWindSpeed\": true}"))
                    .andExpect(status().isInternalServerError())
                    .andExpect(jsonPath("$.status").value("500"))
                    .andExpect(jsonPath("$.error").value("Internal Server Error"));
        }
    }
}