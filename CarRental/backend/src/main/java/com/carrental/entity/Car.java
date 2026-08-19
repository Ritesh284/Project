package com.carrental.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "cars")
public class Car {

    @Id
    private String id;

    private String brand;
    private String model;
    private String carName;
    private String category;
    private Double pricePerDay;
    private String fuelType;
    private String transmission;
    private Integer seats;
    private String image;
    private String description;
    private boolean available = true;
    private LocalDateTime createdAt = LocalDateTime.now();

    public Car() {
    }

    public Car(String brand, String model, String carName, String category, Double pricePerDay,
               String fuelType, String transmission, Integer seats, String image, String description, boolean available) {
        this.brand = brand;
        this.model = model;
        this.carName = carName;
        this.category = category;
        this.pricePerDay = pricePerDay;
        this.fuelType = fuelType;
        this.transmission = transmission;
        this.seats = seats;
        this.image = image;
        this.description = description;
        this.available = available;
        this.createdAt = LocalDateTime.now();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getCarName() {
        return carName;
    }

    public void setCarName(String carName) {
        this.carName = carName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Double getPricePerDay() {
        return pricePerDay;
    }

    public void setPricePerDay(Double pricePerDay) {
        this.pricePerDay = pricePerDay;
    }

    public String getFuelType() {
        return fuelType;
    }

    public void setFuelType(String fuelType) {
        this.fuelType = fuelType;
    }

    public String getTransmission() {
        return transmission;
    }

    public void setTransmission(String transmission) {
        this.transmission = transmission;
    }

    public Integer getSeats() {
        return seats;
    }

    public void setSeats(Integer seats) {
        this.seats = seats;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
