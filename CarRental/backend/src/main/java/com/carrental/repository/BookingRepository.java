package com.carrental.repository;

import com.carrental.entity.Booking;
import com.carrental.entity.BookingStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BookingRepository extends MongoRepository<Booking, String> {

    List<Booking> findByUserIdOrderByCreatedAtDesc(String userId);

    List<Booking> findAllByOrderByCreatedAtDesc();

    long countByBookingStatus(BookingStatus status);

    @Query("{ 'carId': ?0, 'bookingStatus': 'ACCEPTED', 'pickupDate': { '$lte': ?2 }, 'returnDate': { '$gte': ?1 } }")
    List<Booking> findOverlappingBookings(String carId, LocalDate pickupDate, LocalDate returnDate);
}
