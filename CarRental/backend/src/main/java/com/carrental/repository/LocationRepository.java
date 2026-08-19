package com.carrental.repository;

import com.carrental.entity.Location;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LocationRepository extends MongoRepository<Location, String> {

    List<Location> findByCityIdOrderByNameAsc(String cityId);

    Optional<Location> findByNameIgnoreCaseAndCityId(String name, String cityId);

    @Query("{ '$or': [ { 'name': { $regex: ?0, $options: 'i' } }, { 'address': { $regex: ?0, $options: 'i' } } ] }")
    List<Location> searchLocations(String query);
}
