package com.assignment.spring.rest;

import com.assignment.spring.WeatherEntity;
import com.assignment.spring.WeatherRepository;
import com.assignment.spring.api.ApiEndpointWeather;
import com.assignment.spring.api.WeatherResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@RestController
public class WeatherController {

    private static final Logger LOG = LoggerFactory.getLogger(WeatherController.class);

    private RestTemplate restTemplate;
    private WeatherRepository weatherRepository;
    private ApiEndpointWeather endpointWeather;

    @Autowired
    public WeatherController(RestTemplate restTemplate, WeatherRepository weatherRepository, ApiEndpointWeather endpointWeather) {
        this.restTemplate = restTemplate;
        this.weatherRepository = weatherRepository;
        this.endpointWeather = endpointWeather;
    }

    @PostMapping("/weather")
    public ResponseEntity<WeatherRestResponse> weather(@RequestParam String city) {
        String methodName = "weather";

        LOG.info("{}: city <{}>", methodName, city);
        if (city == null) {
            return buildFailureResponse(HttpStatus.BAD_REQUEST, "Missing mandatory request parameter 'city'");
        }

        String url = endpointWeather.buildUrl(city);
        LOG.trace("{}: URL <{}>", methodName, url);
        ResponseEntity<WeatherResponse> apiResponse;
        try {
            apiResponse = restTemplate.getForEntity(url, WeatherResponse.class);
        } catch (HttpClientErrorException.Unauthorized e) {
            return buildFailureResponse(HttpStatus.SERVICE_UNAVAILABLE, e.getMessage());
        } catch (HttpClientErrorException.NotFound e) {
            return buildFailureResponse(HttpStatus.NOT_FOUND, e.getMessage());
        }

        WeatherResponse weatherResponse = apiResponse.getBody();
        if (weatherResponse == null) {
            return buildFailureResponse(HttpStatus.SERVICE_UNAVAILABLE, String.format("OpenWeatherMap returned empty body and status code %s", apiResponse.getStatusCode()));
        }
        save(weatherResponse);

        return buildSuccessResponse(weatherResponse);
    }

    private ResponseEntity<WeatherRestResponse> buildFailureResponse(HttpStatus code, String reason) {
        String methodName = "buildFailureResponse";
        WeatherRestResponse response = new WeatherRestResponse();
        response.setSuccess(false);
        response.setReason(reason);
        LOG.info("{}: API failure: reason <{}>", methodName, response.getCity(), response.getReason());
        return ResponseEntity.status(code).body(response);
    }

    private ResponseEntity<WeatherRestResponse> buildSuccessResponse(WeatherResponse weatherResponse) {
        String methodName = "buildSuccessResponse";
        WeatherRestResponse response = new WeatherRestResponse();
        response.setCity(weatherResponse.getName());
        response.setCountry(weatherResponse.getSys().getCountry());
        response.setTemperature(weatherResponse.getMain().getTemp());
        LOG.info("{}: API success for city <{}>: temp <{}>", methodName, response.getCity(), response.getTemperature());
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    private WeatherEntity save(WeatherResponse response) {
        WeatherEntity entity = new WeatherEntity();
        entity.setCity(response.getName());
        entity.setCountry(response.getSys().getCountry());
        entity.setTemperature(response.getMain().getTemp());

        return weatherRepository.save(entity);
    }
}
