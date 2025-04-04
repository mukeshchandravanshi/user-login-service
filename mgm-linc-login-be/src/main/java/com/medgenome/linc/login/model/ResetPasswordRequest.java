package com.medgenome.linc.login.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ResetPasswordRequest {

    @NotBlank(message = "Email or Phone number is required.")
    private String emailOrPhone;

    @NotBlank(message = "OTP is required.")
    private String otp;

    @NotBlank(message = "New password is required.")
    private String newPassword;

    @NotBlank(message = "Confirm password is required.")
    private String confirmPassword;

}

