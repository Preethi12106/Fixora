package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ComplaintRequest(
        @NotBlank(message = "Complaint title is required")
        @Size(max = 120, message = "Complaint title cannot exceed 120 characters")
        String title,

        @NotBlank(message = "Complaint description is required")
        @Size(min = 5, max = 2000, message = "Description must be between 5 and 2000 characters")
        String description,

        @NotBlank(message = "Complaint category is required")
        @Pattern(regexp = "Electrical|Plumbing|Cleanliness|Wi-Fi|Other",
                message = "Select a valid complaint category")
        String category,

        @NotBlank(message = "Room number is required")
        @Size(max = 50, message = "Room number cannot exceed 50 characters")
        String roomNumber
) {
}
