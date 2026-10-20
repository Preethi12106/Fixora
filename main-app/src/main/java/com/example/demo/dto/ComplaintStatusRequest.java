package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;

public record ComplaintStatusRequest(
        @NotBlank(message = "Status is required")
        String status
) {
}
