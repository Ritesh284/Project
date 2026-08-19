package com.carrental.controller;

import com.carrental.dto.CarDTO;
import com.carrental.service.CarService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cars")
public class CarController {

    private final CarService carService;

    public CarController(CarService carService) {
        this.carService = carService;
    }

    @GetMapping
    public ResponseEntity<List<CarDTO>> getAllAvailableCars(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String category
    ) {
        if (search != null && !search.trim().isEmpty()) {
            return ResponseEntity.ok(carService.searchCars(search));
        }
        if (category != null && !category.trim().isEmpty()) {
            return ResponseEntity.ok(carService.getCarsByCategory(category));
        }
        return ResponseEntity.ok(carService.getAllAvailableCars());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CarDTO> getCarById(@PathVariable String id) {
        return ResponseEntity.ok(carService.getCarById(id));
    }
}
