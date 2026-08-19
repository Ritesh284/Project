package com.carrental.dto;

import com.carrental.entity.User;
import java.time.LocalDateTime;

public class UserDTO {

    private String id;
    private String name;
    private String email;
    private String mobileNumber;
    private String role;
    private LocalDateTime createdAt;

    public UserDTO() {
    }

    public UserDTO(String id, String name, String email, String mobileNumber, String role, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.mobileNumber = mobileNumber;
        this.role = role;
        this.createdAt = createdAt;
    }

    public UserDTO(User user) {
        if (user != null) {
            this.id = user.getId();
            this.name = user.getName();
            this.email = user.getEmail();
            this.mobileNumber = user.getMobileNumber();
            this.role = user.getRole() != null ? user.getRole().name() : null;
            this.createdAt = user.getCreatedAt();
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
