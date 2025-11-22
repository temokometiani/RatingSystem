package com.ratingsystem.service.impl;

import com.ratingsystem.dto.request.AuthenticationRequestDto;
import com.ratingsystem.dto.request.RegisterRequestDto;
import com.ratingsystem.dto.response.AuthenticationResponseDto;
import com.ratingsystem.entity.User;
import com.ratingsystem.enums.Role;
import com.ratingsystem.repository.UserRepository;
import com.ratingsystem.service.in.AuthService;

import com.ratingsystem.service.utilityServices.JwtService;
import com.ratingsystem.service.utilityServices.RedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final RedisService redisService;

    // registration
    @Override
    public AuthenticationResponseDto register(RegisterRequestDto request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email already in use");
        }

        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.SELLER)
                .emailConfirmed(false)
                .approved(false)
                .build();

        userRepository.save(user);

        String code = UUID.randomUUID().toString();
        redisService.saveConfirmationCode(request.getEmail(), code);

        log.info("EMAIL CONFIRM LINK: http://localhost:8081/api/auth/confirm?email={}&code={}",
                request.getEmail(), code);

        return AuthenticationResponseDto.builder()
                .token(jwtService.generateToken(user))
                .build();
    }

    // login
    @Override
    public AuthenticationResponseDto authenticate(AuthenticationRequestDto request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        if (!user.isEmailConfirmed()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Confirm email first");
        }

        if (user.getRole() == Role.SELLER && !user.isApproved()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Seller not approved");
        }

        return AuthenticationResponseDto.builder()
                .token(jwtService.generateToken(user))
                .build();
    }

    // reset code sender
    @Override
    public void sendPasswordResetCode(String email) {
        userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        String code = UUID.randomUUID().toString();
        redisService.saveConfirmationCode(email, code);

        log.info("RESET PASSWORD LINK: http://localhost:8081/api/auth/reset?code={}", code);
    }

    // reset password
    @Override
    public void resetPassword(String code, String newPassword) {

        String email = redisService.getEmailByResetCode(code);
        if (email == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid or expired code");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        redisService.deleteConfirmationCode(email);
    }

    // code checker
    @Override
    public boolean isResetCodeValid(String code) {
        return redisService.getEmailByResetCode(code) != null;
    }

    @Override
    public AuthenticationResponseDto registerAdmin(RegisterRequestDto request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email already in use");
        }

        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.ADMIN)
                .emailConfirmed(true)
                .approved(true)
                .build();

        userRepository.save(user);

        String code = UUID.randomUUID().toString();
        redisService.saveConfirmationCode(request.getEmail(), code);

        log.info("EMAIL CONFIRM LINK: http://localhost:8081/api/auth/confirm?email={}&code={}",
                request.getEmail(), code);

        return AuthenticationResponseDto.builder()
                .token(jwtService.generateToken(user))
                .build();
    }

}
