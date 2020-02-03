package com.group5.quacker.controllers;

import com.group5.quacker.entities.User;
import com.group5.quacker.repositories.QuackRepository;
import com.group5.quacker.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class UserController {
    @Autowired
    UserRepository userRepository;

    @Autowired
    QuackRepository quackRepository;

    @RequestMapping(value = "/user/{name}", method = RequestMethod.GET)
    public String userPageGet(@PathVariable("name") String name, Model model) {

        User user = userRepository.findByName(name);
        if(user==null)
            return "redirect:/";

        model.addAttribute("user", user);

        if (user.getProfilePhoto() != null) {
            model.addAttribute("profilePhotoUrl", "/files/" + user.getProfilePhoto().getPublicId());
        }

        model.addAttribute("userquacks", quackRepository.findByPoster(user));

        return "userpage";
    }

}
