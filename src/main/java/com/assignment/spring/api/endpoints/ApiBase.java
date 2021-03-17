package com.assignment.spring.api.endpoints;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class ApiBase {
    private String baseUrl;
    private String appKeyParam;
    private String appKey;

    @Autowired
    public ApiBase(
            @Value("${openWeatherMap.baseUrl}") String baseUrl,
            @Value("${openWeatherMap.appKey.param}") String appKeyParam,
            @Value("${openWeatherMap.appKey.value}") String appKey
    ) {
        this.baseUrl = baseUrl;
        this.appKeyParam = appKeyParam;
        this.appKey = appKey;
    }

    public UriComponentsBuilder buildPath() {
        return UriComponentsBuilder.fromHttpUrl(baseUrl);
    }

    public UriComponentsBuilder addKeyParam(UriComponentsBuilder builder) {
        return builder.queryParam(appKeyParam, appKey);
    }
}
