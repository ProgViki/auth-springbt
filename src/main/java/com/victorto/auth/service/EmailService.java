package com.victorto.auth.service;

// package com.example.auth.service;

public interface EmailService {
    void sendPasswordResetEmail(String to, String token);
}