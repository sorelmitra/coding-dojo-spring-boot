package com.assignment.spring.db;

import org.springframework.data.repository.CrudRepository;

public interface WeatherRepository extends CrudRepository<WeatherEntity, Integer> {

    Iterable<WeatherEntity> findByCityId(Long city);

}
