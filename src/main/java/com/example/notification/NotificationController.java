package com.example.notification;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api")
public class NotificationController {

    @PostMapping("/notifications")
    public ResponseEntity<NotificationResponse> sendNotification(@Valid @RequestBody NotificationRequest request) {
        NotificationResponse response = new NotificationResponse(
                System.currentTimeMillis(),
                request.complaintId(),
                request.message(),
                "SENT"
        );

        return ResponseEntity.ok(response);
    }

    public record NotificationRequest(Long complaintId, String message) {
        public NotificationRequest {
            if (complaintId == null || complaintId <= 0) {
                throw new IllegalArgumentException("Complaint ID is required");
            }
            if (message == null || message.isBlank()) {
                throw new IllegalArgumentException("Notification message is required");
            }
        }
    }

    public record NotificationResponse(Long notificationId, Long complaintId, String message, String status) {
    }
}
