package com.carrental.repository;

import com.carrental.entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, String> {

    Optional<User> findByEmail(String email);

    Optional<User> findByEmailIgnoreCase(String email);

    Optional<User> findByMobileNumber(String mobileNumber);

    @Query("{ '$or': [ { 'email': { '$regex': '^?0$', '$options': 'i' } }, { 'mobileNumber': ?0 } ] }")
    Optional<User> findByEmailOrMobileNumber(String identifier);

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByEmail(String email);

    boolean existsByMobileNumber(String mobileNumber);
}
