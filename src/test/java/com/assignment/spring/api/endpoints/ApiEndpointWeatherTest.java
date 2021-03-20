package com.assignment.spring.api.endpoints;

import org.junit.jupiter.api.Test;
import org.springframework.web.util.UriComponentsBuilder;

import static org.junit.jupiter.api.Assertions.*;

class ApiEndpointWeatherTest {

    private UriComponentsBuilder builder;

    private String baseUrl = "http://dummy.site";
    private String appKeyParam = "app-key";
    private String appKey = "dummyappkey";

    private ApiBase apiBase = new ApiBase(baseUrl, appKeyParam, appKey);

    private String path = "foo";
    private String cityParam = "y";
    private String cityIdParam = "x";

    private ApiEndpointWeather endpointWeather = new ApiEndpointWeather(apiBase, path, cityIdParam, cityIdParam);

    @Test void buildUrlBasic() {
        Long cityId = 4853497593L;
        assertEquals(expectedUri(cityId), endpointWeather.buildUrl(cityId));
    }

    private String expectedUri(Long cityId) {
        return String.format("%s/%s?%s=%s&%s=%s", baseUrl, path, appKeyParam, appKey, cityIdParam, cityId);
    }
}