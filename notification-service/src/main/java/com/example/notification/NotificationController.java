package com.example.notification;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class NotificationController {

    @PostMapping("/notifications")
    public ResponseEntity<NotificationResponse> sendNotification(@Valid @RequestBody NotificationRequest request) {
        NotificationResponse response = new NotificationResponse(
                request.complaintId(),
                request.message(),
                "ACCEPTED"
        );

        return ResponseEntity.ok(response);
    }

    public record NotificationRequest(
            @NotNull @Positive Long complaintId,
            @NotBlank String message
    ) {
    }

    public record NotificationResponse(Long complaintId, String message, String status) {
    }
}
