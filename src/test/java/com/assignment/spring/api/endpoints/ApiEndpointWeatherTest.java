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
    private String cityParam = "x";

    private ApiEndpointWeather endpointWeather = new ApiEndpointWeather(apiBase, path, cityParam);

    @Test void buildUrlBasic() {
        String city = "Denomination";
        assertEquals(expectedUri(city), endpointWeather.buildUrl(city));
    }

    @Test void buildUrlWithSpaces() {
        String city = "Has some spaces";
        assertEquals(expectedUri(city), endpointWeather.buildUrl(city));
    }

    private String expectedUri(String city) {
        return String.format("%s/%s?%s=%s&%s=%s", baseUrl, path, appKeyParam, appKey, cityParam, city);
    }
}