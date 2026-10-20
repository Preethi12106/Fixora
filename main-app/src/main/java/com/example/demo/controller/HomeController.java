package com.example.demo.controller;

import com.example.demo.entity.Complaint;
import com.example.demo.entity.ComplaintStatus;
import com.example.demo.service.ComplaintNotFoundException;
import com.example.demo.service.ComplaintService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Locale;

@Controller
public class HomeController {

    private static final Logger log = LoggerFactory.getLogger(HomeController.class);

    private final ComplaintService complaintService;

    public HomeController(ComplaintService complaintService) {
        this.complaintService = complaintService;
    }

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/complaints/new")
    public String showComplaintForm(Model model) {
        model.addAttribute("complaint", new Complaint());
        return "complaint-form";
    }

    @PostMapping("/complaints")
    public String submitComplaint(@Valid @ModelAttribute("complaint") Complaint complaint,
                                  BindingResult bindingResult,
                                  Model model,
                                  RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("complaint", complaint);
            return "complaint-form";
        }

        try {
            complaintService.createComplaint(complaint);
            redirectAttributes.addFlashAttribute("successMessage", "Complaint submitted successfully.");
            return "redirect:/complaints";
        } catch (DataAccessException ex) {
            log.error("Could not save submitted complaint", ex);
            model.addAttribute("errorMessage", "We could not save your complaint. Please try again.");
            model.addAttribute("complaint", complaint);
            return "complaint-form";
        }
    }

    @GetMapping("/complaints")
    public String showComplaintList(Model model) {
        model.addAttribute("complaints", complaintService.getAllComplaints());
        return "complaints";
    }

    @GetMapping({"/warden", "/admin"})
    public String showWardenDashboard(Model model) {
        model.addAttribute("complaints", complaintService.getAllComplaints());
        return "admin-dashboard";
    }

    @GetMapping("/complaints/{id}")
    public String showComplaintDetails(@PathVariable Long id, Model model) {
        model.addAttribute("complaint", complaintService.getRequiredComplaint(id));
        return "complaint-details";
    }

    @PostMapping("/warden/complaints/{id}/status")
    public String updateComplaintStatus(@PathVariable Long id,
                                       @RequestParam("status") String status,
                                       RedirectAttributes redirectAttributes) {
        try {
            ComplaintStatus complaintStatus = ComplaintStatus.valueOf(
                    status.trim().toUpperCase(Locale.ROOT).replace(' ', '_'));
            complaintService.updateStatus(id, complaintStatus);
            redirectAttributes.addFlashAttribute("successMessage", "Complaint status updated successfully.");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Invalid complaint status.");
        } catch (ComplaintNotFoundException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/warden";
    }

    @PostMapping("/warden/complaints/{id}/delete")
    public String deleteComplaint(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            complaintService.deleteComplaint(id);
            redirectAttributes.addFlashAttribute("successMessage", "Complaint deleted successfully.");
        } catch (ComplaintNotFoundException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/warden";
    }
}
