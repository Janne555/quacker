package com.group5.quacker.controllers;

import com.group5.quacker.entities.User;
import com.group5.quacker.repositories.QuackRepository;
import com.group5.quacker.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class UserController {
    @Autowired
    UserRepository userRepository;

    @Autowired
    QuackRepository quackRepository;

    @RequestMapping(value = "/user/{name}", method = RequestMethod.GET)
    public String userPageGet(@PathVariable("name") String name, Model model) {

        User user = userRepository.findByName(name);
        if(user==null) {
            List<User> users = userRepository.findByNameContaining(name);

            model.addAttribute("users", users);
            return "user-search";
        }


        model.addAttribute("user", user);

        if (user.getProfilePhoto() != null) {
            model.addAttribute("profilePhotoUrl", "/files/" + user.getProfilePhoto().getPublicId());
        }

        model.addAttribute("userquacks", quackRepository.findByPoster(user));

        return "userpage";
    }

    @RequestMapping(value = "/follow/{name}", method = RequestMethod.GET)
    public String userFollow(@PathVariable("name") String name, Model model) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        User follower = userRepository.findByName(auth.getName());
        if(follower == null)
            return "redirect:/login";

        User user = userRepository.findByName(name);
        if(user==null) {
            return "redirect:/";
        }

        if(!follower.getFollowing().contains(user) && !user.equals(follower)) {
            follower.addFollowing(user);

            userRepository.save(follower);
        }

        return "redirect:/user/" + name;
    }

    @RequestMapping(value = {"/user/", "/users/", "/user", "/users"}, method = RequestMethod.GET)
    public String userPageGet(Model model) {

            List<User> users = userRepository.findAll();

            model.addAttribute("users", users);
            return "user-search";
    }


}
