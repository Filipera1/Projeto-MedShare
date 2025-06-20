package org.example.dto;

import jakarta.validation.constraints.NotBlank;

public class GoogleSignupRequest {

    @NotBlank
    private String token;

    private Boolean newsletter = false;

    public GoogleSignupRequest() {}

    public GoogleSignupRequest(String token, Boolean newsletter) {
        this.token = token;
        this.newsletter = newsletter;
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public Boolean getNewsletter() { return newsletter; }
    public void setNewsletter(Boolean newsletter) { this.newsletter = newsletter; }
}