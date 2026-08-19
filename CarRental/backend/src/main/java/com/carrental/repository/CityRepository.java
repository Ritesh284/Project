package com.carrental.repository;

import com.carrental.entity.City;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CityRepository extends MongoRepository<City, String> {

    List<City> findByStateIdOrderByNameAsc(String stateId);

    Optional<City> findByNameIgnoreCaseAndStateId(String name, String stateId);
}
