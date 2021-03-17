package com.assignment.spring.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class ApiEndpointWeather {
    private ApiConfig apiConfig;
    private String path;
    private String cityParam;

    @Autowired
    public ApiEndpointWeather(
            ApiConfig apiConfig,
            @Value("${openWeatherMap.weather.path}") String path,
            @Value("${openWeatherMap.weather.params.city}") String cityParam
    ) {
        this.apiConfig = apiConfig;
        this.path = path;
        this.cityParam = cityParam;
    }

    public String buildUrl(String city) {
        return apiConfig.addKeyParam(buildPath())
                .queryParam(cityParam, city)
                .toUriString();
    }

    private UriComponentsBuilder buildPath() {
        return apiConfig.buildPath()
                .pathSegment(path);
    }
}
