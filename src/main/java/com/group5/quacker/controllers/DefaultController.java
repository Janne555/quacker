package com.group5.quacker.controllers;

import com.group5.quacker.entities.Quack;
import com.group5.quacker.entities.User;
import com.group5.quacker.repositories.QuackRepository;
import com.group5.quacker.repositories.UserRepository;
import com.group5.quacker.services.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.lang.reflect.Array;
import java.util.*;

@Controller
public class DefaultController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    QuackRepository quackRepository;

    @Autowired
    AccountService accountService;

    @RequestMapping(value = {"/index", "/index/", "/"}, method = RequestMethod.GET)
    public String pageRootGet(Model model, String displayQuacks) {
        User user = accountService.currentUser();
        model.addAttribute("username", user.getName());

        if (user == null) {
            return "redirect/login";
        }

        model.addAttribute("user", user);
        model.addAttribute("latestQuackView", user.getLatestQuackView());
        user.setLatestQuackView(new Date());
        userRepository.save(user);

        if (user.getProfilePhoto() != null) {
            model.addAttribute("profilePhotoHead", "/files/" + user.getProfilePhoto().getPublicId());
        }

        if (displayQuacks != null) {

            switch (displayQuacks) {
                case "followed":        // Vain seurattujen käyttäjien quackit näkyviin
                    List<User> following = user.getFollowing();
                    ArrayList<Quack> quacks = new ArrayList();
                    for (User followedUser : following) {
                        quacks.addAll(followedUser.getQuacks());
                    }

                    Collections.sort(quacks, new Comparator<Quack>() {
                        public int compare(Quack o1, Quack o2) {
                            return o1.getDatePosted().compareTo(o2.getDatePosted());
                        }
                    });

                    Collections.reverse(quacks);        // Uusin ylhäällä

                    if (quacks.size() >= 10) {            // Vain 10 viimeistä quackia sivulle
                        model.addAttribute("quacks", quacks.subList(0, 10));
                    } else {
                        model.addAttribute("quacks", quacks);
                    }

                    model.addAttribute("quackView", "followed");    // All/Followed napin highlightausta varten

                    break;

                default:            // Kaikki quackit näkyviin
                    ArrayList<Quack> quacksDefault = new ArrayList();

                    quacksDefault.addAll(quackRepository.findAll());

                    quacksDefault.removeIf(quack -> user.getBlocked().contains(quack.getPoster())); // poista blokattujen käyttäjien quackit feedistä

                    Collections.sort(quacksDefault, new Comparator<Quack>() {
                        public int compare(Quack o1, Quack o2) {
                            return o1.getDatePosted().compareTo(o2.getDatePosted());
                        }
                    });

                    Collections.reverse(quacksDefault);        // Uusin ylhäällä

                    if (quacksDefault.size() >= 10) {            // Vain 10 viimeistä quackia sivulle
                        model.addAttribute("quacks", quacksDefault.subList(0, 10));
                    } else {
                        model.addAttribute("quacks", quacksDefault);
                    }
                    break;
            }
        } else {
            ArrayList<Quack> quacksDefault = new ArrayList();

            quacksDefault.addAll(quackRepository.findAll());

            quacksDefault.removeIf(quack -> user.getBlocked().contains(quack.getPoster())); // poista blokattujen käyttäjien quackit feedistä

            Collections.sort(quacksDefault, new Comparator<Quack>() {
                public int compare(Quack o1, Quack o2) {
                    return o1.getDatePosted().compareTo(o2.getDatePosted());
                }
            });

            Collections.reverse(quacksDefault);        // Uusin ylhäällä

            if (quacksDefault.size() >= 10) {            // Vain 10 viimeistä quackia sivulle
                model.addAttribute("quacks", quacksDefault.subList(0, 10));
            } else {
                model.addAttribute("quacks", quacksDefault);
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
