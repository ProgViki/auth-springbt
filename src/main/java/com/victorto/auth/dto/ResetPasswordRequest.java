package com.victorto.auth.dto;

// package com.example.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ResetPasswordRequest {
    @NotBlank
    private String token;
    
    @NotBlank
    private String newPassword;
}
