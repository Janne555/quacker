package com.group5.quacker.controllers;

import com.group5.quacker.entities.FileMap;
import com.group5.quacker.entities.Quack;
import com.group5.quacker.entities.User;
import com.group5.quacker.repositories.QuackRepository;
import com.group5.quacker.repositories.UserRepository;
import com.group5.quacker.services.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

@Controller
public class QuackController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    QuackRepository quackRepository;

    @Autowired
    FileService fileService;

    @RequestMapping(value = "/quack", method = RequestMethod.POST)
    public String newQuack(@RequestParam String message, @RequestParam(value = "file", required = false) MultipartFile file) throws IOException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        User poster = userRepository.findByName(auth.getName());
        if (poster == null)
            return "redirect:/login";

        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

        FileMap attachment = file != null ? fileService.storeFile(file) : null;

        Quack newQuack = new Quack();
        newQuack.setPoster(poster);
        newQuack.setQuackMessage(message);
        newQuack.setDatePosted(new Date());
        newQuack.setFormattedDate(dateFormat.format(newQuack.getDatePosted()));
        newQuack.setAttachment(attachment);
        quackRepository.save(newQuack);
        poster.addQuack(newQuack);
        userRepository.save(poster);

        return "redirect:/";
    }

    @RequestMapping(value = "/like/{id}", method = RequestMethod.GET)
    public String newQuack(@PathVariable("id") long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        User liker = userRepository.findByName(auth.getName());
        if (liker == null)
            return "redirect:/login";


        Quack quack = quackRepository.findById(id);
        if (quack == null) {
            return "redirect:/";
        }

        if (quack.getPoster().equals(liker)) {
            return "redirect:/user/" + quack.getPoster().getName();
        }

        if (!quack.getLikers().contains(liker)) {
            quack.addLiker(liker);
            quackRepository.save(quack);
        } else {
            quack.removeLiker(liker);
            quackRepository.save(quack);
        }

        return "redirect:/user/" + quack.getPoster().getName();
    }


}
