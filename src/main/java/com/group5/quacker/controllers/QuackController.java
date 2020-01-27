package com.group5.quacker.controllers;

import com.group5.quacker.entities.Quack;
import com.group5.quacker.entities.User;
import com.group5.quacker.repositories.QuackRepository;
import com.group5.quacker.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class QuackController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    QuackRepository quackRepository;

    @RequestMapping(value = "/quack", method = RequestMethod.POST)
    public String newQuack(@RequestParam String message) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        User poster = userRepository.findByName(auth.getName());
        if(poster == null)
            return "redirect:/login";

        Quack newQuack = new Quack();
        newQuack.setPoster(poster);
        newQuack.setQuackMessage(message);
        quackRepository.save(newQuack);

        return "redirect:/";
    }


}
