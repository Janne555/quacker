package com.group5.quacker.controllers;

import com.group5.quacker.entities.FileMap;
import com.group5.quacker.entities.User;
import com.group5.quacker.repositories.UserRepository;
import com.group5.quacker.services.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;

@Controller
public class SettingsController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FileService fileService;

    @RequestMapping(value = {"/settings/{setting}", "/settings"}, method = RequestMethod.GET)
    public String getSettings(@PathVariable(value = "setting", required = false) String setting, Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        User user = userRepository.findByName(auth.getName());
        if (user == null) {
            return "redirect:/login";
        }

        if (user.getProfilePhoto() != null) {
            model.addAttribute("profilePhotoUrl", "/files/" + user.getProfilePhoto().getPublicId());
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
}
