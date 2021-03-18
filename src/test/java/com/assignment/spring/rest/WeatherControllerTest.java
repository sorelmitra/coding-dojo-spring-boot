package com.assignment.spring.rest;

import com.assignment.spring.api.endpoints.ApiEndpointWeather;
import com.assignment.spring.api.model.ApiModelMain;
import com.assignment.spring.api.response.ApiResponseWeather;
import com.assignment.spring.api.model.ApiModelSys;
import com.assignment.spring.db.WeatherEntity;
import com.assignment.spring.db.WeatherRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.LinkedList;
import java.util.List;

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

    private ApiModelMain apiModelMain = new ApiModelMain();
    private ApiModelSys apiModelSys = new ApiModelSys();
    ApiResponseWeather apiResponseWeather = new ApiResponseWeather();
    ResponseEntity<RestWeatherResponse> responseEntity;
    RestWeatherResponse response;

    WeatherEntity weatherEntity = new WeatherEntity();

    @Test
    void testNoCity() {
        responseEntity = controller.weather(null, false);
        response = responseEntity.getBody();
        assertEquals(false, response.getSuccess());
    }

    @Test
    void testUnauthorized() {
        String city = "Unauthorized";
        when(endpointWeather.buildUrl(anyString())).thenReturn(url);
        when(restTemplate.getForEntity(anyString(), eq(ApiResponseWeather.class))).thenThrow(HttpClientErrorException.Unauthorized.create(HttpStatus.UNAUTHORIZED, "unauthorized", HttpHeaders.EMPTY, null, null));
        responseEntity = controller.weather(city, false);
        response = responseEntity.getBody();
        assertEquals(false, response.getSuccess());
    }

    @Test
    void testNotFound() {
        String city = "Not Found";
        when(endpointWeather.buildUrl(anyString())).thenReturn(url);
        when(restTemplate.getForEntity(anyString(), eq(ApiResponseWeather.class))).thenThrow(HttpClientErrorException.Unauthorized.create(HttpStatus.NOT_FOUND, "not found", HttpHeaders.EMPTY, null, null));
        responseEntity = controller.weather(city, false);
        response = responseEntity.getBody();
        assertEquals(false, response.getSuccess());
    }

    @Test
    void testNullBody() {
        String city = "Null Body";
        when(endpointWeather.buildUrl(anyString())).thenReturn(url);
        when(restTemplate.getForEntity(anyString(), eq(ApiResponseWeather.class))).thenReturn(ResponseEntity.status(HttpStatus.OK).body(null));
        responseEntity = controller.weather(city, false);
        response = responseEntity.getBody();
        assertEquals(false, response.getSuccess());
    }

    @Test
    void testDatabaseException() {
        String city = "Database Exception";
        apiResponseWeather.setName(city);
        apiModelMain.setTemp(0.0);
        apiModelSys.setCountry("KO");
        apiResponseWeather.setMain(apiModelMain);
        apiResponseWeather.setSys(apiModelSys);
        when(endpointWeather.buildUrl(anyString())).thenReturn(url);
        when(restTemplate.getForEntity(anyString(), eq(ApiResponseWeather.class))).thenReturn(ResponseEntity.status(HttpStatus.OK).body(apiResponseWeather));
        when(weatherRepository.save(any(WeatherEntity.class))).thenThrow(new RuntimeException("database error"));
        responseEntity = controller.weather(city, false);
        response = responseEntity.getBody();
        assertEquals(false, response.getSuccess());
    }

    @Test
    void testSuccess() {
        String city = "Success";
        String country = "OK";
        apiResponseWeather.setName(city);
        apiModelMain.setTemp(100.3);
        apiModelSys.setCountry(country);
        apiResponseWeather.setMain(apiModelMain);
        apiResponseWeather.setSys(apiModelSys);
        when(endpointWeather.buildUrl(anyString())).thenReturn(url);
        when(restTemplate.getForEntity(anyString(), eq(ApiResponseWeather.class))).thenReturn(ResponseEntity.status(HttpStatus.OK).body(apiResponseWeather));
        when(weatherRepository.save(any(WeatherEntity.class))).thenAnswer(new Answer<WeatherEntity>() {
            @Override
            public WeatherEntity answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                return (WeatherEntity) args[0];
            }
        });
        responseEntity = controller.weather(city, false);
        response = responseEntity.getBody();
        assertEquals(true, response.getSuccess());
        assertEquals("", response.getReason());
        assertEquals(apiResponseWeather.getName(), response.getCity());
        assertEquals(apiResponseWeather.getMain().getTemp(), response.getTemperature());
        assertEquals(apiResponseWeather.getSys().getCountry(), response.getCountry());
    }

    @Test
    void testSuccessFake() {
        String city = "Success Fake";
        when(weatherRepository.save(any(WeatherEntity.class))).thenAnswer(new Answer<WeatherEntity>() {
            @Override
            public WeatherEntity answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                return (WeatherEntity) args[0];
            }
        });
        responseEntity = controller.weather(city, true);
        response = responseEntity.getBody();
        assertEquals(true, response.getSuccess());
        assertEquals("", response.getReason());
        assertEquals(city, response.getCity());
        assertTrue(100 < response.getTemperature());
        assertEquals("US", response.getCountry());
    }

    @Test
    void testExistingEntity() {
        String city = "Existing Entity";
        String country = "OK";
        apiResponseWeather.setName(city);
        apiModelMain.setTemp(100.3);
        apiModelSys.setCountry(country);
        apiResponseWeather.setMain(apiModelMain);
        apiResponseWeather.setSys(apiModelSys);
        when(endpointWeather.buildUrl(anyString())).thenReturn(url);
        when(restTemplate.getForEntity(anyString(), eq(ApiResponseWeather.class))).thenReturn(ResponseEntity.status(HttpStatus.OK).body(apiResponseWeather));
        List<WeatherEntity> existingEntities = new LinkedList<>();
        existingEntities.add(weatherEntity);
        weatherEntity.setCity(city);
        weatherEntity.setCountry(country);
        weatherEntity.setTemperature(253.2);
        when(weatherRepository.findByCountryAndCity(anyString(), anyString())).thenReturn(existingEntities);
        when(weatherRepository.save(any(WeatherEntity.class))).thenAnswer(new Answer<WeatherEntity>() {
            @Override
            public WeatherEntity answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                return (WeatherEntity) args[0];
            }
        });
        responseEntity = controller.weather(city, false);
        response = responseEntity.getBody();
        assertEquals(true, response.getSuccess());
        assertEquals("", response.getReason());
        assertEquals(apiResponseWeather.getName(), response.getCity());
        assertEquals(apiResponseWeather.getMain().getTemp(), response.getTemperature());
        assertEquals(apiResponseWeather.getSys().getCountry(), response.getCountry());
    }

    @Test
    void testOverlappingCityName() {
        String city = "Overlapping";
        String country = "OK";
        apiResponseWeather.setName(city);
        apiModelMain.setTemp(100.3);
        apiModelSys.setCountry(country);
        apiResponseWeather.setMain(apiModelMain);
        apiResponseWeather.setSys(apiModelSys);
        when(endpointWeather.buildUrl(anyString())).thenReturn(url);
        when(restTemplate.getForEntity(anyString(), eq(ApiResponseWeather.class))).thenReturn(ResponseEntity.status(HttpStatus.OK).body(apiResponseWeather));
        List<WeatherEntity> existingEntities = new LinkedList<>();
        existingEntities.add(weatherEntity);
        weatherEntity.setCity("Overlapping City Name");
        weatherEntity.setCountry(country);
        weatherEntity.setTemperature(253.2);
        when(weatherRepository.findByCountryAndCity(anyString(), anyString())).thenReturn(existingEntities);
        when(weatherRepository.save(any(WeatherEntity.class))).thenAnswer(new Answer<WeatherEntity>() {
            @Override
            public WeatherEntity answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                return (WeatherEntity) args[0];
            }
        });
        responseEntity = controller.weather(city, false);
        response = responseEntity.getBody();
        assertEquals(true, response.getSuccess());
        assertEquals("", response.getReason());
        assertEquals(apiResponseWeather.getName(), response.getCity());
        assertEquals(apiResponseWeather.getMain().getTemp(), response.getTemperature());
        assertEquals(apiResponseWeather.getSys().getCountry(), response.getCountry());
    }

    @Test
    void testOverlappingAndExactMatchCityName() {
        String city = "Overlapping";
        String country = "OK";
        apiResponseWeather.setName(city);
        apiModelMain.setTemp(100.3);
        apiModelSys.setCountry(country);
        apiResponseWeather.setMain(apiModelMain);
        apiResponseWeather.setSys(apiModelSys);
        when(endpointWeather.buildUrl(anyString())).thenReturn(url);
        when(restTemplate.getForEntity(anyString(), eq(ApiResponseWeather.class))).thenReturn(ResponseEntity.status(HttpStatus.OK).body(apiResponseWeather));
        List<WeatherEntity> existingEntities = new LinkedList<>();
        existingEntities.add(weatherEntity);
        weatherEntity.setCity("Overlapping City Name");
        weatherEntity.setCountry(country);
        weatherEntity.setTemperature(253.2);
        WeatherEntity weatherEntity2 = new WeatherEntity();
        weatherEntity2.setId(1678L);
        weatherEntity2.setCity("Overlapping");
        weatherEntity2.setCountry(country);
        weatherEntity2.setTemperature(100.4);
        existingEntities.add(weatherEntity2);
        when(weatherRepository.findByCountryAndCity(anyString(), anyString())).thenReturn(existingEntities);
        when(weatherRepository.save(any(WeatherEntity.class))).thenAnswer(new Answer<WeatherEntity>() {
            @Override
            public WeatherEntity answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                return (WeatherEntity) args[0];
            }
        });
        responseEntity = controller.weather(city, false);
        response = responseEntity.getBody();
        assertEquals(true, response.getSuccess());
        assertEquals("", response.getReason());
        assertEquals(weatherEntity2.getId(), response.getId());
        assertEquals(apiResponseWeather.getName(), response.getCity());
        assertEquals(apiResponseWeather.getMain().getTemp(), response.getTemperature());
        assertEquals(apiResponseWeather.getSys().getCountry(), response.getCountry());
    }

}