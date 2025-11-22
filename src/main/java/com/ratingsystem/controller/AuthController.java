package com.ratingsystem.controller;


import com.ratingsystem.dto.request.AuthenticationRequestDto;
import com.ratingsystem.dto.request.RegisterRequestDto;
import com.ratingsystem.dto.request.ResetPasswordRequestDto;
import com.ratingsystem.dto.response.AuthenticationResponseDto;
import com.ratingsystem.entity.User;
import com.ratingsystem.repository.UserRepository;
import com.ratingsystem.service.in.AuthService;
import com.ratingsystem.service.utilityServices.RedisService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Authentication", description = "User registration, login, confirmation and password reset")
public class AuthController {

    private final AuthService authService;
    private final RedisService redisService;
    private final UserRepository userRepository;

    // registration
    @PostMapping("/register")
    @Operation(summary = "Register new user",
            description = "Creates a new user account and sends an email confirmation code")
    public ResponseEntity<AuthenticationResponseDto> register(@RequestBody RegisterRequestDto request) {
        log.info("Register request for email: {}", request.getEmail());

        try {
            AuthenticationResponseDto response = authService.register(request);
            log.info("User registered successfully: {}", request.getEmail());
            return ResponseEntity.ok(response);

        } catch (Exception ex) {
            log.error("Registration failed for {}: {}", request.getEmail(), ex.getMessage());
            throw ex;
        }
    }

    // registration
    @PostMapping("/registerAdmin")
    @Operation(summary = "Register new user",
            description = "Creates a new user account and sends an email confirmation code")
    public ResponseEntity<AuthenticationResponseDto> registerAdmin(@RequestBody RegisterRequestDto request) {
        log.info("Register request for email: {}", request.getEmail());

        try {
            AuthenticationResponseDto response = authService.registerAdmin(request);
            log.info("User registered successfully: {}", request.getEmail());
            return ResponseEntity.ok(response);

        } catch (Exception ex) {
            log.error("Registration failed for {}: {}", request.getEmail(), ex.getMessage());
            throw ex;
        }
    }

    // login
    @PostMapping("/authenticate")
    @Operation(summary = "Authenticate user (Login)",
            description = "Returns JWT token if login credentials are valid")
    public ResponseEntity<AuthenticationResponseDto> authenticate(
            @RequestBody AuthenticationRequestDto request) {

        log.info("Login request: {}", request.getEmail());

        try {
            AuthenticationResponseDto response = authService.authenticate(request);
            log.info("Login successful: {}", request.getEmail());
            return ResponseEntity.ok(response);

        } catch (Exception ex) {
            log.warn("Login failed for {}: {}", request.getEmail(), ex.getMessage());
            throw ex;
        }
    }

    // confirm email
    @GetMapping("/confirm")
    @Operation(summary = "Confirm email address",
            description = "Validates the confirmation code sent to the user's email")
    public ResponseEntity<String> confirmEmail(
            @RequestParam("email") String email,
            @RequestParam("code") String code) {

        log.info("Email confirmation attempt for {}", email);

        String storedCode = redisService.getConfirmationCode(email);

        if (storedCode == null || !storedCode.equals(code)) {
            log.warn("Invalid confirmation code for {}", email);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid or expired confirmation code");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("User not found during confirmation: {}", email);
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
                });

        user.setEmailConfirmed(true);
        userRepository.save(user);
        redisService.deleteConfirmationCode(email);

        log.info("Email confirmed for {}", email);
        return ResponseEntity.ok("Email confirmed successfully");
    }

    // reset password
    @PostMapping("/forgot_password")
    @Operation(summary = "Request password reset",
            description = "Sends a reset code to the user's email")
    public ResponseEntity<String> forgotPassword(@RequestParam("email") String email) {
        log.info("Password reset requested for {}", email);

        authService.sendPasswordResetCode(email);

        return ResponseEntity.ok("Password reset code sent to your email");
    }

    @PostMapping("/reset")
    @Operation(summary = "Reset password",
            description = "Resets user password using the provided reset code")
    public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordRequestDto request) {

        log.info("Password reset attempt with code {}", request.getCode());

        authService.resetPassword(request.getCode(), request.getNewPassword());

        log.info("Password reset successful for code {}", request.getCode());
        return ResponseEntity.ok("Password reset successfully");
    }

    @GetMapping("/check_code")
    @Operation(summary = "Validate reset code",
            description = "Checks if the reset code is valid or expired")
    public ResponseEntity<String> checkResetCode(@RequestParam("code") String code) {

        log.debug("Checking reset code {}", code);

        boolean isValid = authService.isResetCodeValid(code);

        if (isValid) {
            log.info("Reset code {} is valid", code);
            return ResponseEntity.ok("Reset code is valid");
        }

        log.warn("Reset code {} is invalid or expired", code);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Invalid or expired reset code");
    }
}
