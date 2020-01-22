package com.group5.quacker.controllers;

import com.group5.quacker.dtos.UserDto;
import com.group5.quacker.entities.User;
import com.group5.quacker.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;

@Controller
public class RegistrationController {
    @Autowired
    private UserService service;

    @RequestMapping(value = "/registration", method = RequestMethod.GET)
    public String showRegistrationForm(WebRequest request, Model model) {
        UserDto userDto = new UserDto();
        model.addAttribute("user", userDto);
        return "register";
    }

    @RequestMapping(value = "/registration", method = RequestMethod.POST)
    public ModelAndView registerUserAccount(
            @ModelAttribute("user") @Valid UserDto accountDto,
            BindingResult result, WebRequest request, Errors errors) {

        if (!result.hasErrors()) {
            service.registerNewUserAccount(accountDto);
        } else {
            return new ModelAndView("register", "user", accountDto);
        }
        return new ModelAndView("registrationSuccess", "user", accountDto);
    }

}