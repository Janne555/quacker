package com.group5.quacker.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class ErrorController {
    @RequestMapping(value = "/login-error", method = RequestMethod.GET)
    public String loginErrorGet() {
        return "login-error";
    }
}
