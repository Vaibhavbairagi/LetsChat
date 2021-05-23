package com.vaibhav.letschat.api;

public class UpdateFCMTokenResponse {
    String status;

    public UpdateFCMTokenResponse() {
    }

    public UpdateFCMTokenResponse(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
