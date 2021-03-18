package com.assignment.spring.db;

import com.assignment.spring.db.WeatherEntity;
import org.springframework.data.repository.CrudRepository;

public interface WeatherRepository extends CrudRepository<WeatherEntity, Integer> {

    Iterable<WeatherEntity> findByCountryAndCity(String country, String city);

}
