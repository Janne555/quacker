package com.group5.quacker.controllers;

import com.group5.quacker.entities.FileMap;
import com.group5.quacker.entities.Quack;
import com.group5.quacker.entities.User;
import com.group5.quacker.repositories.QuackRepository;
import com.group5.quacker.repositories.UserRepository;
import com.group5.quacker.services.AccountService;
import com.group5.quacker.services.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * This is the controller for quack related endpoints
 */
@Controller
public class QuackController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    QuackRepository quackRepository;

    @Autowired
    FileService fileService;

    @Autowired
    AccountService accountService;

    /**
     * Post mapping for /quack. New quacks are posted with this endpoint.
     *
     * @param message Quack message to be posted
     * @param file    (optional) File attached to quack
     * @return Redirect to /
     * @throws IOException
     */
    @RequestMapping(value = "/quack", method = RequestMethod.POST)
    public String newQuack(@RequestParam String message, @RequestParam(value = "file", required = false) MultipartFile file) throws IOException {

        User poster = accountService.currentUser();    // Check that the user actually exists in the database
        if (poster == null)
            return "redirect:/login";

        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

        FileMap attachment = file != null ? fileService.storeFile(file) : null;     // If a file was supplied then store it

        Quack newQuack = new Quack();           // new quack object
        newQuack.setPoster(poster);             // set the posters name
        newQuack.setQuackMessage(message);      // set the message
        newQuack.setDatePosted(new Date());     // set date posted as Date object
        newQuack.setFormattedDate(dateFormat.format(newQuack.getDatePosted())); // set date posted as formatted string
        newQuack.setAttachment(attachment);     // set attachment
        quackRepository.save(newQuack);         // save the quack
        poster.addQuack(newQuack);              // add the quack to the posting users quacks
        userRepository.save(poster);            // save the user

        return "redirect:/";
    }

    /**
     * This endpoint provides the like functionality for a quack.
     *
     * @param id Id value of the quack that will be liked
     * @return Redirect to the users page who had posted the quack to be liked
     */
    @RequestMapping(value = "/like/{id}", method = RequestMethod.GET)
    public String newQuack(@PathVariable("id") long id, HttpServletRequest request) {
        User liker = accountService.currentUser();     // check that the user liking a quack actually exists in the database
        if (liker == null)
            return "redirect:/login";


        Quack quack = quackRepository.findById(id);                 // check that the quack to be liked actually exists in the database
        if (quack == null) {
            return "redirect:/";
        }

        if (quack.getPoster().equals(liker)) {                      // Users can't like their own quacks
            return "redirect:/user/" + quack.getPoster().getName();
        }

        if (!quack.getLikers().contains(liker)) {                   // check that the user has not already liked the quack
            quack.addLiker(liker);
            quackRepository.save(quack);
        } else {
            quack.removeLiker(liker);                               // if the user had liked the quack already, then unlike the quack
            quackRepository.save(quack);
        }

        System.out.println(request.getRequestURI());
        return "redirect:/";
    }

    @DeleteMapping("/quack/{id}")
    public ResponseEntity deleteQuack(@PathVariable("id") long id, HttpServletRequest request) {
        User user = accountService.currentUser();
        if (user == null) {
            return new ResponseEntity(HttpStatus.UNAUTHORIZED);
        }

        Quack byId = quackRepository.findById(id);

        if (byId == null) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }

        if (!byId.getPoster().equals(user)) {
            return new ResponseEntity(HttpStatus.FORBIDDEN);
        }

        quackRepository.delete(byId);

        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }
}
