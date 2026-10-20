package com.example.demo.repository;

import com.example.demo.entity.Complaint;
import com.example.demo.entity.ComplaintStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ComplaintRepository extends JpaRepository<Complaint, Long> {

    List<Complaint> findAllByOrderByCreatedAtDesc();

    @Query("select c from Complaint c where c.status = :status order by c.createdAt desc")
    List<Complaint> findByStatusWithHql(@Param("status") ComplaintStatus status);

    @Query("select c from Complaint c where lower(c.category) = lower(:category) order by c.createdAt desc")
    List<Complaint> findByCategoryWithHql(@Param("category") String category);
}
