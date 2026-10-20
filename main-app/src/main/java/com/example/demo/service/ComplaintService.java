package com.example.demo.service;

import com.example.demo.entity.Complaint;
import com.example.demo.entity.ComplaintStatus;
import com.example.demo.repository.ComplaintRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ComplaintService {

    private final ComplaintRepository complaintRepository;
    private final NotificationServiceClient notificationServiceClient;

    public ComplaintService(ComplaintRepository complaintRepository,
                           NotificationServiceClient notificationServiceClient) {
        this.complaintRepository = complaintRepository;
        this.notificationServiceClient = notificationServiceClient;
    }

    public Complaint createComplaint(Complaint complaint) {
        if (complaint == null) {
            throw new IllegalArgumentException("Complaint cannot be null");
        }

        complaint.setId(null);
        complaint.setCreatedAt(null);
        complaint.setStatus(ComplaintStatus.PENDING);
        complaint.setCategory(normalizeCategory(complaint.getCategory()));

        Complaint savedComplaint = complaintRepository.save(complaint);
        notificationServiceClient.sendNotification(savedComplaint);
        return savedComplaint;
    }

    @Transactional(readOnly = true)
    public List<Complaint> getAllComplaints() {
        return complaintRepository.findAllByOrderByCreatedAtDesc();
    }

    @Transactional(readOnly = true)
    public Optional<Complaint> getComplaintById(Long id) {
        return complaintRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Complaint getRequiredComplaint(Long id) {
        return complaintRepository.findById(id)
                .orElseThrow(() -> new ComplaintNotFoundException(id));
    }

    @Transactional(readOnly = true)
    public List<Complaint> findComplaintsByStatus(ComplaintStatus status) {
        return complaintRepository.findByStatusWithHql(status);
    }

    @Transactional(readOnly = true)
    public List<Complaint> findComplaintsByCategory(String category) {
        return complaintRepository.findByCategoryWithHql(category);
    }

    @Transactional
    public Complaint updateStatus(Long id, ComplaintStatus status) {
        Complaint complaint = getRequiredComplaint(id);
        complaint.setStatus(status);
        return complaintRepository.save(complaint);
    }

    @Transactional
    public Complaint updateComplaint(Long id, Complaint updatedDetails) {
        Complaint complaint = getRequiredComplaint(id);
        complaint.setTitle(updatedDetails.getTitle());
        complaint.setDescription(updatedDetails.getDescription());
        complaint.setCategory(normalizeCategory(updatedDetails.getCategory()));
        complaint.setRoomNumber(updatedDetails.getRoomNumber());
        return complaintRepository.save(complaint);
    }

    @Transactional
    public void deleteComplaint(Long id) {
        if (!complaintRepository.existsById(id)) {
            throw new ComplaintNotFoundException(id);
        }
        complaintRepository.deleteById(id);
    }

    private String normalizeCategory(String category) {
        if (category == null) {
            return null;
        }
        return category.trim();
    }
}
