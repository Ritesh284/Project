package com.carrental.service;

import com.carrental.dto.BookingRequest;
import com.carrental.dto.BookingResponse;
import com.carrental.dto.DashboardStatsDTO;
import com.carrental.dto.UserDTO;
import com.carrental.entity.*;
import com.carrental.exception.BadRequestException;
import com.carrental.exception.ResourceNotFoundException;
import com.carrental.repository.BookingRepository;
import com.carrental.repository.CarRepository;
import com.carrental.repository.UserRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final CarRepository carRepository;
    private final UserRepository userRepository;
    private final AuthService authService;

    public BookingService(BookingRepository bookingRepository,
                          CarRepository carRepository,
                          UserRepository userRepository,
                          AuthService authService) {
        this.bookingRepository = bookingRepository;
        this.carRepository = carRepository;
        this.userRepository = userRepository;
        this.authService = authService;
    }

    public BookingResponse createBooking(BookingRequest request) {
        User currentUser = authService.getCurrentAuthenticatedUser();

        Car car = carRepository.findById(request.getCarId())
                .orElseThrow(() -> new ResourceNotFoundException("Car not found with ID: " + request.getCarId()));

        if (!car.isAvailable()) {
            throw new BadRequestException("Car is currently marked as unavailable for booking.");
        }

        LocalDate pickup = request.getPickupDate();
        LocalDate returnD = request.getReturnDate();

        if (pickup.isBefore(LocalDate.now())) {
            throw new BadRequestException("Pick-up date cannot be in the past.");
        }

        if (returnD.isBefore(pickup)) {
            throw new BadRequestException("Return date cannot be earlier than pick-up date.");
        }

        // Check date conflict with existing ACCEPTED bookings
        boolean hasOverlap = bookingRepository.findAll().stream()
                .filter(b -> b.getCarId() != null && b.getCarId().equals(car.getId()))
                .filter(b -> b.getBookingStatus() == BookingStatus.ACCEPTED)
                .anyMatch(b -> {
                    LocalDate bStart = b.getPickupDate();
                    LocalDate bEnd = b.getReturnDate();
                    return !(returnD.isBefore(bStart) || pickup.isAfter(bEnd));
                });

        if (hasOverlap) {
            throw new BadRequestException("This car already has an accepted booking overlapping with your selected dates.");
        }

        long days = ChronoUnit.DAYS.between(pickup, returnD);
        if (days <= 0) {
            days = 1;
        }

        double totalAmount = days * car.getPricePerDay();

        Booking booking = new Booking();
        booking.setUserId(currentUser.getId());
        booking.setUserName(currentUser.getName());
        booking.setUserEmail(currentUser.getEmail());
        booking.setUserMobile(currentUser.getMobileNumber());

        booking.setCarId(car.getId());
        booking.setCarBrand(car.getBrand());
        booking.setCarModel(car.getModel());
        booking.setCarName(car.getCarName());
        booking.setCarImage(car.getImage());
        booking.setPricePerDay(car.getPricePerDay());

        booking.setPickupState(request.getPickupState());
        booking.setPickupCity(request.getPickupCity());
        booking.setPickupLocation(request.getPickupLocation());

        booking.setDropState(request.getDropState());
        booking.setDropCity(request.getDropCity());
        booking.setDropLocation(request.getDropLocation());

        booking.setPickupDate(pickup);
        booking.setReturnDate(returnD);
        booking.setTotalDays(days);
        booking.setTotalAmount(totalAmount);

        booking.setBookingStatus(BookingStatus.PENDING);
        booking.setPaymentStatus(PaymentStatus.PAID);

        Booking saved = bookingRepository.save(booking);
        return new BookingResponse(saved);
    }

    public List<BookingResponse> getMyBookings() {
        User currentUser = authService.getCurrentAuthenticatedUser();
        return bookingRepository.findByUserIdOrderByCreatedAtDesc(currentUser.getId())
                .stream()
                .map(BookingResponse::new)
                .collect(Collectors.toList());
    }

    public BookingResponse getBookingById(String id) {
        User currentUser = authService.getCurrentAuthenticatedUser();
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with ID: " + id));

        if (currentUser.getRole() != Role.ROLE_ADMIN && !booking.getUserId().equals(currentUser.getId())) {
            throw new AccessDeniedException("You do not have permission to view this booking");
        }

        return new BookingResponse(booking);
    }

    public BookingResponse cancelBooking(String id) {
        User currentUser = authService.getCurrentAuthenticatedUser();
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with ID: " + id));

        if (currentUser.getRole() != Role.ROLE_ADMIN && !booking.getUserId().equals(currentUser.getId())) {
            throw new AccessDeniedException("You do not have permission to cancel this booking");
        }

        if (booking.getBookingStatus() == BookingStatus.COMPLETED) {
            throw new BadRequestException("Completed bookings cannot be cancelled");
        }

        booking.setBookingStatus(BookingStatus.CANCELLED);
        Booking updated = bookingRepository.save(booking);
        return new BookingResponse(updated);
    }

    // ==========================================
    // ADMIN BOOKING OPERATIONS
    // ==========================================

    public List<BookingResponse> getAllBookingsForAdmin() {
        return bookingRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(BookingResponse::new)
                .collect(Collectors.toList());
    }

    public BookingResponse acceptBooking(String id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with ID: " + id));

        if (booking.getBookingStatus() == BookingStatus.CANCELLED) {
            throw new BadRequestException("Cannot accept a booking that was already cancelled by the customer.");
        }

        // Verify if conflict exists with other accepted bookings
        LocalDate pickup = booking.getPickupDate();
        LocalDate returnD = booking.getReturnDate();

        boolean conflictExists = bookingRepository.findAll().stream()
                .filter(b -> !b.getId().equals(id))
                .filter(b -> b.getCarId() != null && b.getCarId().equals(booking.getCarId()))
                .filter(b -> b.getBookingStatus() == BookingStatus.ACCEPTED)
                .anyMatch(b -> {
                    LocalDate bStart = b.getPickupDate();
                    LocalDate bEnd = b.getReturnDate();
                    return !(returnD.isBefore(bStart) || pickup.isAfter(bEnd));
                });

        if (conflictExists) {
            throw new BadRequestException("Cannot accept this booking because another accepted booking overlaps with these dates.");
        }

        booking.setBookingStatus(BookingStatus.ACCEPTED);
        booking.setAdminNote("Ride request approved by Admin");
        Booking updated = bookingRepository.save(booking);
        return new BookingResponse(updated);
    }

    public BookingResponse denyBooking(String id, String reason) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with ID: " + id));

        booking.setBookingStatus(BookingStatus.DENIED);
        booking.setAdminNote(reason != null && !reason.trim().isEmpty() ? reason.trim() : "Ride request declined by Administrator");
        Booking updated = bookingRepository.save(booking);
        return new BookingResponse(updated);
    }

    public BookingResponse completeBooking(String id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with ID: " + id));

        booking.setBookingStatus(BookingStatus.COMPLETED);
        booking.setPaymentStatus(PaymentStatus.PAID);
        Booking updated = bookingRepository.save(booking);
        return new BookingResponse(updated);
    }

    public DashboardStatsDTO getDashboardStats() {
        long totalUsers = userRepository.count();
        long totalCars = carRepository.count();
        List<Booking> allBookings = bookingRepository.findAll();

        long pendingRides = allBookings.stream().filter(b -> b.getBookingStatus() == BookingStatus.PENDING).count();
        long acceptedRides = allBookings.stream().filter(b -> b.getBookingStatus() == BookingStatus.ACCEPTED).count();
        long deniedRides = allBookings.stream().filter(b -> b.getBookingStatus() == BookingStatus.DENIED).count();
        long completedRides = allBookings.stream().filter(b -> b.getBookingStatus() == BookingStatus.COMPLETED).count();

        double totalRevenue = allBookings.stream()
                .filter(b -> b.getBookingStatus() == BookingStatus.ACCEPTED || b.getBookingStatus() == BookingStatus.COMPLETED)
                .mapToDouble(b -> b.getTotalAmount() != null ? b.getTotalAmount() : 0.0)
                .sum();

        return new DashboardStatsDTO(
                totalUsers,
                totalCars,
                pendingRides,
                acceptedRides,
                deniedRides,
                completedRides,
                totalRevenue
        );
    }

    public List<UserDTO> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(UserDTO::new)
                .collect(Collectors.toList());
    }
}
