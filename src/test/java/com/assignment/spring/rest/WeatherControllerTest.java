package com.assignment.spring.rest;

import com.assignment.spring.api.endpoints.ApiEndpointWeather;
import com.assignment.spring.api.model.ApiWeatherResponse;
import com.assignment.spring.api.model.Main;
import com.assignment.spring.api.model.Sys;
import com.assignment.spring.db.WeatherEntity;
import com.assignment.spring.db.WeatherRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WeatherControllerTest {

    @Mock
    private RestTemplate restTemplate;
    @Mock
    private WeatherRepository weatherRepository;
    @Mock
    private ApiEndpointWeather endpointWeather;

    @InjectMocks
    private WeatherController controller;

    private String url = "http://baz.bum/pic?x=f&y=a b";

    private Main main = new Main();
    private Sys sys = new Sys();
    ApiWeatherResponse apiWeatherResponse = new ApiWeatherResponse();
    ResponseEntity<RestWeatherResponse> responseEntity;
    RestWeatherResponse response;

    WeatherEntity weatherEntity = new WeatherEntity();

    @Test
    void testNoCity() {
        responseEntity = controller.weather(null);
        response = responseEntity.getBody();
        assertEquals(false, response.getSuccess());
    }

    @Test
    void testUnauthorized() {
        String city = "Unauthorized";
        when(endpointWeather.buildUrl(anyString())).thenReturn(url);
        when(restTemplate.getForEntity(anyString(), eq(ApiWeatherResponse.class))).thenThrow(HttpClientErrorException.Unauthorized.create(HttpStatus.UNAUTHORIZED, "unauthorized", HttpHeaders.EMPTY, null, null));
        responseEntity = controller.weather(city);
        response = responseEntity.getBody();
        assertEquals(false, response.getSuccess());
    }

    @Test
    void testNotFound() {
        String city = "Not Found";
        when(endpointWeather.buildUrl(anyString())).thenReturn(url);
        when(restTemplate.getForEntity(anyString(), eq(ApiWeatherResponse.class))).thenThrow(HttpClientErrorException.Unauthorized.create(HttpStatus.NOT_FOUND, "not found", HttpHeaders.EMPTY, null, null));
        responseEntity = controller.weather(city);
        response = responseEntity.getBody();
        assertEquals(false, response.getSuccess());
    }

    @Test
    void testNullBody() {
        String city = "Null Body";
        when(endpointWeather.buildUrl(anyString())).thenReturn(url);
        when(restTemplate.getForEntity(anyString(), eq(ApiWeatherResponse.class))).thenReturn(ResponseEntity.status(HttpStatus.OK).body(null));
        responseEntity = controller.weather(city);
        response = responseEntity.getBody();
        assertEquals(false, response.getSuccess());
    }

    @Test
    void testDatabaseException() {
        String city = "Database Exception";
        apiWeatherResponse.setName(city);
        main.setTemp(0.0);
        sys.setCountry("KO");
        apiWeatherResponse.setMain(main);
        apiWeatherResponse.setSys(sys);
        when(endpointWeather.buildUrl(anyString())).thenReturn(url);
        when(restTemplate.getForEntity(anyString(), eq(ApiWeatherResponse.class))).thenReturn(ResponseEntity.status(HttpStatus.OK).body(apiWeatherResponse));
        when(weatherRepository.save(any(WeatherEntity.class))).thenThrow(new RuntimeException("database error"));
        responseEntity = controller.weather(city);
        response = responseEntity.getBody();
        assertEquals(false, response.getSuccess());
    }

    @Test
    void testSuccess() {
        String city = "Success";
        apiWeatherResponse.setName(city);
        main.setTemp(100.3);
        sys.setCountry("OK");
        apiWeatherResponse.setMain(main);
        apiWeatherResponse.setSys(sys);
        when(endpointWeather.buildUrl(anyString())).thenReturn(url);
        when(restTemplate.getForEntity(anyString(), eq(ApiWeatherResponse.class))).thenReturn(ResponseEntity.status(HttpStatus.OK).body(apiWeatherResponse));
        when(weatherRepository.save(any(WeatherEntity.class))).thenReturn(weatherEntity);
        responseEntity = controller.weather(city);
        response = responseEntity.getBody();
        assertEquals(true, response.getSuccess());
        assertEquals("", response.getReason());
        assertEquals(apiWeatherResponse.getName(), response.getCity());
        assertEquals(apiWeatherResponse.getMain().getTemp(), response.getTemperature());
        assertEquals(apiWeatherResponse.getSys().getCountry(), response.getCountry());
    }

}