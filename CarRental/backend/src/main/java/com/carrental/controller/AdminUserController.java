package com.carrental.controller;

import com.carrental.dto.DashboardStatsDTO;
import com.carrental.dto.UserDTO;
import com.carrental.service.BookingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminUserController {

    private final BookingService bookingService;

    public AdminUserController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        return ResponseEntity.ok(bookingService.getAllUsers());
    }

    @GetMapping("/dashboard/stats")
    public ResponseEntity<DashboardStatsDTO> getDashboardStats() {
        return ResponseEntity.ok(bookingService.getDashboardStats());
    }
}
