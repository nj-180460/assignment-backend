package com.gler.assignment.controller;

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
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.ResourceAccessException;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
        LocalDateTime localDateTime = LocalDateTime.now();
        LocalDate localDate = LocalDate.now();
        ForecastRequest request = new ForecastRequest(true, true, true);
        WeatherResponseDTO mockResponse = new WeatherResponseDTO(
                localDateTime,
                localDate,
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
                .andExpect(jsonPath("$.message").value("Successfully saved weather record"))

                .andExpect(jsonPath("$.data").exists())

                .andExpect(jsonPath("$.data.temperatureTime").value("2024-03-20T14:00"))
                .andExpect(jsonPath("$.data.temperatureMax").value(32.5))

                .andExpect(jsonPath("$.data.relativeHumidityTime").value("2024-03-20T18:00"))
                .andExpect(jsonPath("$.data.relativeHumidityMax").value(85.0))

                .andExpect(jsonPath("$.data.windSpeedTime").value("2024-03-20T10:00"))
                .andExpect(jsonPath("$.data.windSpeedMax").value(15.2));
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
                    .andExpect(jsonPath("$.error").value("Bad Request: Missing Parameter/s"))
                    .andExpect(jsonPath("$.message").value("Temperature flag must be provided"))
                    .andExpect(jsonPath("$.timestamp").exists())
                    .andExpect(jsonPath("$.status").value(400))
                    .andExpect(jsonPath("$.path").value("/api/v1/forecast"));
        }

        @Test
        @DisplayName("400 Bad Request - JSON Type Mismatch - addTemperature")
        void testTypeMismatchHandling_addTemperature() throws Exception {
            // Sending a string "YES" instead of a boolean true/false
            String invalidValue = "YES";
            String badTypeJson = "{\"addTemperature\": \""+invalidValue+"\", \"addHumidity\": true, \"addWindSpeed\": true}";
            when(weatherService.getWeather(any()))
                    .thenThrow(new HttpMessageNotReadableException("Invalid param types"));

            mockMvc.perform(post("/api/v1/forecast")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(badTypeJson))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error").value("Bad Request: Invalid input format type"))
                    .andExpect(jsonPath("$.message").value("Invalid value for parameter: 'addTemperature'. Expected a boolean (true/false)."))
                    .andExpect(jsonPath("$.timestamp").exists())
                    .andExpect(jsonPath("$.status").value(400))
                    .andExpect(jsonPath("$.path").value("/api/v1/forecast"));
        }


        @Test
        @DisplayName("400 Bad Request - JSON Type Mismatch - addHumidity")
        void testTypeMismatchHandling_addHumidity() throws Exception {
            // Sending a string "YES" instead of a boolean true/false
            String invalidValue = "1";
            String badTypeJson = "{\"addTemperature\": true, \"addHumidity\":  \""+invalidValue+"\", \"addWindSpeed\": true}";
            when(weatherService.getWeather(any()))
                    .thenThrow(new HttpMessageNotReadableException("Invalid param types"));

            mockMvc.perform(post("/api/v1/forecast")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(badTypeJson))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error").value("Bad Request: Invalid input format type"))
                    .andExpect(jsonPath("$.message").value("Invalid value for parameter: 'addHumidity'. Expected a boolean (true/false)."))
                    .andExpect(jsonPath("$.timestamp").exists())
                    .andExpect(jsonPath("$.status").value(400))
                    .andExpect(jsonPath("$.path").value("/api/v1/forecast"));
        }

        @Test
        @DisplayName("400 Bad Request - JSON Type Mismatch - addWindSpeed")
        void testTypeMismatchHandling_addWindSpeed() throws Exception {
            // Sending a string "YES" instead of a boolean true/false
            String invalidValue = "YES";
            String badTypeJson = "{\"addTemperature\": false, \"addHumidity\": true, \"addWindSpeed\": \""+invalidValue+"\"}";
            when(weatherService.getWeather(any()))
                    .thenThrow(new HttpMessageNotReadableException("Invalid param types"));

            mockMvc.perform(post("/api/v1/forecast")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(badTypeJson))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error").value("Bad Request: Invalid input format type"))
                    .andExpect(jsonPath("$.message").value("Invalid value for parameter: 'addWindSpeed'. Expected a boolean (true/false)."))
                    .andExpect(jsonPath("$.timestamp").exists())
                    .andExpect(jsonPath("$.status").value(400))
                    .andExpect(jsonPath("$.path").value("/api/v1/forecast"));
        }



        @Test
        @DisplayName("500 Bad Gateway - Weather Service Failure - Timeout Error")
        void testWeatherServiceException() throws Exception {

            when(weatherService.getWeather(any()))
                    .thenThrow(new ResourceAccessException("Connection to the upstream is unreachable"));

            mockMvc.perform(post("/api/v1/forecast")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"addTemperature\": true, \"addHumidity\": true, \"addWindSpeed\": true}"))
                    .andExpect(status().isBadGateway())
                    .andExpect(jsonPath("$.error").value("Upstream API Unreachable"))
                    .andExpect(jsonPath("$.message").value("Connection to the upstream is unreachable"))
                    .andExpect(jsonPath("$.timestamp").exists())
                    .andExpect(jsonPath("$.status").value(502))
                    .andExpect(jsonPath("$.path").value("/api/v1/forecast"));
        }

        @Test
        @DisplayName("500 Internal Server Error - Uncaught Exception")
        void testUncaughtException() throws Exception {
            when(weatherService.getWeather(any()))
                    .thenThrow(new WeatherServiceException("External API Error", 500));

            mockMvc.perform(post("/api/v1/forecast")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"addTemperature\": true, \"addHumidity\": true, \"addWindSpeed\": true}"))
                    .andExpect(status().isInternalServerError())
                    .andExpect(jsonPath("$.status").value("500"))
                    .andExpect(jsonPath("$.error").value("Weather client api call error"))
                    .andExpect(jsonPath("$.message").value("External API Error"))
                    .andExpect(jsonPath("$.path").value("/api/v1/forecast"))
                    .andExpect(jsonPath("$.timestamp").exists())
                    .andExpect(jsonPath("$.status").value(500));
        }
    }
}