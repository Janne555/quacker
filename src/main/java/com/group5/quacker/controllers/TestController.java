package com.group5.quacker.controllers;

import com.group5.quacker.entities.Quack;
import com.group5.quacker.entities.User;
import com.group5.quacker.repositories.QuackRepository;
import com.group5.quacker.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.List;

@Controller
public class TestController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    QuackRepository quackRepository;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String pageRootGet(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("User: \"" + auth.getName() + "\" got the index page.");
        model.addAttribute("username", auth.getName());

        
        User user = userRepository.findByName(auth.getName());
        model.addAttribute("user", user);
        if(user!=null) {
            List<User> following = user.getFollowing();
            ArrayList<Quack> quacks = new ArrayList();
            for(User followedUser : following) {
                quacks.addAll(followedUser.getQuacks());
            }

            model.addAttribute("quacks", quacks);
            
            if (user.getProfilePhoto() != null) {
                model.addAttribute("profilePhotoHead", "/files/" + user.getProfilePhoto().getPublicId());
            }

        }

        /*
        if(quackRepository.count()>5)
            model.addAttribute("quacks", quackRepository.findAll().subList((int)quackRepository.count()-5, (int)quackRepository.count()));
        else
            model.addAttribute("quacks", quackRepository.findAll());*/

        return "index";
    }
}
