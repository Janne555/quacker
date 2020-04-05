package com.group5.quacker.controllers;

import com.group5.quacker.models.FeedbackForm;
import com.group5.quacker.repositories.FeedbackRepository;
import com.group5.quacker.services.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

@Controller
public class HelpController {
    @Autowired
    private AccountService accountService;

    @Autowired
    private FeedbackRepository feedbackRepository;

    @ModelAttribute("feedbackForm")
    public FeedbackForm newFeedBackForm() {
        return new FeedbackForm();
    }

    @GetMapping(value = {"/help", "/help/{subpage}"})
    public String getHelpPage(Model model, @PathVariable(required = false) String subpage) {
        model.addAttribute("user", accountService.currentUser());
        model.addAttribute("subpage", subpage != null ? subpage : "default");

        return "help";
    }

    @PostMapping("/help/feedback")
    public String postFeedback(
            @Valid FeedbackForm feedbackForm,
            final RedirectAttributes redirectAttributes) {

        feedbackRepository.save(feedbackForm.getFeedback(accountService.currentUser()));

        redirectAttributes.addFlashAttribute("feedbackSuccess", true);

        return "redirect:/help/feedback";
    }
}
