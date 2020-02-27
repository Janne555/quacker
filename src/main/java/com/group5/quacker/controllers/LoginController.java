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

/**
 * This controller handles login and registering endpoints
 */
@Controller
public class LoginController {

    /**
     * JPA Repository for users
     */
    @Autowired
    private UserRepository userRepository;

    /**
     * Get mapping for /login
     * @return Thymeleaf template for login page
     */
    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String loginGet() {
        return "login";
    }

    /**
     * Get mapping for /register
     * @return Thymeleaf template for register page
     */
    @RequestMapping(value = "/register", method = RequestMethod.GET)
    public String registerGet() {
        return "register";
    }

    /**
     * Post mapping for /register
     * This endpoint is used for creating new accounts
     * @return Redirect to login page
     */
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public String newUser(@RequestParam String username, @RequestParam String email, @RequestParam String password) {

        if(userRepository.findByName(username) != null)     // If the user already exists then redirect
            return "redirect:/register";

        PasswordEncoder enc = new BCryptPasswordEncoder();
        String hash = enc.encode(password);                 // Hash the password

        System.out.println("New user: \"" + username + "\" password has hash: " + hash);    // Debugging

        User newAcc = new User();       // create new user object
        newAcc.setName(username);       // set the username
        newAcc.setPasswordHash(hash);   // set the passwordhash
        newAcc.setEmail(email);         // set the email
        userRepository.save(newAcc);    // save the new user object

        return "redirect:/login";
    }
}
