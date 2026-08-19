package com.carrental.dto;

import com.carrental.entity.Booking;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class BookingResponse {

    private String id;
    private String userId;
    private String userName;
    private String userEmail;
    private String userMobile;

    private String carId;
    private String carBrand;
    private String carModel;
    private String carName;
    private String carImage;
    private Double pricePerDay;

    private String pickupState;
    private String pickupCity;
    private String pickupLocation;

    private String dropState;
    private String dropCity;
    private String dropLocation;

    private LocalDate pickupDate;
    private LocalDate returnDate;
    private Long totalDays;
    private Double totalAmount;

    private String bookingStatus;
    private String paymentStatus;
    private String adminNote;
    private LocalDateTime createdAt;

    public BookingResponse() {
    }

    public BookingResponse(Booking booking) {
        if (booking != null) {
            this.id = booking.getId();
            this.userId = booking.getUserId();
            this.userName = booking.getUserName();
            this.userEmail = booking.getUserEmail();
            this.userMobile = booking.getUserMobile();

            this.carId = booking.getCarId();
            this.carBrand = booking.getCarBrand();
            this.carModel = booking.getCarModel();
            this.carName = booking.getCarName();
            this.carImage = booking.getCarImage();
            this.pricePerDay = booking.getPricePerDay();

            this.pickupState = booking.getPickupState();
            this.pickupCity = booking.getPickupCity();
            this.pickupLocation = booking.getPickupLocation();

            this.dropState = booking.getDropState();
            this.dropCity = booking.getDropCity();
            this.dropLocation = booking.getDropLocation();

            this.pickupDate = booking.getPickupDate();
            this.returnDate = booking.getReturnDate();
            this.totalDays = booking.getTotalDays();
            this.totalAmount = booking.getTotalAmount();

            this.bookingStatus = booking.getBookingStatus() != null ? booking.getBookingStatus().name() : null;
            this.paymentStatus = booking.getPaymentStatus() != null ? booking.getPaymentStatus().name() : null;
            this.adminNote = booking.getAdminNote();
            this.createdAt = booking.getCreatedAt();
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserMobile() {
        return userMobile;
    }

    public void setUserMobile(String userMobile) {
        this.userMobile = userMobile;
    }

    public String getCarId() {
        return carId;
    }

    public void setCarId(String carId) {
        this.carId = carId;
    }

    public String getCarBrand() {
        return carBrand;
    }

    public void setCarBrand(String carBrand) {
        this.carBrand = carBrand;
    }

    public String getCarModel() {
        return carModel;
    }

    public void setCarModel(String carModel) {
        this.carModel = carModel;
    }

    public String getCarName() {
        return carName;
    }

    public void setCarName(String carName) {
        this.carName = carName;
    }

    public String getCarImage() {
        return carImage;
    }

    public void setCarImage(String carImage) {
        this.carImage = carImage;
    }

    public Double getPricePerDay() {
        return pricePerDay;
    }

    public void setPricePerDay(Double pricePerDay) {
        this.pricePerDay = pricePerDay;
    }

    public String getPickupState() {
        return pickupState;
    }

    public void setPickupState(String pickupState) {
        this.pickupState = pickupState;
    }

    public String getPickupCity() {
        return pickupCity;
    }

    public void setPickupCity(String pickupCity) {
        this.pickupCity = pickupCity;
    }

    public String getPickupLocation() {
        return pickupLocation;
    }

    public void setPickupLocation(String pickupLocation) {
        this.pickupLocation = pickupLocation;
    }

    public String getDropState() {
        return dropState;
    }

    public void setDropState(String dropState) {
        this.dropState = dropState;
    }

    public String getDropCity() {
        return dropCity;
    }

    public void setDropCity(String dropCity) {
        this.dropCity = dropCity;
    }

    public String getDropLocation() {
        return dropLocation;
    }

    public void setDropLocation(String dropLocation) {
        this.dropLocation = dropLocation;
    }

    public LocalDate getPickupDate() {
        return pickupDate;
    }

    public void setPickupDate(LocalDate pickupDate) {
        this.pickupDate = pickupDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }

    public Long getTotalDays() {
        return totalDays;
    }

    public void setTotalDays(Long totalDays) {
        this.totalDays = totalDays;
    }

    public Double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getBookingStatus() {
        return bookingStatus;
    }

    public void setBookingStatus(String bookingStatus) {
        this.bookingStatus = bookingStatus;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getAdminNote() {
        return adminNote;
    }

    public void setAdminNote(String adminNote) {
        this.adminNote = adminNote;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
