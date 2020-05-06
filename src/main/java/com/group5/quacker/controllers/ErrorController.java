package com.group5.quacker.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * This is the controller for an invalid login attempt.
 */
@Controller
public class ErrorController {
    @RequestMapping(value = "/login-error", method = RequestMethod.GET)
    public String loginErrorGet(final RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("loginError", "Invalid credentials");  // Add error message
        return "redirect:/login";
    }
}
