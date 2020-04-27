package com.group5.quacker.controllers;

import com.group5.quacker.entities.FileMap;
import com.group5.quacker.entities.Quack;
import com.group5.quacker.entities.User;
import com.group5.quacker.models.CurrentPasswordForm;
import com.group5.quacker.models.PasswordForm;
import com.group5.quacker.models.PersonalInfoForm;
import com.group5.quacker.repositories.QuackRepository;
import com.group5.quacker.repositories.UserRepository;
import com.group5.quacker.services.AccountService;
import com.group5.quacker.services.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.validation.Valid;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Controller for handling the user settings page
 */
@Controller
public class SettingsController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FileService fileService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AccountService accountService;
    /**
     * Includes the class as an attribute
     *
     * @return
     */
    @ModelAttribute("personalInfoForm")
    public PersonalInfoForm newPersonalInfoForm() { return new PersonalInfoForm(); }

    @ModelAttribute("currentPasswordForm")
    public CurrentPasswordForm newMyForm() {
        return new CurrentPasswordForm();
    }

    @ModelAttribute("passwordForm")
    public PasswordForm newpasswordForm() { return new PasswordForm(); }

    /**
     * Returns the specific settings page requested in the URL.
     * Includes attributes required by the page.
     *
     * @param setting
     * @param allParams
     * @param model
     * @return
     */
    @RequestMapping(value = {"/settings/{setting}", "/settings"}, method = RequestMethod.GET)
    public String getSettings(
            @PathVariable(value = "setting", required = false) String setting,
            @RequestParam Map<String, String> allParams,
            Model model, User user) {

        if (user == null) {
            return "redirect:/login";
        }

        if (user.getProfilePhoto() != null) {
            model.addAttribute("profilePhotoUrl", "/files/" + user.getProfilePhoto().getPublicId());
        }
        
        if (user.getProfilePhoto() != null) {
            model.addAttribute("profilePhotoHead", "/files/" + user.getProfilePhoto().getPublicId());
        }

        model.addAttribute("user", user);

        if (setting == null) {
            return "settings";
        }

        switch (setting) {
            case "profile-photo":
                model.addAttribute("setting", "profile-photo");
                break;
            case "personal-info":
                model.addAttribute("setting", "personal-info");
                break;
            case "delete-account":
                model.addAttribute("setting", "delete-account");
                break;
            case "download-data":
                model.addAttribute("setting", "download-data");
                break;
            default:
                break;
        }

        return "settings";
    }


    /**
     * Accepts a new profile photo and sets
     * it as the users current profile photo
     *
     * @param file
     * @return
     * @throws IOException
     */
    @PostMapping("/settings/profile-photo")
    public String postProfilePhoto(@RequestParam("file") MultipartFile file, User user) throws IOException {
        if (user == null) {
            return "redirect:/login";
        }

        FileMap fileMap = fileService.storeFile(file);

        user.setProfilePhoto(fileMap);
        userRepository.save(user);

        return "redirect:/settings/profile-photo";
    }

    /**
     * Accepts a validated personal info form.
     * If the form has errors, returns a redirect and
     * includes the errors as a flash attribute
     *
     * @param personalInfoForm
     * @param bindingResult
     * @param redirectAttributes
     * @return
     */
    @PostMapping("/settings/personal-info/email")
    public String postEmail(
            @Valid PersonalInfoForm personalInfoForm,
            final BindingResult bindingResult,
            final RedirectAttributes redirectAttributes, User user) {
        if (user == null) {
            return "redirect:/login";
        }

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.personalInfoForm", bindingResult);
            redirectAttributes.addFlashAttribute("personalInfoForm", personalInfoForm);
        } else {
            user.setEmail(personalInfoForm.getEmail());
            userRepository.save(user);
        }

        return "redirect:/settings/personal-info";
    }

    @PostMapping("/settings/password-change")
    public String postPassword(@Valid PasswordForm passWordForm, final BindingResult bindingResult, final RedirectAttributes redirectAttributes, User user) {
        if (user == null) {
            return "redirect:/login";
        }

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.passwordForm", bindingResult);
            redirectAttributes.addFlashAttribute("passwordForm", passWordForm);
        }
        else
        {
            user.setPasswordHash(passwordEncoder.encode(passWordForm.getNew_password()));
            userRepository.save(user);
            redirectAttributes.addFlashAttribute("success", "Password changed successfully!");
        }

        return "redirect:/settings/password-change";
    }

    @PostMapping("/settings/delete-account")
    public String postAccountDeletion(
            @Valid CurrentPasswordForm currentPasswordForm,
            final BindingResult bindingResult,
            final RedirectAttributes redirectAttributes, User user) {
        if (user == null) {
            return "redirect:/login";
        }

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.currentPasswordForm", bindingResult);
            redirectAttributes.addFlashAttribute("currentPasswordForm", currentPasswordForm);
        } else {
            accountService.deleteAccount(user);
        }

        return "redirect:/settings/delete-account";
    }
}
