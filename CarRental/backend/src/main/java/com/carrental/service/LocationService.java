package com.carrental.service;

import com.carrental.dto.LocationDTO;
import com.carrental.repository.CityRepository;
import com.carrental.repository.LocationRepository;
import com.carrental.repository.StateRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LocationService {

    private final StateRepository stateRepository;
    private final CityRepository cityRepository;
    private final LocationRepository locationRepository;

    public LocationService(StateRepository stateRepository,
                           CityRepository cityRepository,
                           LocationRepository locationRepository) {
        this.stateRepository = stateRepository;
        this.cityRepository = cityRepository;
        this.locationRepository = locationRepository;
    }

    public List<LocationDTO.StateResponse> getAllStates() {
        return stateRepository.findAllByOrderByNameAsc()
                .stream()
                .map(LocationDTO.StateResponse::new)
                .collect(Collectors.toList());
    }

    public List<LocationDTO.CityResponse> getCitiesByState(String stateId) {
        return cityRepository.findByStateIdOrderByNameAsc(stateId)
                .stream()
                .map(LocationDTO.CityResponse::new)
                .collect(Collectors.toList());
    }

    public List<LocationDTO.LocationResponse> getLocationsByCity(String cityId) {
        return locationRepository.findByCityIdOrderByNameAsc(cityId)
                .stream()
                .map(LocationDTO.LocationResponse::new)
                .collect(Collectors.toList());
    }

    public List<LocationDTO.LocationResponse> searchLocations(String query) {
        if (query == null || query.trim().isEmpty()) {
            return locationRepository.findAll()
                    .stream()
                    .map(LocationDTO.LocationResponse::new)
                    .collect(Collectors.toList());
        }
        return locationRepository.searchLocations(query.trim())
                    .stream()
                    .map(LocationDTO.LocationResponse::new)
                    .collect(Collectors.toList());
    }
}
