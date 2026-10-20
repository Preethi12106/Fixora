package com.example.demo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "complaints")
public class Complaint extends BaseRecord {

    @NotBlank(message = "Complaint title is required")
    @Size(max = 120, message = "Complaint title cannot exceed 120 characters")
    @Column(name = "title", nullable = false)
    private String title;

    @NotBlank(message = "Complaint description is required")
    @Size(min = 5, max = 2000, message = "Description should be between 5 and 2000 characters")
    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    private String description;

    @NotBlank(message = "Complaint category is required")
    @Pattern(
            regexp = "Electrical|Plumbing|Cleanliness|Wi-Fi|Other",
            message = "Select a valid complaint category"
    )
    @Column(name = "category", nullable = false, length = 100)
    private String category;

    @NotBlank(message = "Room number is required")
    @Size(max = 50, message = "Room number cannot exceed 50 characters")
    @Column(name = "room_number", nullable = false)
    private String roomNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ComplaintStatus status = ComplaintStatus.PENDING;

    @PrePersist
    @Override
    protected void onCreate() {
        super.onCreate();
        if (status == null) {
            status = ComplaintStatus.PENDING;
        }
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public ComplaintStatus getStatus() {
        return status;
    }

    public void setStatus(ComplaintStatus status) {
        this.status = status;
    }
}
