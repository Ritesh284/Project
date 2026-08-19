package com.carrental.dto;

public class DashboardStatsDTO {

    private long totalUsers;
    private long totalCars;
    private long pendingRides;
    private long acceptedRides;
    private long deniedRides;
    private long completedRides;
    private double totalRevenue;

    public DashboardStatsDTO() {
    }

    public DashboardStatsDTO(long totalUsers, long totalCars, long pendingRides,
                             long acceptedRides, long deniedRides, long completedRides, double totalRevenue) {
        this.totalUsers = totalUsers;
        this.totalCars = totalCars;
        this.pendingRides = pendingRides;
        this.acceptedRides = acceptedRides;
        this.deniedRides = deniedRides;
        this.completedRides = completedRides;
        this.totalRevenue = totalRevenue;
    }

    public long getTotalUsers() {
        return totalUsers;
    }

    public void setTotalUsers(long totalUsers) {
        this.totalUsers = totalUsers;
    }

    public long getTotalCars() {
        return totalCars;
    }

    public void setTotalCars(long totalCars) {
        this.totalCars = totalCars;
    }

    public long getPendingRides() {
        return pendingRides;
    }

    public void setPendingRides(long pendingRides) {
        this.pendingRides = pendingRides;
    }

    public long getAcceptedRides() {
        return acceptedRides;
    }

    public void setAcceptedRides(long acceptedRides) {
        this.acceptedRides = acceptedRides;
    }

    public long getDeniedRides() {
        return deniedRides;
    }

    public void setDeniedRides(long deniedRides) {
        this.deniedRides = deniedRides;
    }

    public long getCompletedRides() {
        return completedRides;
    }

    public void setCompletedRides(long completedRides) {
        this.completedRides = completedRides;
    }

    public double getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(double totalRevenue) {
        this.totalRevenue = totalRevenue;
    }
}
