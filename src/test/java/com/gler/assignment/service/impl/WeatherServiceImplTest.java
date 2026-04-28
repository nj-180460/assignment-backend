package com.gler.assignment.service.impl;

import com.gler.assignment.client.WeatherClient;
import com.gler.assignment.dto.request.ForecastRequest;
import com.gler.assignment.dto.response.WeatherClientServiceResponse;
import com.gler.assignment.dto.response.WeatherResponseDTO;
import com.gler.assignment.model.HourlyWeather;
import com.gler.assignment.repository.WeatherRepository;
import com.gler.assignment.util.WeatherObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WeatherServiceImplTest {

    @InjectMocks
    private WeatherServiceImpl weatherService;

    @Mock
    private WeatherClient weatherClient;

    @Mock
    private WeatherRepository weatherRepository;

    @Mock
    private WeatherObjectMapper weatherObjectMapper;


    @Test
    @DisplayName("Full Request: Should correctly filter all parameters and save data")
    void getWeather_Success() {
        ForecastRequest request = new ForecastRequest(true, true, true);

        WeatherClientServiceResponse clientResponse = mock(WeatherClientServiceResponse.class);
        when(weatherClient.getForecast(anyDouble(), anyDouble(), anyString(), anyString()))
                .thenReturn(clientResponse);

        HourlyWeather entity = new HourlyWeather();
        entity.setId(1L);
        entity.setTemperature(30.0);
        when(weatherRepository.save(any())).thenReturn(entity);

        WeatherResponseDTO expectedDto = mock(WeatherResponseDTO.class);
        when(weatherObjectMapper.toDTO(any(HourlyWeather.class))).thenReturn(expectedDto);

        WeatherResponseDTO result = weatherService.getWeather(request);

        assertThat(result).isNotNull();

        verify(weatherClient, times(1)).getForecast(eq(52.52), eq(13.41), anyString(), anyString());
        verify(weatherRepository, times(1)).save(any());
        verify(weatherObjectMapper, times(1)).toDTO(any());
    }


    @Nested
    @DisplayName("Edge Cases")
    class EdgeCaseTests {

        @BeforeEach
        void setUp() {
            when(weatherRepository.save(any())).thenReturn(new HourlyWeather());

        }

        @Test
        @DisplayName("Empty Request: Should handle request with no metrics selected")
        void testEmptyRequest() {
            ForecastRequest request = new ForecastRequest(false, false, false);

            when(weatherClient.getForecast(anyDouble(), anyDouble(), eq(""), eq("")))
                    .thenReturn(mock(WeatherClientServiceResponse.class));
            when(weatherRepository.save(any())).thenReturn(new HourlyWeather());

            weatherService.getWeather(request);

            verify(weatherClient).getForecast(anyDouble(), anyDouble(), eq(""), eq(""));
        }

        @Test
        @DisplayName("Partial Request: Should only include temperature")
        void testTemperatureOnly() {
            ForecastRequest request = new ForecastRequest(true, false, false);

            when(weatherClient.getForecast(anyDouble(), anyDouble(), anyString(), anyString()))
                    .thenReturn(mock(WeatherClientServiceResponse.class));

            weatherService.getWeather(request);

            verify(weatherClient).getForecast(
                    eq(52.52),
                    eq(13.41),
                    argThat(s -> s.contains("temperature_2m")),
                    argThat(s -> !s.contains("wind_speed"))
            );
        }

        @Test
        @DisplayName("Wind Only: Should include wind in current and hourly")
        void testWindOnly() {
            ForecastRequest request = new ForecastRequest(false, false, true);

            when(weatherClient.getForecast(anyDouble(), anyDouble(), anyString(), anyString()))
                    .thenReturn(mock(WeatherClientServiceResponse.class));

            weatherService.getWeather(request);

            verify(weatherClient).getForecast(
                    anyDouble(), anyDouble(),
                    argThat(s -> s.contains("wind_speed_10m")),
                    argThat(s -> s.contains("wind_speed_10m") && !s.contains("temperature"))
            );
        }

        @Test
        @DisplayName("Humidity Only: Should only be in hourly, not in current")
        void testHumidityOnly() {
            ForecastRequest request = new ForecastRequest(false, true, false);

            when(weatherClient.getForecast(anyDouble(), anyDouble(), anyString(), anyString()))
                    .thenReturn(mock(WeatherClientServiceResponse.class));

            weatherService.getWeather(request);

            verify(weatherClient).getForecast(
                    anyDouble(), anyDouble(),
                    eq(""),
                    argThat(s -> s.contains("relative_humidity_2m"))
            );
        }

        @Test
        @DisplayName("Temperature and Humidity: Should exclude wind")
        void testTemperatureAndHumidity() {
            ForecastRequest request = new ForecastRequest(true, true, false);

            when(weatherClient.getForecast(anyDouble(), anyDouble(), anyString(), anyString()))
                    .thenReturn(mock(WeatherClientServiceResponse.class));

            weatherService.getWeather(request);

            verify(weatherClient).getForecast(
                    anyDouble(), anyDouble(),
                    argThat(s -> s.contains("temperature_2m") && !s.contains("wind")),
                    argThat(s -> s.contains("temperature_2m") && s.contains("relative_humidity_2m"))
            );
        }
    }

    @Nested
    @DisplayName("Negative Scenarios")
    class NegativeTests {

        @Test
        @DisplayName("Database Failure: Should throw exception if repository save fails")
        void testRepositoryFailure() {
            ForecastRequest request = new ForecastRequest(true, true, true);
            when(weatherClient.getForecast(anyDouble(), anyDouble(), anyString(), anyString()))
                    .thenReturn(mock(WeatherClientServiceResponse.class));

            when(weatherRepository.save(any())).thenThrow(new RuntimeException("DB Connection Timeout"));

            assertThatThrownBy(() -> weatherService.getWeather(request))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("DB Connection Timeout");
        }

        @Test
        @DisplayName("Client Failure: Should propagate API client exceptions")
        void testClientFailure() {
            ForecastRequest request = new ForecastRequest(true, true, true);
            when(weatherClient.getForecast(anyDouble(), anyDouble(), anyString(), anyString()))
                    .thenThrow(new RuntimeException("API Down"));

            assertThatThrownBy(() -> weatherService.getWeather(request))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("API Down");

            verifyNoInteractions(weatherRepository);
        }
    }
}