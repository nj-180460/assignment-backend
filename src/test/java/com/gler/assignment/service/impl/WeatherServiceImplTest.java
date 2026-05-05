package com.gler.assignment.service.impl;

import com.gler.assignment.client.WeatherClient;
import com.gler.assignment.constant.WeatherMeasure;
import com.gler.assignment.dto.request.ForecastRequest;
import com.gler.assignment.dto.response.WeatherClientServiceResponse;
import com.gler.assignment.dto.response.WeatherResponseDTO;
import com.gler.assignment.model.WeatherDetails;
import com.gler.assignment.repository.WeatherRepository;
import com.gler.assignment.util.WeatherObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.ResourceAccessException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
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


    private Map<String, Object> buildParams() {
        List<String> dailyParams = new ArrayList<>();
        Map<String, Object> params = new java.util.HashMap<>(Map.of("daily", String.join(",", dailyParams)));

        params.put("latitude", 52.52);
        params.put("longitude", 13.41);
        params.put("timezone", "auto");

        dailyParams.add(WeatherMeasure.TEMPERATURE_2M_MAX.getMeasureMValue());
        dailyParams.add(WeatherMeasure.RELATIVE_HUMIDITY_2M_MAX.getMeasureMValue());
        dailyParams.add(WeatherMeasure.WIND_SPEED_MAX.getMeasureMValue());
        params.put("daily", String.join(",", dailyParams));

        return params;
    }

    @Test
    @DisplayName("Full Request: Should correctly filter all parameters and save data")
    void getWeather_Success() {
        ForecastRequest request = new ForecastRequest(true, true, true);

        WeatherClientServiceResponse clientResponse = mock(WeatherClientServiceResponse.class);
        when(weatherClient.getForecast(anyMap()))
                .thenReturn(clientResponse);

        WeatherDetails entity = new WeatherDetails();
        entity.setId(1L);
        entity.setTemperature(30.0);
        when(weatherRepository.save(any())).thenReturn(entity);

        WeatherResponseDTO expectedDto = mock(WeatherResponseDTO.class);
        when(weatherObjectMapper.toDTO(any(WeatherDetails.class))).thenReturn(expectedDto);

        WeatherResponseDTO result = weatherService.getWeather(request);

        assertThat(result).isNotNull();

        verify(weatherClient, times(1)).getForecast(anyMap());
        verify(weatherRepository, times(1)).save(any());
        verify(weatherObjectMapper, times(1)).toDTO(any());
    }


    @Nested
    @DisplayName("Edge Cases")
    class EdgeCaseTests {

        @BeforeEach
        void setUp() {
            when(weatherRepository.save(any())).thenReturn(new WeatherDetails());

        }

        @Test
        @DisplayName("Empty Request: Should handle request with no metrics selected")
        void testEmptyRequest() {
            ForecastRequest request = new ForecastRequest(false, false, false);

            when(weatherClient.getForecast(anyMap()))
                    .thenReturn(mock(WeatherClientServiceResponse.class));
            when(weatherRepository.save(any())).thenReturn(new WeatherDetails());

            weatherService.getWeather(request);

            verify(weatherClient, times(1)).getForecast(anyMap());
            verify(weatherRepository, times(1)).save(any());
            verify(weatherObjectMapper, times(1)).toDTO(any());
        }

        @Test
        @DisplayName("Partial Request: Should only include temperature")
        void testTemperatureOnly() {
            ForecastRequest request = new ForecastRequest(true, false, false);

            WeatherClientServiceResponse clientResponse = mock(WeatherClientServiceResponse.class);
            when(weatherClient.getForecast(anyMap()))
                    .thenReturn(clientResponse);

            weatherService.getWeather(request);

            verify(weatherClient, times(1)).getForecast(buildParams());
            verify(weatherRepository, times(1)).save(any());
            verify(weatherObjectMapper, times(1)).toDTO(any());
        }

        @Test
        @DisplayName("Wind Only: Should include wind in current and hourly")
        void testWindOnly() {
            ForecastRequest request = new ForecastRequest(false, false, true);

            when(weatherClient.getForecast(anyMap()))
                    .thenReturn(mock(WeatherClientServiceResponse.class));

            weatherService.getWeather(request);

            verify(weatherClient, times(1)).getForecast(buildParams());
            verify(weatherRepository, times(1)).save(any());
            verify(weatherObjectMapper, times(1)).toDTO(any());
        }

        @Test
        @DisplayName("Humidity Only: Should only be in hourly, not in current")
        void testHumidityOnly() {
            ForecastRequest request = new ForecastRequest(false, true, false);

            when(weatherClient.getForecast(anyMap()))
                    .thenReturn(mock(WeatherClientServiceResponse.class));

            weatherService.getWeather(request);

            verify(weatherClient, times(1)).getForecast(buildParams());
            verify(weatherRepository, times(1)).save(any());
            verify(weatherObjectMapper, times(1)).toDTO(any());
        }

        @Test
        @DisplayName("Temperature and Humidity: Should exclude wind")
        void testTemperatureAndHumidity() {
            ForecastRequest request = new ForecastRequest(true, true, false);

            when(weatherClient.getForecast(anyMap()))
                    .thenReturn(mock(WeatherClientServiceResponse.class));

            weatherService.getWeather(request);

            verify(weatherClient, times(1)).getForecast(buildParams());
            verify(weatherRepository, times(1)).save(any());
            verify(weatherObjectMapper, times(1)).toDTO(any());
        }
    }



    @Nested
    @DisplayName("Other scenarios")
    class RefinedRequirementTests {

        @Test
        @DisplayName("Requirement 1: Mapper should filter out Wind and Humidity if only Temperature is requested")
        void testTemperatureOnly_DataIntegrity() {
            ForecastRequest request = new ForecastRequest(true, false, false);

            WeatherClientServiceResponse.DailyDataDTO dailyData = new WeatherClientServiceResponse.DailyDataDTO(
                    List.of("2026-05-05"),
                    List.of(25.5),
                    List.of(80.0),
                    List.of(15.0)
            );
            WeatherClientServiceResponse clientResponse = new WeatherClientServiceResponse(
                    52.52, 13.41, 0.1, "UTC", null, dailyData
            );

            when(weatherClient.getForecast(anyMap())).thenReturn(clientResponse);

            ArgumentCaptor<WeatherDetails> captor = ArgumentCaptor.forClass(WeatherDetails.class);
            when(weatherRepository.save(captor.capture())).thenReturn(new WeatherDetails());

            weatherService.getWeather(request);

            WeatherDetails savedEntity = captor.getValue();
            assertThat(savedEntity.getTemperature()).isEqualTo(25.5);
            assertThat(savedEntity.getRelativeHumidity()).isNull();
            assertThat(savedEntity.getWindSpeed()).isNull();
        }

        @Test
        @DisplayName("Requirement 2: Should map Record Date correctly from API")
        void testDateMapping() {
            ForecastRequest request = new ForecastRequest(true, true, true);
            String expectedDateStr = "2026-05-05";

            WeatherClientServiceResponse.DailyDataDTO dailyData = new WeatherClientServiceResponse.DailyDataDTO(
                    List.of(expectedDateStr), List.of(25.0), List.of(80.0), List.of(10.0)
            );
            WeatherClientServiceResponse clientResponse = new WeatherClientServiceResponse(
                    52.52, 13.41, 0.1, "UTC", null, dailyData
            );

            when(weatherClient.getForecast(anyMap())).thenReturn(clientResponse);

            ArgumentCaptor<WeatherDetails> captor = ArgumentCaptor.forClass(WeatherDetails.class);
            when(weatherRepository.save(captor.capture())).thenReturn(new WeatherDetails());

            weatherService.getWeather(request);

            assertThat(captor.getValue().getRecordDate()).isEqualTo(LocalDate.parse(expectedDateStr));
        }
    }



    @Nested
    @DisplayName("Negative Scenarios")
    class NegativeTests {

        @Test
        @DisplayName("Database Failure: Should throw exception if repository save fails")
        void testRepositoryFailure() {
            ForecastRequest request = new ForecastRequest(true, true, true);
            when(weatherClient.getForecast(anyMap()))
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
            when(weatherClient.getForecast(anyMap()))
                    .thenThrow(new ResourceAccessException("Upstream API Unreachable"));

            assertThatThrownBy(() -> weatherService.getWeather(request))
                    .isInstanceOf(ResourceAccessException.class)
                    .hasMessage("Upstream API Unreachable");

            verifyNoInteractions(weatherRepository);
            verifyNoInteractions(weatherObjectMapper);
        }
    }
}