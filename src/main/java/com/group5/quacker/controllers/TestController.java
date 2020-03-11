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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Controller
public class TestController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    QuackRepository quackRepository;

    @RequestMapping(value = {"/{display}", "/index", "/index/", "/"}, method = RequestMethod.GET)
    public String pageRootGet(Model model,
                              @PathVariable(value = "display", required = false) String displayQuacks) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("User: \"" + auth.getName() + "\" got the index page.");
        model.addAttribute("username", auth.getName());

        
        User user = userRepository.findByName(auth.getName());
        model.addAttribute("user", user);
        if(user!=null && displayQuacks != null) {
            switch(displayQuacks) {
                case "followed":
                    List<User> following = user.getFollowing();
                    ArrayList<Quack> quacks = new ArrayList();
                    for(User followedUser : following) {
                        quacks.addAll(followedUser.getQuacks());
                    }

                    Collections.sort(quacks, new Comparator<Quack>() {
                        public int compare(Quack o1, Quack o2) {
                            return o1.getDatePosted().compareTo(o2.getDatePosted());
                        }
                    });

                    Collections.reverse(quacks);        // Uusin ylhäällä


                    model.addAttribute("quacks", quacks);

                    if (user.getProfilePhoto() != null) {
                        model.addAttribute("profilePhotoHead", "/files/" + user.getProfilePhoto().getPublicId());
                    }
                    break;

                default:
                    ArrayList<Quack> quacksDefault = new ArrayList();

                    quacksDefault.addAll(quackRepository.findAll());

                    Collections.sort(quacksDefault, new Comparator<Quack>() {
                        public int compare(Quack o1, Quack o2) {
                            return o1.getDatePosted().compareTo(o2.getDatePosted());
                        }
                    });

                    Collections.reverse(quacksDefault);        // Uusin ylhäällä

                    model.addAttribute("quacks", quacksDefault);

                    break;
            }
        } else {
            ArrayList<Quack> quacksDefault = new ArrayList();

            quacksDefault.addAll(quackRepository.findAll());

            Collections.sort(quacksDefault, new Comparator<Quack>() {
                public int compare(Quack o1, Quack o2) {
                    return o1.getDatePosted().compareTo(o2.getDatePosted());
                }
            });

            Collections.reverse(quacksDefault);        // Uusin ylhäällä

            model.addAttribute("quacks", quacksDefault);
        }

        /*
        if(quackRepository.count()>5)
            model.addAttribute("quacks", quackRepository.findAll().subList((int)quackRepository.count()-5, (int)quackRepository.count()));
        else
            model.addAttribute("quacks", quackRepository.findAll());*/

        return "index";
    }
}
