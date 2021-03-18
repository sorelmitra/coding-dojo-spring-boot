package com.assignment.spring.rest;

import com.assignment.spring.api.model.ApiModelMain;
import com.assignment.spring.api.model.ApiModelSys;
import com.assignment.spring.db.WeatherEntity;
import com.assignment.spring.db.WeatherRepository;
import com.assignment.spring.api.endpoints.ApiEndpointWeather;
import com.assignment.spring.api.response.ApiResponseWeather;
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

import java.util.Iterator;
import java.util.Random;
import java.util.concurrent.Callable;

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
    public Callable<ResponseEntity<RestWeatherResponse>> weather(@RequestParam String city, @RequestParam(defaultValue = "-1") Integer fake) {
        Callable<ResponseEntity<RestWeatherResponse>> task = () -> {
            String methodName = "weather";

            LOG.info("{}: city <{}>", methodName, city);
            if (city == null) {
                return buildFailureResponse(HttpStatus.BAD_REQUEST, "Missing mandatory request parameter 'city'");
            }

            ResponseEntity<ApiResponseWeather> apiResponse;
            if (fake > -1) {
                apiResponse = buildFakeApiResponse(city, fake);
            } else {
                String url = endpointWeather.buildUrl(city);
                LOG.trace("{}: URL <{}>", methodName, url);
                try {
                    apiResponse = restTemplate.getForEntity(url, ApiResponseWeather.class);
                } catch (HttpClientErrorException.Unauthorized e) {
                    return buildFailureResponse(HttpStatus.SERVICE_UNAVAILABLE, e.getMessage());
                } catch (HttpClientErrorException.NotFound e) {
                    return buildFailureResponse(HttpStatus.NOT_FOUND, e.getMessage());
                }
            }

            ApiResponseWeather apiResponseWeather = apiResponse.getBody();
            if (apiResponseWeather == null) {
                return buildFailureResponse(HttpStatus.SERVICE_UNAVAILABLE, String.format("OpenWeatherMap returned empty body and status code %s", apiResponse.getStatusCode()));
            }

            try {
                WeatherEntity weatherEntity = save(apiResponseWeather);
                return buildSuccessResponse(weatherEntity);
            } catch (Exception e) {
                LOG.trace("ERROR while accessing database", e);
                return buildFailureResponse(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
            }
        };
        return task;
    }

    private ResponseEntity<ApiResponseWeather> buildFakeApiResponse(String city, Integer fake) {
        ApiResponseWeather apiResponseWeather = new ApiResponseWeather();
        apiResponseWeather.setName(city);
        ApiModelMain apiModelMain = new ApiModelMain();
        Random random = new Random();
        apiModelMain.setTemp(
                Math.round((100 + 100 * random.nextDouble()) * 100) / 100.0);
        ApiModelSys apiModelSys = new ApiModelSys();
        apiModelSys.setCountry("US");
        apiResponseWeather.setMain(apiModelMain);
        apiResponseWeather.setSys(apiModelSys);
        if (fake > 0) {
            try {
                Thread.sleep(fake);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return ResponseEntity.status(HttpStatus.OK).body(apiResponseWeather);
    }

    private ResponseEntity<RestWeatherResponse> buildFailureResponse(HttpStatus code, String reason) {
        String methodName = "buildFailureResponse";
        RestWeatherResponse response = new RestWeatherResponse();
        response.setSuccess(false);
        response.setReason(reason);
        LOG.info("{}: API failure: reason <{}>", methodName, response.getReason());
        return ResponseEntity.status(code).body(response);
    }

    private ResponseEntity<RestWeatherResponse> buildSuccessResponse(WeatherEntity weatherEntity) {
        String methodName = "buildSuccessResponse";
        RestWeatherResponse response = new RestWeatherResponse();
        response.setSuccess(true);
        response.setReason("");
        response.setId(weatherEntity.getId());
        response.setCity(weatherEntity.getCity());
        response.setCountry(weatherEntity.getCountry());
        response.setTemperature(weatherEntity.getTemperature());
        LOG.info("{}: API success for city <{}>: temp <{}>", methodName, response.getCity(), response.getTemperature());
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    private WeatherEntity save(ApiResponseWeather response) {
        Iterable<WeatherEntity> existingEntities = weatherRepository.findByCountryAndCity(response.getSys().getCountry(), response.getName());
        WeatherEntity entity = null;
        Iterator<WeatherEntity> it = existingEntities.iterator();
        while (it.hasNext()) {
            WeatherEntity existingEntity = it.next();
            if (existingEntity.getCity().equals(response.getName())) {
                existingEntity.setTemperature(response.getMain().getTemp());
                entity = existingEntity;
                break;
            }
        }
        if (entity == null) {
            entity = createWeatherEntity(response);
        }
        return weatherRepository.save(entity);
    }

    private WeatherEntity createWeatherEntity(ApiResponseWeather response) {
        WeatherEntity entity = new WeatherEntity();
        entity.setCity(response.getName());
        entity.setCountry(response.getSys().getCountry());
        entity.setTemperature(response.getMain().getTemp());
        return entity;
    }
}
