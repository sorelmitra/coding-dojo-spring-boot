package com.assignment.spring.api.endpoints;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class ApiEndpointWeather {
    private ApiBase apiBase;
    private String path;
    private String cityParam;

    @Autowired
    public ApiEndpointWeather(
            ApiBase apiBase,
            @Value("${openWeatherMap.weather.path}") String path,
            @Value("${openWeatherMap.weather.params.city}") String cityParam
    ) {
        this.apiBase = apiBase;
        this.path = path;
        this.cityParam = cityParam;
    }

    public String buildUrl(String city) {
        return apiBase.addKeyParam(buildPath())
                .queryParam(cityParam, city)
                .build().toUriString();
    }

    private UriComponentsBuilder buildPath() {
        return apiBase.buildPath()
                .pathSegment(path);
    }
}
