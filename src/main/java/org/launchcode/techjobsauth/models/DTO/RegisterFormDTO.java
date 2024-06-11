package org.launchcode.techjobsauth.models.DTO;

import jakarta.validation.constraints.NotBlank;

public class RegisterFormDTO extends LoginFormDTO {

    @NotBlank
    private String verifyPassword;

    // Getters and Setters
    public String getVerifyPassword() {
        return verifyPassword;
    }

    public void setVerifyPassword(String verifyPassword) {
        this.verifyPassword = verifyPassword;
    }
}