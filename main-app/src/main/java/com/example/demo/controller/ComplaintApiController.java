package com.example.demo.controller;

import com.example.demo.dto.ComplaintRequest;
import com.example.demo.dto.ComplaintStatusRequest;
import com.example.demo.entity.Complaint;
import com.example.demo.entity.ComplaintStatus;
import com.example.demo.service.ComplaintService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Locale;

@RestController
@RequestMapping("/api")
public class ComplaintApiController {

    private final ComplaintService complaintService;

    public ComplaintApiController(ComplaintService complaintService) {
        this.complaintService = complaintService;
    }

    @GetMapping("/complaints")
    public List<Complaint> getAllComplaints() {
        return complaintService.getAllComplaints();
    }

    @GetMapping("/complaints/search/status/{status}")
    public List<Complaint> getComplaintsByStatus(@PathVariable String status) {
        return complaintService.findComplaintsByStatus(parseStatus(status));
    }

    @GetMapping("/complaints/search/category/{category}")
    public List<Complaint> getComplaintsByCategory(@PathVariable String category) {
        return complaintService.findComplaintsByCategory(category);
    }

    @GetMapping("/complaints/{id}")
    public Complaint getComplaintById(@PathVariable Long id) {
        return complaintService.getRequiredComplaint(id);
    }

    @PostMapping("/complaints")
    public ResponseEntity<Complaint> createComplaint(@Valid @RequestBody ComplaintRequest request) {
        Complaint complaint = toComplaint(request);
        Complaint savedComplaint = complaintService.createComplaint(complaint);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedComplaint);
    }

    @PutMapping("/complaints/{id}")
    public Complaint updateComplaint(@PathVariable Long id,
                                     @Valid @RequestBody ComplaintRequest request) {
        return complaintService.updateComplaint(id, toComplaint(request));
    }

    @PutMapping("/complaints/{id}/status")
    public ResponseEntity<Complaint> updateComplaintStatus(@PathVariable Long id,
                                                           @Valid @RequestBody ComplaintStatusRequest request) {
        Complaint updatedComplaint = complaintService.updateStatus(id, parseStatus(request.status()));
        return ResponseEntity.ok(updatedComplaint);
    }

    @DeleteMapping("/complaints/{id}")
    public ResponseEntity<Void> deleteComplaint(@PathVariable Long id) {
        complaintService.deleteComplaint(id);
        return ResponseEntity.noContent().build();
    }

    private Complaint toComplaint(ComplaintRequest request) {
        Complaint complaint = new Complaint();
        complaint.setTitle(request.title());
        complaint.setDescription(request.description());
        complaint.setCategory(request.category());
        complaint.setRoomNumber(request.roomNumber());
        return complaint;
    }

    private ComplaintStatus parseStatus(String status) {
        try {
            return ComplaintStatus.valueOf(status.trim().toUpperCase(Locale.ROOT).replace(' ', '_'));
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Status must be one of: Pending, In Progress, Resolved");
        }
    }
}
