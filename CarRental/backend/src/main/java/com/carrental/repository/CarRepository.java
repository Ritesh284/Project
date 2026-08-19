package com.carrental.repository;

import com.carrental.entity.Car;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CarRepository extends MongoRepository<Car, String> {

    List<Car> findByAvailableTrueOrderByCreatedAtDesc();

    List<Car> findAllByOrderByCreatedAtDesc();

    List<Car> findByAvailableTrueAndCategoryIgnoreCaseOrderByCreatedAtDesc(String category);

    @Query("{ 'available': true, '$or': [ { 'brand': { $regex: ?0, $options: 'i' } }, { 'carName': { $regex: ?0, $options: 'i' } }, { 'category': { $regex: ?0, $options: 'i' } } ] }")
    List<Car> searchAvailableCars(String keyword);
}
