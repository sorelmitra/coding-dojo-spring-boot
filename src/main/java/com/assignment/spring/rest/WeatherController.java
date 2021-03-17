package com.assignment.spring.rest;

import com.assignment.spring.db.WeatherEntity;
import com.assignment.spring.db.WeatherRepository;
import com.assignment.spring.api.endpoints.ApiEndpointWeather;
import com.assignment.spring.api.model.ApiWeatherResponse;
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
    public ResponseEntity<RestWeatherResponse> weather(@RequestParam String city) {
        String methodName = "weather";

        LOG.info("{}: city <{}>", methodName, city);
        if (city == null) {
            return buildFailureResponse(HttpStatus.BAD_REQUEST, "Missing mandatory request parameter 'city'");
        }

        String url = endpointWeather.buildUrl(city);
        LOG.trace("{}: URL <{}>", methodName, url);
        ResponseEntity<ApiWeatherResponse> apiResponse;
        try {
            apiResponse = restTemplate.getForEntity(url, ApiWeatherResponse.class);
        } catch (HttpClientErrorException.Unauthorized e) {
            return buildFailureResponse(HttpStatus.SERVICE_UNAVAILABLE, e.getMessage());
        } catch (HttpClientErrorException.NotFound e) {
            return buildFailureResponse(HttpStatus.NOT_FOUND, e.getMessage());
        }

        ApiWeatherResponse apiWeatherResponse = apiResponse.getBody();
        if (apiWeatherResponse == null) {
            return buildFailureResponse(HttpStatus.SERVICE_UNAVAILABLE, String.format("OpenWeatherMap returned empty body and status code %s", apiResponse.getStatusCode()));
        }

        try {
            save(apiWeatherResponse);
        } catch (Exception e) {
            return buildFailureResponse(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }

        return buildSuccessResponse(apiWeatherResponse);
    }

    private ResponseEntity<RestWeatherResponse> buildFailureResponse(HttpStatus code, String reason) {
        String methodName = "buildFailureResponse";
        RestWeatherResponse response = new RestWeatherResponse();
        response.setSuccess(false);
        response.setReason(reason);
        LOG.info("{}: API failure: reason <{}>", methodName, response.getReason());
        return ResponseEntity.status(code).body(response);
    }

    private ResponseEntity<RestWeatherResponse> buildSuccessResponse(ApiWeatherResponse apiWeatherResponse) {
        String methodName = "buildSuccessResponse";
        RestWeatherResponse response = new RestWeatherResponse();
        response.setSuccess(true);
        response.setReason("");
        response.setCity(apiWeatherResponse.getName());
        response.setCountry(apiWeatherResponse.getSys().getCountry());
        response.setTemperature(apiWeatherResponse.getMain().getTemp());
        LOG.info("{}: API success for city <{}>: temp <{}>", methodName, response.getCity(), response.getTemperature());
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    private WeatherEntity save(ApiWeatherResponse response) {
        WeatherEntity entity = new WeatherEntity();
        entity.setCity(response.getName());
        entity.setCountry(response.getSys().getCountry());
        entity.setTemperature(response.getMain().getTemp());
        return weatherRepository.save(entity);
    }
}
