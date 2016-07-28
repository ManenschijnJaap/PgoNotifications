package com.moonshine.pokemongonotifications.model;

/**
 * Created by jaapmanenschijn on 28/07/16.
 */
public class LoginResponse {
    private String loginUrl;
    private String authToken;
    private String refreshToken;

    public String getLoginUrl() {
        return loginUrl;
    }

    public void setLoginUrl(String loginUrl) {
        this.loginUrl = loginUrl;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

}
