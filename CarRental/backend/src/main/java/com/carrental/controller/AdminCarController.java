package com.carrental.controller;

import com.carrental.dto.CarDTO;
import com.carrental.service.CarService;
import com.carrental.service.FileStorageService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/cars")
public class AdminCarController {

    private final CarService carService;
    private final FileStorageService fileStorageService;

    public AdminCarController(CarService carService, FileStorageService fileStorageService) {
        this.carService = carService;
        this.fileStorageService = fileStorageService;
    }

    @GetMapping
    public ResponseEntity<List<CarDTO>> getAllCarsForAdmin() {
        return ResponseEntity.ok(carService.getAllCarsForAdmin());
    }

    @PostMapping
    public ResponseEntity<CarDTO> createCar(@Valid @RequestBody CarDTO carDTO) {
        CarDTO created = carService.createCar(carDTO);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CarDTO> updateCar(@PathVariable String id, @Valid @RequestBody CarDTO carDTO) {
        CarDTO updated = carService.updateCar(id, carDTO);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteCar(@PathVariable String id) {
        carService.deleteCar(id);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Car successfully deleted from system");
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/toggle-availability")
    public ResponseEntity<CarDTO> toggleAvailability(@PathVariable String id) {
        CarDTO updated = carService.toggleAvailability(id);
        return ResponseEntity.ok(updated);
    }

    @PostMapping(value = "/upload-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, String>> uploadCarImage(@RequestParam("file") MultipartFile file) {
        String fileUrl = fileStorageService.storeFile(file);
        Map<String, String> response = new HashMap<>();
        response.put("imageUrl", fileUrl);
        response.put("message", "Image uploaded successfully");
        return ResponseEntity.ok(response);
    }
}
