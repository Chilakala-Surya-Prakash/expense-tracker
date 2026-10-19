package com.example.expensetracker.dto;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public class AuthDto {

    //sent by user when registering a new account
    public record RegisterRequest(
        @NotBlank(message = "Full name is required") String fullName,
        @NotBlank @Email(message = "Valid email is required") String email,
        @NotBlank @Size(min = 6, message = "Password must be at least 6 characters") String password,
        @Positive(message = "Monthly budget must be positive") BigDecimal monthlyBudget
    ){}
    
    //sent by user when logging in
    public record LoginRequest(
        @NotBlank @Email String email,
        @NotBlank String password
    ){}

    // sent back by our server with the signed JWT token
    public record AuthResponse(
        String token,
        String fullName,
        String email,
        BigDecimal monthlyBudget
    ){}

}