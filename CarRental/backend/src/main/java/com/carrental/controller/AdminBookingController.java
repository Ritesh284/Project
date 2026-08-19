package com.carrental.controller;

import com.carrental.dto.BookingResponse;
import com.carrental.dto.DenyRequest;
import com.carrental.service.BookingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/bookings")
public class AdminBookingController {

    private final BookingService bookingService;

    public AdminBookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @GetMapping
    public ResponseEntity<List<BookingResponse>> getAllBookings() {
        return ResponseEntity.ok(bookingService.getAllBookingsForAdmin());
    }

    @PutMapping("/{id}/accept")
    public ResponseEntity<BookingResponse> acceptBooking(@PathVariable String id) {
        return ResponseEntity.ok(bookingService.acceptBooking(id));
    }

    @PutMapping("/{id}/deny")
    public ResponseEntity<BookingResponse> denyBooking(@PathVariable String id, @RequestBody(required = false) DenyRequest request) {
        String adminNote = (request != null) ? request.getAdminNote() : null;
        return ResponseEntity.ok(bookingService.denyBooking(id, adminNote));
    }

    @PutMapping("/{id}/complete")
    public ResponseEntity<BookingResponse> completeBooking(@PathVariable String id) {
        return ResponseEntity.ok(bookingService.completeBooking(id));
    }
}
