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
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * This is the controller for quack related endpoints
 */
@Controller
public class QuackController {

    /**
     * Autowired reference to the user repository
     */
    @Autowired
    UserRepository userRepository;

    /**
     * Autowired reference to the quack repository
     */
    @Autowired
    QuackRepository quackRepository;

    /**
     * Autowired reference to the file service
     */
    @Autowired
    FileService fileService;

    /**
     * Post mapping for /quack. New quacks are posted with this endpoint.
     *
     * @param message Quack message to be posted
     * @param file    (optional) File attached to quack
     * @return Redirect to /
     * @throws IOException
     */
    @RequestMapping(value = "/quack", method = RequestMethod.POST)
    public String newQuack(@RequestParam String message, @RequestParam(value = "publicClassification", defaultValue = "false") boolean checkbox,@RequestParam(value = "file", required = false) MultipartFile file, User poster, Model model) throws IOException {
        if (poster == null)     // Check that the user actually exists in the database
            return "redirect:/login";

        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

        FileMap attachment = file.isEmpty() ? null : fileService.storeFile(file);     // If a file was supplied then store it

        Quack newQuack = new Quack();           // new quack object
        newQuack.setPoster(poster);             // set the posters name
        newQuack.setQuackMessage(message);      // set the message
        newQuack.setDatePosted(new Date());     // set date posted as Date object
        newQuack.setFormattedDate(dateFormat.format(newQuack.getDatePosted())); // set date posted as formatted string
        newQuack.setAttachment(attachment);     // set attachment
        quackRepository.save(newQuack);         // save the quack
        poster.addQuack(newQuack);              // add the quack to the posting users quacks
        userRepository.save(poster);            // save the user
        
        if(checkbox) {				//set if quack is public or not
        	newQuack.setPublicClassification(true);
        }	

        return "redirect:/";
    }

    /**
     * This endpoint provides the like functionality for a quack.
     *
     * @param id Id value of the quack that will be liked
     * @return Redirect to the users page who had posted the quack to be liked
     */
    @RequestMapping(value = "/like/{id}", method = RequestMethod.GET)
    public String newQuack(@PathVariable("id") long id, HttpServletRequest request, User liker) {
        if (liker == null) // check that the user liking a quack actually exists in the database
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

    /**
     * Mapping to delete a quack
     *
     * @param id
     * @param request
     * @return
     */
    @DeleteMapping("/quack/{id}")
    public ResponseEntity deleteQuack(@PathVariable("id") long id, HttpServletRequest request, User user) {
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

    /**
     * Mapping to list quacks
     */
    @RequestMapping(value = {"/quack/", "/quacks", "/quacks"}, method = RequestMethod.GET)
    public String quackPageGet(Model model, User user) {
        if (user == null)
            return "redirect:/login";

        model.addAttribute("user", user);

        if (user.getProfilePhoto() != null) {
            model.addAttribute("profilePhotoHead", "/files/" + user.getProfilePhoto().getPublicId());
        }

        List<Quack> quacks = quackRepository.findAll();

        model.addAttribute("quacks", quacks);

        return "quack-search";
    }

    /**
     * Request mapping for searching quacks
     * @param model Model for variables to be passed to the thymeleaf template
     * @param loggedUser Reference to the logged in user
     * @return Returns a thymeleaf template
     */
    @GetMapping("/quack/search")
    public String searchForm(Model model, User loggedUser) {
        if (loggedUser == null)
            return "redirect:/login";

        model.addAttribute("user", loggedUser);

        if (loggedUser.getProfilePhoto() != null) {
            model.addAttribute("profilePhotoHead", "/files/" + loggedUser.getProfilePhoto().getPublicId());
        }
        return "search";
    }

    /**
     * Request mapping for the result of a quack search
     * @param model Model for variables to be passed to the thymeleaf template
     * @param loggedUser Reference to the logged in user
     * @return Returns a thymeleaf template
     */
    @RequestMapping(value = {"/quack/search/result"}, method = RequestMethod.POST)
    public String searchSubmitted(@RequestParam("quackSearch") String search, Model model, User loggedUser) {
        if (loggedUser == null)
            return "redirect:/login";

        model.addAttribute("user", loggedUser);

        List<Quack> quacks = quackRepository.findByQuackMessageContaining(search);

        model.addAttribute("quacks", quacks);

        if (loggedUser.getProfilePhoto() != null) {
            model.addAttribute("profilePhotoHead", "/files/" + loggedUser.getProfilePhoto().getPublicId());
        }
        return "quack-search-result";
    }
    
    /**
     * Mapping for public quacks
     * @param model For listing public quacks
     * @return page with public quacks
     */
	@RequestMapping(value = {"/publicQuacks"}, method = RequestMethod.GET)
    public String publicQuacks(Model model) {

    	List<Quack> publicQuacks = quackRepository.findByPublicClassificationTrue();

    	model.addAttribute("publicQuacks", publicQuacks);
    	
        return "public-quacks";
    }
}
