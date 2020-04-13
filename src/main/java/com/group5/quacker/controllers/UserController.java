package com.group5.quacker.controllers;

import com.group5.quacker.entities.User;
import com.group5.quacker.repositories.QuackRepository;
import com.group5.quacker.repositories.UserRepository;
import com.group5.quacker.services.AccountService;
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

/**
 * This is the controller for user related endpoints
 */
@Controller
public class UserController {
    @Autowired
    UserRepository userRepository;

    @Autowired
    QuackRepository quackRepository;

    @Autowired
    AccountService accountService;

    /**
     * This is the GET mapping for a users page.
     *
     * @param name  Name of the user
     * @param model Model for populating the thymeleaf template
     * @return Returns the thymeleaf template for a users page
     */
    @RequestMapping(value = "/user/{name}", method = RequestMethod.GET)
    public String userPageGet(@PathVariable("name") String name, Model model) {
        User loggedUser = accountService.currentUser();   // check that the logged in user exists in the database
        if (loggedUser == null)
            return "redirect:/login";

        User user = userRepository.findByName(name);                    // if the user was not found, search for users containing the supplied name value
        if (user == null) {
            List<User> users = userRepository.findByNameContaining(name);

            model.addAttribute("users", users);
            return "user-search";
        }

        model.addAttribute("user", loggedUser);               // populate the model with the user models
        model.addAttribute("watchedUser", user);

        if (user.getProfilePhoto() != null) {                           // populate model with the profile photo
            model.addAttribute("profilePhotoUrl", "/files/" + user.getProfilePhoto().getPublicId());
        }
        if (loggedUser.getProfilePhoto() != null) {
            model.addAttribute("profilePhotoHead", "/files/" + loggedUser.getProfilePhoto().getPublicId());
        }


        if (user.getFollowers().contains(user)) {   // check if the viewing user has followed the user
            model.addAttribute("isFollowed", true);
        }

        if (user.getBlocked().contains(user)) {       // check if the viewing user has blocked the user
            model.addAttribute("isBlocked", true);
        }

        model.addAttribute("userquacks", quackRepository.findByPoster(user));       // populate model with the users quacks

        return "userpage";
    }

    /**
     * GET mapping for following users
     *
     * @param name  Name of the user to be followed
     * @param model Model for populating the thyeleaf template
     * @return Redirects to the users page that was followed
     */
    @RequestMapping(value = "/follow/{name}", method = RequestMethod.GET)
    public String userFollow(@PathVariable("name") String name, Model model) {
        User follower = accountService.currentUser();      // check that the logged in user actually exists in the database
        if (follower == null)
            return "redirect:/login";

        User user = userRepository.findByName(name);                    // check that the user to be followed actually exists
        if (user == null) {
            return "redirect:/";
        }

        // check that the user has not already followed the other user and that the user is not trying to follow himself
        if (!follower.getFollowing().contains(user) && !user.getFollowers().contains(follower) && !user.equals(follower)) {
            follower.addFollowing(user);
            user.addFollower(follower);

            if (follower.getBlocked().contains(user)) {  // If the user is following a blocked user, unblock that user
                follower.removeBlocked(user);
            }

            userRepository.save(follower);
            userRepository.save(user);
        }

        return "redirect:/user/" + name;
    }

    @RequestMapping(value = "/unfollow/{name}", method = RequestMethod.GET)
    public String userUnfollow(@PathVariable("name") String name, Model model) {
        User follower = accountService.currentUser();      // check that the logged in user actually exists in the database
        if (follower == null)
            return "redirect:/login";

        User user = userRepository.findByName(name);                    // check that the user to be unfollowed actually exists
        if (user == null) {
            return "redirect:/";
        }

        // check that the user has already followed the other user and that the user is not trying to unfollow himself
        if (follower.getFollowing().contains(user) && user.getFollowers().contains(follower) && !user.equals(follower)) {
            follower.removeFollowing(user);
            user.removeFollower(follower);
            userRepository.save(follower);
            userRepository.save(user);
        }

        return "redirect:/user/" + name;
    }

    /**
     * GET mapping for blocking & unblocking users
     *
     * @param name  Name of the user to be blocked
     * @param model Model for populating the thyeleaf template
     * @return Redirects to the users page that was followed
     */
    @RequestMapping(value = "/block/{name}", method = RequestMethod.GET)
    public String userBlock(@PathVariable("name") String name, Model model) {
        User blocker = accountService.currentUser();      // check that the logged in user actually exists in the database
        if (blocker == null)
            return "redirect:/login";

        User user = userRepository.findByName(name);                    // check that the user to be blocked actually exists
        if (user == null) {
            return "redirect:/";
        }

        // check that the user has not already blocked the other user and that the user is not trying to block himself
        if (!blocker.getBlocked().contains(user) && !blocker.equals(user)) {
            blocker.addBlocked(user);

            if (blocker.getFollowing().contains(user)) {     // Remove following status when blocking a user
                blocker.removeFollowing(user);
                user.removeFollower(blocker);
                userRepository.save(user);
            }

            userRepository.save(blocker);
        } else if (blocker.getBlocked().contains(user) && !blocker.equals(user)) {
            blocker.removeBlocked(user);
            userRepository.save(blocker);
        }


        return "redirect:/user/" + name;
    }

    /**
     * Mapping to list users
     *
     * @param model Model to populate the thymeleaf template
     * @return returns a list of users
     */
    @RequestMapping(value = {"/user/", "/users/", "/user", "/users"}, method = RequestMethod.GET)
    public String userPageGet(Model model) {
        User user = accountService.currentUser();
        if (user == null)
            return "redirect:/login";
        
        model.addAttribute("user", user);

        List<User> users = userRepository.findAll();

        model.addAttribute("users", users);

        if (user.getProfilePhoto() != null) {
            model.addAttribute("profilePhotoHead", "/files/" + user.getProfilePhoto().getPublicId());
        }


        return "user-search";

    }


}
