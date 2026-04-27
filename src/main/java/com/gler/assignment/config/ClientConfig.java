package com.gler.assignment.config;

import com.gler.assignment.client.WeatherClient;
import com.gler.assignment.exception.WeatherExceptionHandler;
import com.gler.assignment.properties.WeatherAPIProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

import java.time.Duration;

@Configuration
@EnableConfigurationProperties(WeatherAPIProperties.class)
public class ClientConfig {

    private final WeatherAPIProperties weatherProps;

    public ClientConfig(WeatherAPIProperties weatherProps) {
        this.weatherProps = weatherProps;
    }

    @Bean
    public WeatherClient weatherClient(RestClient.Builder builder) {
        JdkClientHttpRequestFactory requestFactory = new JdkClientHttpRequestFactory();
        requestFactory.setReadTimeout(Duration.ofSeconds(5));

        RestClient restClient = builder
                .baseUrl(weatherProps.endpoint())
                .requestFactory(requestFactory)
                .defaultHeader("Content-Type", "application/json")
                .defaultStatusHandler(new WeatherExceptionHandler())
                .build();

        HttpServiceProxyFactory factory = HttpServiceProxyFactory
                .builderFor(RestClientAdapter.create(restClient))
                .build();
        return factory.createClient(WeatherClient.class);
    }
}
