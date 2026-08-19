package com.carrental.service;

import com.carrental.dto.CarDTO;
import com.carrental.entity.Car;
import com.carrental.exception.ResourceNotFoundException;
import com.carrental.repository.CarRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CarService {

    private final CarRepository carRepository;

    public CarService(CarRepository carRepository) {
        this.carRepository = carRepository;
    }

    public List<CarDTO> getAllAvailableCars() {
        return carRepository.findByAvailableTrueOrderByCreatedAtDesc()
                .stream()
                .map(CarDTO::new)
                .collect(Collectors.toList());
    }

    public List<CarDTO> getAllCarsForAdmin() {
        return carRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(CarDTO::new)
                .collect(Collectors.toList());
    }

    public CarDTO getCarById(String id) {
        Car car = carRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Car not found with ID: " + id));
        return new CarDTO(car);
    }

    public Car getCarEntityById(String id) {
        return carRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Car not found with ID: " + id));
    }

    public List<CarDTO> searchCars(String query) {
        if (query == null || query.trim().isEmpty()) {
            return getAllAvailableCars();
        }
        List<CarDTO> results = carRepository.searchAvailableCars(query.trim())
                .stream()
                .map(CarDTO::new)
                .collect(Collectors.toList());

        // Always provide available fleet deals so user can browse and rent
        if (results.isEmpty()) {
            return getAllAvailableCars();
        }
        return results;
    }

    public List<CarDTO> getCarsByCategory(String category) {
        if (category == null || category.trim().isEmpty() || "All".equalsIgnoreCase(category)) {
            return getAllAvailableCars();
        }
        return carRepository.findByAvailableTrueAndCategoryIgnoreCaseOrderByCreatedAtDesc(category.trim())
                .stream()
                .map(CarDTO::new)
                .collect(Collectors.toList());
    }

    public CarDTO createCar(CarDTO dto) {
        Car car = new Car();
        mapDtoToEntity(dto, car);
        Car saved = carRepository.save(car);
        return new CarDTO(saved);
    }

    public CarDTO updateCar(String id, CarDTO dto) {
        Car car = carRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Car not found with ID: " + id));

        mapDtoToEntity(dto, car);
        Car updated = carRepository.save(car);
        return new CarDTO(updated);
    }

    public void deleteCar(String id) {
        Car car = carRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Car not found with ID: " + id));
        carRepository.delete(car);
    }

    public CarDTO toggleAvailability(String id) {
        Car car = carRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Car not found with ID: " + id));
        car.setAvailable(!car.isAvailable());
        Car updated = carRepository.save(car);
        return new CarDTO(updated);
    }

    private void mapDtoToEntity(CarDTO dto, Car car) {
        car.setBrand(dto.getBrand().trim());
        car.setModel(dto.getModel().trim());
        car.setCarName(dto.getCarName().trim());
        car.setCategory(dto.getCategory().trim());
        car.setPricePerDay(dto.getPricePerDay());
        if (dto.getFuelType() != null) {
            car.setFuelType(dto.getFuelType().trim());
        }
        if (dto.getTransmission() != null) {
            car.setTransmission(dto.getTransmission().trim());
        }
        if (dto.getSeats() != null) {
            car.setSeats(dto.getSeats());
        }
        if (dto.getImage() != null && !dto.getImage().trim().isEmpty()) {
            car.setImage(dto.getImage().trim());
        }
        car.setDescription(dto.getDescription());
        car.setAvailable(dto.isAvailable());
    }
}
