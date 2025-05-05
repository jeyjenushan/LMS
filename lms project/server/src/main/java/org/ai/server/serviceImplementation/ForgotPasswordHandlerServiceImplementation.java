package org.ai.server.serviceImplementation;

import lombok.AllArgsConstructor;
import org.ai.server.dto.Response;
import org.ai.server.model.ForgotPasswordTokenEntity;
import org.ai.server.model.UserEntity;
import org.ai.server.otpUtils.GenerateOtp;
import org.ai.server.repository.UserRepository;
import org.ai.server.service.EmailService;
import org.ai.server.service.ForgotPasswordHandlerService;
import org.ai.server.service.ForgotPasswordService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor
public class ForgotPasswordHandlerServiceImplementation implements ForgotPasswordHandlerService {

    private final UserRepository userRepository;
    private final ForgotPasswordService forgotPasswordService;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;




    @Override
    public Response sendForgetPasswordOtp(String email) {
        Response response = new Response();
        try {
            UserEntity user = userRepository.findByEmail(email);
            if (user == null) {
                response.setStatusCode(404);
                response.setMessage("User not found with provided email.");
                return response;
            }
            String otp = GenerateOtp.generateOtp();
            UUID uuid = UUID.randomUUID();
            String id = uuid.toString();
            ForgotPasswordTokenEntity token = forgotPasswordService.findByUser(user.getId());
            if (token == null) {
                token = forgotPasswordService.createToken(user, id, otp, email);
            }
            emailService.sendEmail(email,"Your Forgot Password Verification Code\n\n","Your verification code is " + token.getOtp());


            response.setStatusCode(200);
            response.setMessage("Password reset OTP sent successfully");
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error sending password reset OTP: " + e.getMessage());
        }
        return response;
    }

    @Override
    public Response verifyOtp(String email, String otp) {
        Response response = new Response();
        try {
            UserEntity userAccount = userRepository.findByEmail(email);
            if (userAccount == null) {
                response.setStatusCode(404);
                response.setMessage("User not found with provided email.");
                return response;
            }

            ForgotPasswordTokenEntity forgotPasswordToken = forgotPasswordService.findByUser(userAccount.getId());
            if (forgotPasswordToken == null) {
                response.setStatusCode(404);
                response.setMessage("Invalid or expired token.");
                return response;
            }

            if (forgotPasswordToken.getOtp().equals(otp)) {
                response.setStatusCode(200);
                response.setMessage("OTP verified successfully.");
            } else {
                response.setStatusCode(400);
                response.setMessage("Wrong OTP provided.");
            }
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error verifying OTP: " + e.getMessage());
        }
        return response;
    }

    public Response resetPassword(String email, String newPassword, String otp) {
        Response response = new Response();
        try {
            if (newPassword.length() <= 4) {
                response.setStatusCode(400);
                response.setMessage("Password must be at least 4 characters long.");
                return response;
            }

            boolean isVerified = OtpCheck(email, otp);
            if (isVerified) {
                UserEntity userAccount = userRepository.findByEmail(email);
                if (userAccount == null) {
                    response.setStatusCode(404);
                    response.setMessage("User not found with provided email.");
                    return response;
                }

                updatePassword(userAccount, newPassword);
                response.setStatusCode(200);
                response.setMessage("Password updated successfully!");
            } else {
                response.setStatusCode(400);
                response.setMessage("Wrong OTP provided.");
            }
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error resetting password: " + e.getMessage());
        }
        return response;
    }

    public void updatePassword(UserEntity userAccount, String newPassword) {
        try {
            userAccount.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(userAccount);
        } catch (Exception e) {
            throw new RuntimeException("Error updating password: " + e.getMessage());
        }
    }

    public boolean OtpCheck(String email, String otp) {
        Response response = new Response();
        UserEntity user = userRepository.findByEmail(email);
        if (user == null) {
            response.setStatusCode(404);
            response.setMessage("User not found with provided email.");
        }
        ForgotPasswordTokenEntity forgotPasswordToken = forgotPasswordService.findByUser(user.getId());
        if (forgotPasswordToken == null) {
            response.setStatusCode(404);
            response.setMessage("Invalid or expired token.");
        }
        return forgotPasswordToken.getOtp().equals(otp);
    }
}
