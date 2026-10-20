package com.example.demo.service;

import com.example.demo.entity.Complaint;
import com.example.demo.entity.ComplaintStatus;
import com.example.demo.repository.ComplaintRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ComplaintServiceTest {

    @Mock
    private ComplaintRepository complaintRepository;

    @Mock
    private NotificationServiceClient notificationServiceClient;

    @InjectMocks
    private ComplaintService complaintService;

    @Test
    void createComplaintSetsServerManagedFieldsAndNotifiesAfterSaving() {
        Complaint complaint = new Complaint();
        complaint.setId(42L);
        complaint.setCreatedAt(LocalDateTime.parse("2020-01-01T00:00:00"));
        complaint.setTitle("Fan issue");
        complaint.setDescription("The fan is not working");
        complaint.setCategory(" Electrical ");
        complaint.setRoomNumber("204");
        complaint.setStatus(ComplaintStatus.RESOLVED);

        when(complaintRepository.save(any(Complaint.class))).thenAnswer(invocation -> {
            Complaint saved = invocation.getArgument(0);
            saved.setId(7L);
            saved.setCreatedAt(LocalDateTime.now());
            return saved;
        });

        Complaint saved = complaintService.createComplaint(complaint);

        assertEquals(7L, saved.getId());
        assertEquals(ComplaintStatus.PENDING, saved.getStatus());
        assertEquals("Electrical", saved.getCategory());
        assertNotNull(saved.getCreatedAt());

        InOrder order = inOrder(complaintRepository, notificationServiceClient);
        order.verify(complaintRepository).save(complaint);
        order.verify(notificationServiceClient).sendNotification(complaint);
    }
}
