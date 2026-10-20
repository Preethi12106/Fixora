package com.example.demo.service;

import com.example.demo.entity.Complaint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Service
public class NotificationServiceClient {

    private static final Logger log = LoggerFactory.getLogger(NotificationServiceClient.class);

    private final RestClient restClient;

    public NotificationServiceClient(@Value("${notification.service.url}") String baseUrl) {
        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .build();
    }

    public boolean sendNotification(Complaint complaint) {
        try {
            NotificationRequest request = new NotificationRequest(
                    complaint.getId(),
                    "New complaint received for room " + complaint.getRoomNumber() + ": " + complaint.getTitle()
            );

            ResponseEntity<NotificationResponse> response = restClient.post()
                    .uri("/api/notifications")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(request)
                    .retrieve()
                    .toEntity(NotificationResponse.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("Notification sent successfully for complaint ID {}", complaint.getId());
                return true;
            }
            log.warn("Notification service returned unexpected status {} for complaint ID {}",
                    response.getStatusCode(), complaint.getId());
            return false;
        } catch (RestClientException ex) {
            log.warn("Notification service is unavailable. Complaint {} was saved successfully but notification is skipped.", complaint.getId(), ex);
            return false;
        }
    }

    public record NotificationRequest(Long complaintId, String message) {
    }

    public record NotificationResponse(Long notificationId, Long complaintId, String message, String status) {
    }
}
