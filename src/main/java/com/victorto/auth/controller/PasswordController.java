package com.victorto.auth.controller;

// package com.example.auth.controller;

import com.victorto.auth.dto.ForgotPasswordRequest;
import com.victorto.auth.dto.ResetPasswordRequest;
import com.victorto.auth.exception.TokenExpiredException;
import com.victorto.auth.exception.TokenNotFoundException;
import com.victorto.auth.exception.UserNotFoundException;
import com.victorto.auth.model.PasswordResetToken;
import com.victorto.auth.model.User;
import com.victorto.auth.repository.PasswordResetTokenRepository;
import com.victorto.auth.repository.UserRepository;
import com.victorto.auth.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/password")
@RequiredArgsConstructor
public class PasswordController {
    private final UserRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/forgot")
    public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + request.getEmail()));

        String token = UUID.randomUUID().toString();
        PasswordResetToken resetToken = new PasswordResetToken(token, user);
        tokenRepository.save(resetToken);

        emailService.sendPasswordResetEmail(user.getEmail(), token);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/reset")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest request) {
        PasswordResetToken resetToken = tokenRepository.findByToken(request.getToken())
                .orElseThrow(() -> new TokenNotFoundException("Invalid token"));

        if (resetToken.isExpired()) {
            throw new TokenExpiredException("Token has expired");
        }

        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        // Delete the used token
        tokenRepository.delete(resetToken);

        return ResponseEntity.ok().build();
    }
}
