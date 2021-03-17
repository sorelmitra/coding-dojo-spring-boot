package com.assignment.spring.rest;

import com.assignment.spring.Constants;
import com.assignment.spring.WeatherEntity;
import com.assignment.spring.WeatherRepository;
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

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private WeatherRepository weatherRepository;

    @PostMapping("/weather")
    public ResponseEntity<WeatherRestResponse> weather(@RequestParam String city) {
        String methodName = "weather";

        LOG.info("{}: city <{}>", methodName, city);
        if (city == null) {
            return buildFailureResponse(HttpStatus.BAD_REQUEST, "Missing mandatory request parameter 'city'");
        }

        String url = Constants.WEATHER_API_URL.replace("{city}", city).replace("{appid}", Constants.APP_ID);
        ResponseEntity<WeatherResponse> apiResponse = null;
        try {
            apiResponse = restTemplate.getForEntity(url, WeatherResponse.class);
        } catch (HttpClientErrorException.Unauthorized e) {
            return buildFailureResponse(HttpStatus.SERVICE_UNAVAILABLE, e.getMessage());
        }

        WeatherResponse weatherResponse = apiResponse.getBody();
        save(weatherResponse);

        return buildSuccessResponse(weatherResponse);
    }

    private ResponseEntity<WeatherRestResponse> buildFailureResponse(HttpStatus code, String reason) {
        WeatherRestResponse response = new WeatherRestResponse();
        response.setSuccess(false);
        response.setReason(reason);
        return ResponseEntity.status(code).body(response);
    }

    private ResponseEntity<WeatherRestResponse> buildSuccessResponse(WeatherResponse weatherResponse) {
        WeatherRestResponse response = new WeatherRestResponse();
        response.setCity(weatherResponse.getName());
        response.setCountry(weatherResponse.getSys().getCountry());
        response.setTemperature(weatherResponse.getMain().getTemp());
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
