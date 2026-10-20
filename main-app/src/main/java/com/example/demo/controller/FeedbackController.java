package com.example.demo.controller;

import com.example.demo.entity.Feedback;
import com.example.demo.service.FeedbackService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class FeedbackController {

    private static final Logger log = LoggerFactory.getLogger(FeedbackController.class);

    private final FeedbackService feedbackService;

    public FeedbackController(FeedbackService feedbackService) {
        this.feedbackService = feedbackService;
    }

    @GetMapping("/feedback")
    public String showFeedbackForm(Model model) {
        model.addAttribute("feedback", new Feedback());
        return "feedback-form";
    }

    @PostMapping("/feedback")
    public String saveFeedback(@Valid @ModelAttribute("feedback") Feedback feedback,
                              BindingResult bindingResult,
                              RedirectAttributes redirectAttributes,
                              Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("feedback", feedback);
            return "feedback-form";
        }

        try {
            feedbackService.saveFeedback(feedback);
            redirectAttributes.addFlashAttribute("successMessage", "Thank you! Your feedback was saved.");
            return "redirect:/feedback";
        } catch (DataAccessException ex) {
            log.error("Could not save submitted feedback", ex);
            model.addAttribute("errorMessage", "We could not save your feedback. Please try again.");
            model.addAttribute("feedback", feedback);
            return "feedback-form";
        }
    }
}
