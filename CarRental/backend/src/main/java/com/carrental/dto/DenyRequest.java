package com.carrental.dto;

public class DenyRequest {

    private String adminNote;

    public DenyRequest() {
    }

    public DenyRequest(String adminNote) {
        this.adminNote = adminNote;
    }

    public String getAdminNote() {
        return adminNote;
    }

    public void setAdminNote(String adminNote) {
        this.adminNote = adminNote;
    }
}
