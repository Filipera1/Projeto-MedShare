package org.example.dto;

import jakarta.validation.constraints.NotBlank;

public class GoogleTokenRequest {

    @NotBlank
    private String token;

    public GoogleTokenRequest() {}

    public GoogleTokenRequest(String token) {
        this.token = token;
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
}