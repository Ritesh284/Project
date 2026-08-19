package com.carrental.repository;

import com.carrental.entity.State;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StateRepository extends MongoRepository<State, String> {

    List<State> findAllByOrderByNameAsc();

    Optional<State> findByNameIgnoreCase(String name);
}
