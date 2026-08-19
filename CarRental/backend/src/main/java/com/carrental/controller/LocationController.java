package com.carrental.controller;

import com.carrental.dto.LocationDTO;
import com.carrental.service.LocationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/locations")
public class LocationController {

    private final LocationService locationService;

    public LocationController(LocationService locationService) {
        this.locationService = locationService;
    }

    @GetMapping("/states")
    public ResponseEntity<List<LocationDTO.StateResponse>> getAllStates() {
        return ResponseEntity.ok(locationService.getAllStates());
    }

    @GetMapping("/cities/{stateId}")
    public ResponseEntity<List<LocationDTO.CityResponse>> getCitiesByState(@PathVariable String stateId) {
        return ResponseEntity.ok(locationService.getCitiesByState(stateId));
    }

    @GetMapping("/{cityId}")
    public ResponseEntity<List<LocationDTO.LocationResponse>> getLocationsByCity(@PathVariable String cityId) {
        return ResponseEntity.ok(locationService.getLocationsByCity(cityId));
    }

    @GetMapping("/search")
    public ResponseEntity<List<LocationDTO.LocationResponse>> searchLocations(@RequestParam(required = false) String q) {
        return ResponseEntity.ok(locationService.searchLocations(q));
    }
}
