package com.group5.quacker.controllers;

import com.group5.quacker.entities.FileMap;
import com.group5.quacker.entities.User;
import com.group5.quacker.models.PersonalInfoForm;
import com.group5.quacker.repositories.UserRepository;
import com.group5.quacker.services.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.io.IOException;
import java.util.Map;

@Controller
public class SettingsController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FileService fileService;


    @ModelAttribute("personalInfoForm")
    public PersonalInfoForm newMyForm() {
        return new PersonalInfoForm();
    }

    @RequestMapping(value = {"/settings/{setting}", "/settings"}, method = RequestMethod.GET)
    public String getSettings(
            @PathVariable(value = "setting", required = false) String setting,
            @RequestParam Map<String, String> allParams,
            Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        User user = userRepository.findByName(auth.getName());
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
            default:
                break;
        }

        return "settings";
    }
    
    


    @PostMapping("/settings/profile-photo")
    public String postProfilePhoto(@RequestParam("file") MultipartFile file) throws IOException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        User user = userRepository.findByName(auth.getName());
        if (user == null) {
            return "redirect:/login";
        }

        FileMap fileMap = fileService.storeFile(file);

        user.setProfilePhoto(fileMap);
        userRepository.save(user);

        return "redirect:/settings/profile-photo";
    }

    @PostMapping("/settings/personal-info/email")
    public String postEmail(
            @Valid PersonalInfoForm personalInfoForm,
            final BindingResult bindingResult,
            final RedirectAttributes redirectAttributes) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        User user = userRepository.findByName(auth.getName());
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
}
