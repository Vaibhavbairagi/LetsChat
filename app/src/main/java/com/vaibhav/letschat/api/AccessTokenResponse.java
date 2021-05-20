package com.vaibhav.letschat.api;

public class AccessTokenResponse {
    String token;
    String tokenCreationTime;

    public String getTokenCreationTime() {
        return tokenCreationTime;
    }

    public void setTokenCreationTime(String tokenCreationTime) {
        this.tokenCreationTime = tokenCreationTime;
    }

    public AccessTokenResponse(String token, String tokenCreationTime) {
        this.token = token;
        this.tokenCreationTime = tokenCreationTime;
    }

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
