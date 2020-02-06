package com.group5.quacker.controllers;

import com.group5.quacker.entities.User;
import com.group5.quacker.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {

    @Autowired
    private UserRepository userRepository;


    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String loginGet() {
        return "login";
    }

    @RequestMapping(value = "/register", method = RequestMethod.GET)
    public String registerGet() {
        return "register";
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public String newUser(@RequestParam String username, @RequestParam String email, @RequestParam String password) {

        if(userRepository.findByName(username) != null)
            return "redirect:/register";

        PasswordEncoder enc = new BCryptPasswordEncoder();
        String hash = enc.encode(password);

        System.out.println("New user: \"" + username + "\" password has hash: " + hash);

        User newAcc = new User();
        newAcc.setName(username);
        newAcc.setPasswordHash(hash);
        newAcc.setEmail(email);
        userRepository.save(newAcc);

        return "redirect:/login";
    }
}
