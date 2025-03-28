package com.medgenome.linc.login.service;

import com.medgenome.linc.login.config.OtpUtil;
import com.medgenome.linc.login.model.User;
import com.medgenome.linc.login.service.EmailService;
import com.medgenome.linc.login.service.SmsService;
import com.medgenome.linc.login.service.UserService;
import com.medgenome.linc.login.util.validator.EmailAndPhoneValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SendOtpService {

    private final UserService userService;
    private final OtpUtil otpUtil;
    private final EmailService emailService;
    private final SmsService smsService;

    public Map<String, String> handleOtpSending(User request) {
        String email = request.getEmail();
        String phoneNum = request.getPhoneNum();
        String emailOrPhone = email != null ? email : phoneNum;

        if (emailOrPhone == null || emailOrPhone.isBlank()) {
            throw new RuntimeException("Email or phone number is required.");
        }

        // Validate Email or Phone number format
        EmailAndPhoneValidator.validateEmailAndPhone(email, phoneNum);

        // Check if user exists
        Optional<User> userOpt = userService.findByUserName(emailOrPhone);
        if (userOpt.isEmpty()) {
            throw new RuntimeException(email != null ? "Email not registered." : "Phone number not registered.");
        }

        // Generate OTP
        String otp = otpUtil.generateOtp(emailOrPhone);
        String subjectMessage = "Login OTP";
        String message = "You have requested a login OTP. Use the OTP below to proceed with login: " + otp;

        boolean emailSent = false;
        boolean smsSent = false;

        try {
            if (email != null) {
                emailService.sendEmail(email, subjectMessage, message);
                emailSent = true;
            }
            if (phoneNum != null) {
                smsService.sendSms(phoneNum, message);
                smsSent = true;
            }
        } catch (Exception e) {
            System.err.println("OtpService: Failed to send OTP: " + e.getMessage());
        }

        return generateOtpResponse(emailSent, smsSent);
    }

    private Map<String, String> generateOtpResponse(boolean emailSent, boolean smsSent) {
        if (!emailSent && !smsSent) {
            throw new RuntimeException("Failed to send OTP via both Email and SMS.");
        } else if (!emailSent) {
            return Map.of("message", "OTP sent successfully via SMS. Failed to send via Email.");
        } else if (!smsSent) {
            return Map.of("message", "OTP sent successfully via Email. Failed to send via SMS.");
        }
        return Map.of("message", "OTP sent successfully via Email and SMS!");
    }
}
