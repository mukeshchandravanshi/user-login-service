package com.medgenome.linc.login.model;

import lombok.Data;

@Data
public class OtpVerificationRequest {
    private String emailOrPhone;
    private String otp;
}
