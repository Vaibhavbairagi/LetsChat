package com.vaibhav.letschat.api;

public class AccessTokenResponse {
    String token;

    public AccessTokenResponse() {
    }

    public AccessTokenResponse(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
